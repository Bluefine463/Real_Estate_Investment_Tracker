package com.example.servlet;
import com.mongodb.client.MongoCollection;
import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@WebServlet("/GetPropertiesId")
public class GetPropertiesId extends HttpServlet{
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException{

        MongoCollection<Document> mc = MongoDBConnection.getCollection("Properties");

        String type = request.getParameter("type");
        String username =""; 
        HttpSession session = request.getSession(false);  // just reference to call
        if (session != null && session.getAttribute("username") != null) {
            username = (String) session.getAttribute("username");
        } 

        FindIterable<Document> docs = mc.find(and(eq("type",type),eq("username",username)));

        List<Object> Ids = new ArrayList<>();
        for (Document doc : docs) {
            Ids.add(doc.get("_id"));
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        if (!Ids.isEmpty()) {
            out.write(new Gson().toJson(Ids));
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.println("{\"error\": \"No properties found with the given type\"}");
        }


    } 

}
