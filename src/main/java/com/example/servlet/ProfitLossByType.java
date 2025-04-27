package com.example.servlet;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

@WebServlet("/ProfitLossByType")
public class ProfitLossByType extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        DecimalFormat df = new DecimalFormat("0.00");

        // Fetching the username from session
        String username = (String) request.getSession().getAttribute("username");
        String type = request.getParameter("type");

        if (username == null || type == null) {
            out.write("{\"status\":\"error\", \"message\":\"Missing parameters\"}");
            return;
        }

        MongoCollection<Document> collection = MongoDBConnection.getCollection("Properties");

        List<Document> properties = collection.find(Filters.and(
                Filters.eq("username", username),
                Filters.eq("type", type)
        )).into(new java.util.ArrayList<>());

        double totalProfitLoss = 0.0;
        double totalInvestment = 0.0;
        double totalIncome = 0.0;
        double totalExpenses = 0.0;
        double totalGain = 0.0;

        for (Document property : properties) {
            
            double principal = property.get("principalAmount") instanceof Integer ? 
                    ((Integer) property.get("principalAmount")).doubleValue() : 
                    property.getDouble("principalAmount");
            
            // double appreciationRate = property.get("appreciationRate") instanceof Integer ? 
            //         ((Integer) property.get("appreciationRate")).doubleValue() : 
            //         property.getDouble("appreciationRate");
            double pricePerSqft     = property.get("pricePerSqft", Double.class);
            
            double monthlyIncome = property.get("income") instanceof Integer ? 
                    ((Integer) property.get("income")).doubleValue() : 
                    property.getDouble("income");
            
            Date purchaseDate = property.getDate("purchaseDate");
            long yearsHeld = (new Date().getTime() - purchaseDate.getTime()) / (1000L * 60 * 60 * 24 * 365);

            // Appreciation
            //double currentValue = principal * Math.pow(appreciationRate, yearsHeld);
            double sizeSqft     = Double.parseDouble(property.getString("size"));
            double currentValue = pricePerSqft * sizeSqft;


            // Income calculation
            double propertyTotalIncome = monthlyIncome * 12 * yearsHeld;
            totalIncome += propertyTotalIncome;

            // Expenses calculation
            List<Document> expenses = (List<Document>) property.get("Expenses");
            double propertyTotalExpenses = 0.0;
            
            for (Document expense : expenses) {
                propertyTotalExpenses += expense.get("amount") instanceof Integer ? 
                        ((Integer) expense.get("amount")).doubleValue() : 
                        expense.getDouble("amount");
            }
            totalExpenses += propertyTotalExpenses;

            // Profit/Loss Calculation
            double profitLoss = currentValue + propertyTotalIncome - propertyTotalExpenses - principal;
            totalProfitLoss += profitLoss;
            totalInvestment += principal;
            totalGain += currentValue;
        }

        double profitLossPercentage = (totalProfitLoss / totalInvestment) * 100;

        // Output JSON
        out.write("{\"totalInvestment\":" + df.format(totalInvestment) +
                  ", \"totalProfitLoss\":" + df.format(totalProfitLoss) +
                  ", \"profitLossPercentage\":" + df.format(profitLossPercentage) +
                  ", \"totalIncome\":" + df.format(totalIncome) +
                  ", \"totalExpenses\":" + df.format(totalExpenses) +
                  ", \"totalGain\":" + df.format(totalGain) + "}");
        out.close();
    }
}