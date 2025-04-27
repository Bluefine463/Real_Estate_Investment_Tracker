package com.example.servlet;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/UpdateProperty")
public class UpdateProperty extends HttpServlet {

    // Utility to safely trim
    private String safeTrim(String val) {
        return val != null ? val.trim() : "";
    }

    // Utility to safely parse double
    private double safeParseDouble(String val) {
        try {
            return val != null && !val.trim().isEmpty() ? Double.parseDouble(val.trim()) : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    // Utility to safely parse int
    private int safeParseInt(String val) {
        try {
            return val != null && !val.trim().isEmpty() ? Integer.parseInt(val.trim()) : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        MongoCollection<Document> mc = MongoDBConnection.getCollection("Properties");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        Map<String, String> result = new HashMap<>();

        try {
            // Get UID
            String uid = safeTrim(request.getParameter("uid"));
            if (uid.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.put("error", "UID is required");
                out.println(gson.toJson(result));
                return;
            }

            // Extract and safely handle parameters
            String name = safeTrim(request.getParameter("name"));
            String type = safeTrim(request.getParameter("type"));
            String location = safeTrim(request.getParameter("location"));
            String size = safeTrim(request.getParameter("size"));

            double principalAmount = safeParseDouble(request.getParameter("principalAmount"));
            //double currentEstimatedPrice = safeParseDouble(request.getParameter("currentEstimatedPrice"));
            //int appreciationRate = safeParseInt(request.getParameter("appreciationRate"));
            double pricePerSqft = safeParseDouble(request.getParameter("pricePerSqft"));
            double income = safeParseDouble(request.getParameter("income"));

            // Calculate Profit/Loss
            //double pl = currentEstimatedPrice - principalAmount;
            // size is a string parameter
            double sizeSqft = safeParseDouble(request.getParameter("size"));
            double pl       = pricePerSqft * sizeSqft - principalAmount;
            // Parse purchase date
            Date purchaseDate = null;
            String purchaseDateStr = safeTrim(request.getParameter("purchaseDate"));
            if (!purchaseDateStr.isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    purchaseDate = sdf.parse(purchaseDateStr);
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    result.put("error", "Invalid date format. Use yyyy-MM-dd.");
                    out.println(gson.toJson(result));
                    return;
                }
            }

            // Prepare updated document
            Document updatedDoc = new Document("name", name)
                    .append("type", type)
                    .append("location", location)
                    .append("size", size)
                    .append("principalAmount", principalAmount)
                    //.append("currentEstimatedPrice", currentEstimatedPrice)
                    .append("income", income)
                    //.append("appreciationRate", appreciationRate)
                    .append("pricePerSqft", pricePerSqft)
                    .append("purchaseDate", purchaseDate)
                    .append("pl", pl);

            // Perform update
            mc.updateOne(Filters.eq("_id", uid), new Document("$set", updatedDoc));

            result.put("message", "Property updated successfully");
            out.println(gson.toJson(result));

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.put("error", "Failed to update property: " + e.getMessage());
            out.println(gson.toJson(result));
        }
    }
}
