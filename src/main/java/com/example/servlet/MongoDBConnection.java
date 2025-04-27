package com.example.servlet;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;


public class MongoDBConnection {
    private static MongoDatabase database;

    private MongoDBConnection(){}

    public static MongoCollection<Document> getCollection(String collection){
            return MongoDBConnection.getDatabase().getCollection(collection);
            //return database.getCollection(collection);
    }

    public static MongoDatabase getDatabase() {
        String uri = ""; // setup the mongodb atlas cloud and copy paste the connection code for java
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "error");

        try{
                MongoClient mc = MongoClients.create(uri);
                database = mc.getDatabase("Rental_db");
                
        }catch(Exception e){
                System.out.println("MongoDB Exception: "+e);
        }
        return database;

    }
}

