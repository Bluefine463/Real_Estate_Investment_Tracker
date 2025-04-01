package com.example.servlet;
import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import static com.mongodb.client.model.Filters.eq;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;


@WebServlet("/GetProperty")
public class GetProperty extends HttpServlet{
    @Override
    protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{

        MongoCollection<Document> mc = MongoDBConnection.getCollection("Properties");

        String uid = request.getParameter("uid");
        Document doc = mc.find(eq("_id",uid)).first();
       

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
  
        if(doc != null ){
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date purchaseDate = (Date) doc.get("purchaseDate");
    
            String formattedDate = outputFormat.format(purchaseDate);
            doc.put("purchaseDate", formattedDate);
            String jsonDoc = new Gson().toJson(doc);
            out.write(jsonDoc);
        }else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.println("{\"error\": \"Property not found\"}");
        }
        
    }   
}
