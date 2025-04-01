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
import com.google.gson.Gson;

@WebServlet("/ProfitLossByUsername")
public class ProfitLossByUsername extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Fetching the username from session
        String username = (String) request.getSession().getAttribute("username");
        
        if (username == null) {
            out.write("{\"status\":\"error\", \"message\":\"User not logged in\"}");
            return;
        }

        MongoCollection<Document> collection = MongoDBConnection.getCollection("Properties");

        List<Document> properties = collection.find(Filters.eq("username", username)).into(new java.util.ArrayList<>());

        double totalProfitLoss = 0.0;
        double totalInvestment = 0.0;
        double totalIncome = 0.0;

        for (Document property : properties) {
            
            double principal = property.get("principalAmount") != null ? ((Number) property.get("principalAmount")).doubleValue() : 0.0;
            double appreciationRate = property.get("appreciationRate") != null ? ((Number) property.get("appreciationRate")).doubleValue() : 0.0;
            double monthlyIncome = property.get("income") != null ? ((Number) property.get("income")).doubleValue() : 0.0;
            
            Date purchaseDate = property.getDate("purchaseDate");
            long yearsHeld = (new Date().getTime() - purchaseDate.getTime()) / (1000L * 60 * 60 * 24 * 365);

            // Appreciation
            double currentValue = principal * Math.pow(appreciationRate, yearsHeld);

            // Income calculation
            double totalPropertyIncome = monthlyIncome * 12 * yearsHeld;
            totalIncome += totalPropertyIncome;

            // Expenses calculation
            List<Document> expenses = (List<Document>) property.get("Expenses");
            double totalExpenses = 0.0;
            
            for (Document expense : expenses) {
                totalExpenses += expense.get("amount") != null ? ((Number) expense.get("amount")).doubleValue() : 0.0;
            }

            // Profit/Loss Calculation
            double profitLoss = currentValue + totalPropertyIncome - totalExpenses - principal;

            totalProfitLoss += profitLoss;
            totalInvestment += principal;
        }

        double profitLossPercentage = (totalInvestment == 0) ? 0 : (totalProfitLoss / totalInvestment) * 100;
        double totalGain = totalProfitLoss + totalInvestment;

        // Formatting numbers to avoid scientific notation
        DecimalFormat df = new DecimalFormat("#.##");
        
        // Output JSON
        out.write("{\"totalInvestment\": " + df.format(totalInvestment) +
                  ", \"totalIncome\": " + df.format(totalIncome) +
                  ", \"totalProfitLoss\": " + df.format(totalProfitLoss) +
                  ", \"totalGain\": " + df.format(totalGain) +
                  ", \"profitLossPercentage\": " + df.format(profitLossPercentage) + "}");
        out.close();
    }
}
