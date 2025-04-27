package com.example.servlet;
import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;


import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.IOException;

@WebServlet("/AddExpenses")
public class AddExpenses extends HttpServlet{
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException{

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try{
        MongoCollection<Document> mc = MongoDBConnection.getCollection("Properties");

        String propid = request.getParameter("uid");
        Date expenseDate = null;
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            expenseDate = sdf.parse(request.getParameter("date"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String amountStr = request.getParameter("amount");
        double amount = (amountStr!=null && !amountStr.isEmpty())? Double.parseDouble(amountStr):0.0;
        String description = request.getParameter("description");

        if (propid == null || expenseDate == null || description == null) {
            out.write("{\"status\":\"error\", \"message\":\"Missing required fields\"}");
            return;
        }
        


        Document doc = new Document("expenseDate",expenseDate)
                        .append("amount",amount)
                        .append("description",description);

        long modcount = mc.updateOne(Filters.eq("_id",propid),Updates.push("Expenses",doc)).getModifiedCount();
        if(modcount>0){
            Boolean b = true;
            out.write(new Gson().toJson(b));
        }else{
            out.write("{\"status\":\"error\", \"message\":\"Property not found or update failed\"}");
        }
        }catch(Exception e){
            e.printStackTrace();
            out.write("{\"status\":\"error\", \"message\":\"Internal server error\"}");
        }finally{
            out.close();
        }
    }

}
