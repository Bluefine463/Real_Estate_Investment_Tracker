package com.example.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;

@WebServlet("/DeleteProperty")
public class DeleteProperty extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        MongoCollection<Document> mc = MongoDBConnection.getCollection("Properties");

        // Retrieve the UID from the frontend
        String uid = request.getParameter("uid");
        
        if (uid == null || uid.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"UID is required\"}");
            return;
        }

        // Delete the document with the given UID
        try {
            DeleteResult result = mc.deleteOne(Filters.eq("_id", uid));
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();

            if (result.getDeletedCount() > 0) {
                out.println(new Gson().toJson("{\"message\": \"Property deleted successfully\"}"));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.println(new Gson().toJson("{\"error\": \"Property not found\"}"));
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Failed to delete property\"}");
            e.printStackTrace();
        }
    }
}
