package com.example.servlet;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

import static com.mongodb.client.model.Filters.eq;
import javax.servlet.annotation.*;

@WebServlet("/login")
public class LoginServlet extends HttpServlet{
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        MongoCollection<Document> mc = MongoDBConnection.getCollection("Users");

        Document userDoc = mc.find(eq("username", username)).first();

        if (userDoc != null && userDoc.getString("password").equals(password)) {
                User user = new User(username, password);
                HttpSession session = request.getSession();
                session.setAttribute("username", user.getUsername());  

            Boolean b = true;
            response.getWriter().write(new Gson().toJson(b));
        } else {
            Boolean b = false;
            response.getWriter().write(new Gson().toJson(b));
        }
    }
}
