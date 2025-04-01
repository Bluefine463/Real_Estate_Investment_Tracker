package com.example.servlet;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.Date;

@WebServlet("/AddProperty")
public class AddProperty extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        MongoCollection<Document> mc = MongoDBConnection.getCollection("Properties");
        String username ="";
        HttpSession session = request.getSession(false);  // just reference to call
        if (session != null && session.getAttribute("username") != null) {
            username = (String) session.getAttribute("username");
        } 

        String name = request.getParameter("name");
        String type = request.getParameter("type");
        String location = request.getParameter("location");
        String size = request.getParameter("size");
        double principalAmount = Double.parseDouble(request.getParameter("principalAmount"));
        //double currentEstimatedPrice = Double.parseDouble(request.getParameter("currentEstimatedPrice"));
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

        String uniqueId = UUID.randomUUID().toString();

        Document doc = new Document("_id", uniqueId)
                .append("username", username)
                .append("name",name)
                .append("type", type)
                .append("location", location)
                .append("size",size)
                .append("principalAmount", principalAmount)
                //.append("currentEstimatedPrice", currentEstimatedPrice)
                .append("income", income)
                .append("appreciationRate", appreciationRate)
                .append("purchaseDate", purchaseDate)
                .append("pl", pl);

        mc.insertOne(doc);
        

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.println(new Gson().toJson(uniqueId));

    }
}
