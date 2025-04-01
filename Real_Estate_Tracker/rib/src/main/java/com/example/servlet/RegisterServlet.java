package com.example.servlet;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.servlet.annotation.*;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String phoneno = request.getParameter("phoneno");

        if (username == null || password == null || email == null) {
            response.getWriter().write("Missing parameters!");
            return;
        }

        User user = new User(username, password, email, phoneno);
        MongoCollection<Document> mc = MongoDBConnection.getCollection("Users");

        Document doc = new Document("username", user.getUsername())
                .append("password", user.getPassword())
                .append("email", user.getEmail())
                .append("phoneno",user.getPhoneno());


        
        try {
            mc.insertOne(doc);  // Insert document into MongoDB
            Boolean b = true;
            response.setContentType("application/json");
            response.getWriter().write(new Gson().toJson(b));
        } catch (Exception e) {
            e.printStackTrace();
            Boolean b = false;
            response.setContentType("application/json");
            response.getWriter().write(new Gson().toJson(b));
        }
    }
}

