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

@WebServlet("/ProfitLossByProperty")
public class ProfitLossByProperty extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String propertyId = request.getParameter("uid");

        if (propertyId == null) {
            out.write("{\"status\":\"error\", \"message\":\"Missing property ID\"}");
            return;
        }

        MongoCollection<Document> collection = MongoDBConnection.getCollection("Properties");
        Document property = collection.find(Filters.eq("_id", propertyId)).first();

        if (property == null) {
            out.write("{\"status\":\"error\", \"message\":\"Property not found\"}");
            return;
        }

        double principal = property.get("principalAmount") != null ? ((Number) property.get("principalAmount")).doubleValue() : 0.0;
        //double appreciationRate = property.get("appreciationRate") != null ? ((Number) property.get("appreciationRate")).doubleValue() : 0.0;
        double pricePerSqft     = property.get("pricePerSqft", Double.class);
        double monthlyIncome = property.get("income") != null ? ((Number) property.get("income")).doubleValue() : 0.0;
        
        Date purchaseDate = property.getDate("purchaseDate");
        long yearsHeld = (new Date().getTime() - purchaseDate.getTime()) / (1000L * 60 * 60 * 24 * 365);

        //double currentValue = principal * Math.pow(appreciationRate, yearsHeld);
        // size was stored as String, parse to double:
        double sizeSqft     = Double.parseDouble(property.getString("size"));
        double currentValue = pricePerSqft * sizeSqft;
        double totalIncome = monthlyIncome * 12 * yearsHeld;

        List<Document> expenses = (List<Document>) property.get("Expenses");
        double totalExpenses = 0.0;
        if(expenses != null){
        for (Document expense : expenses) {
            totalExpenses += expense.get("amount") != null ? ((Number) expense.get("amount")).doubleValue() : 0.0;
        }
        }

        double profitLoss = currentValue + totalIncome - totalExpenses - principal;
        double profitLossPercentage = (principal == 0) ? 0 : (profitLoss / principal) * 100;

        DecimalFormat df = new DecimalFormat("#.##");
        
        out.write("{\"currentValue\": " + df.format(currentValue) +
                  ", \"totalIncome\": " + df.format(totalIncome) +
                  ", \"totalExpenses\": " + df.format(totalExpenses) +
                  ", \"profitLoss\": " + df.format(profitLoss) +
                  ", \"profitLossPercentage\": " + df.format(profitLossPercentage) + "}");
        out.close();
    }
}
