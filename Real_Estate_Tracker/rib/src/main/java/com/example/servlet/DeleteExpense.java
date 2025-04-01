package com.example.servlet;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/DeleteExpense")
public class DeleteExpense extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        MongoCollection<Document> mc = MongoDBConnection.getCollection("Properties");

        // Extract parameters
        String uid = request.getParameter("uid");
        String indexStr = request.getParameter("index");

        if (uid == null || indexStr == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"status\":\"error\", \"message\":\"Missing required fields\"}");
            return;
        }

        int index;
        try {
            index = Integer.parseInt(indexStr);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"status\":\"error\", \"message\":\"Invalid index format\"}");
            return;
        }

        try {
            // Step 1: Unset the element at the given index (set it to null)
            UpdateResult unsetResult = mc.updateOne(
                    Filters.eq("_id", uid),
                    Updates.unset("Expenses." + index)
            );

            if (unsetResult.getModifiedCount() > 0) {
                // Step 2: Remove the `null` values from the Expenses array
                UpdateResult pullResult = mc.updateOne(
                        Filters.eq("_id", uid),
                        Updates.pull("Expenses", null)
                );

                if (pullResult.getModifiedCount() > 0) {
                    out.write("{\"status\":\"success\", \"message\":\"Expense deleted successfully\"}");
                } else {
                    out.write("{\"status\":\"warning\", \"message\":\"Null cleanup failed, but expense was unset\"}");
                }

            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write("{\"status\":\"error\", \"message\":\"Expense not found or already deleted\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"status\":\"error\", \"message\":\"Failed to delete expense\"}");
        } finally {
            out.close();
        }
    }
}
