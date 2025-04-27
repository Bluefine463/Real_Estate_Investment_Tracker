package com.example.servlet;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.FindIterable;
import org.bson.Document;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

import static com.mongodb.client.model.Filters.eq;

@WebServlet("/GetAllPropertiesByUsername")
public class GetAllPropertiesByUsername extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get username from session
        String username = (String) request.getSession().getAttribute("username");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        if (username == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.println("{\"error\": \"User not logged in\"}");
            return;
        }

        MongoCollection<Document> collection = MongoDBConnection.getCollection("Properties");

        // Get all documents with matching username
        FindIterable<Document> docs = collection.find(eq("username", username));
        ArrayList<Document> properties = new ArrayList<>();

        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Document doc : docs) {
            Date purchaseDate = doc.getDate("purchaseDate");
            if (purchaseDate != null) {
                doc.put("purchaseDate", outputFormat.format(purchaseDate));
            }
            properties.add(doc);
        }

        String json = new Gson().toJson(properties);
        out.write(json);
    }
}
