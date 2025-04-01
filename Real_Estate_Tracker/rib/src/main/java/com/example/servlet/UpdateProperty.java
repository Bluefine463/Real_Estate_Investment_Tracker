package com.example.servlet;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
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

@WebServlet("/UpdateProperty")
public class UpdateProperty extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        MongoCollection<Document> mc = MongoDBConnection.getCollection("Properties");

        // Get the UID from the frontend
        String uid = request.getParameter("uid");
        if (uid == null || uid.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"UID is required\"}");
            return;
        }

        // Get all the property details from the frontend
        String name = request.getParameter("name");
        String type = request.getParameter("type");
        String location = request.getParameter("location");
        String size = request.getParameter("size");

        double principalAmount = Double.parseDouble(request.getParameter("principalAmount"));
        double currentEstimatedPrice = Double.parseDouble(request.getParameter("currentEstimatedPrice"));
        int appreciationRate = Integer.parseInt(request.getParameter("appreciationRate"));

        String incomeStr = request.getParameter("income");
        double income = (incomeStr != null && !incomeStr.isEmpty()) ? Double.parseDouble(incomeStr) : 0.0;

        double pl = 100.0; 

        Date purchaseDate = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            purchaseDate = sdf.parse(request.getParameter("purchaseDate"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Prepare the updated document
        Document updatedDoc = new Document("name", name)
                .append("type", type)
                .append("location", location)
                .append("size", size)
                .append("principalAmount", principalAmount)
                .append("currentEstimatedPrice", currentEstimatedPrice)
                .append("income", income)
                .append("appreciationRate", appreciationRate)
                .append("purchaseDate", purchaseDate)
                .append("pl", pl);

        // Update the document in MongoDB
        try {
            mc.updateOne(Filters.eq("_id", uid), new Document("$set", updatedDoc));

            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.println(new Gson().toJson("{\"message\": \"Property updated successfully\"}"));

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Failed to update property\"}");
            e.printStackTrace();
        }
    }
}
