package com.example.servlet;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.eq;
import org.bson.Document;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.util.List;
import java.util.Date;

@WebServlet("/GetExpenses")
public class GetExpenses extends HttpServlet {

        @Override
        protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            
            MongoCollection<Document> mc = MongoDBConnection.getCollection("Properties");
            
            String uid = request.getParameter("uid");
            Document doc = mc.find(eq("_id",uid)).first();

            if(doc != null ){
               
                List<Document> exp =(List<Document>)doc.get("Expenses");
                SimpleDateFormat inputFormat = new SimpleDateFormat("MMM d, yyyy, hh:mm:ss a");
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

                for (Document expense : exp) {
                    String expenseDateStr = expense.getString("expenseDate");
                    
                    try {
                        // Parse and reformat the date
                        Date date = inputFormat.parse(expenseDateStr);
                        String formattedDate = outputFormat.format(date);

                        // Replace the original date with the formatted one
                        expense.put("expenseDate", formattedDate);

                    } catch (Exception e) {
                        e.printStackTrace();  // Handle date parsing errors
                    }
                }

                
                out.write(new Gson().toJson(exp));

                //Object exp = doc.get("Expenses");
                
            }else{
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write("{\"error\": \"Property not found\"}");
            }




        }

}
