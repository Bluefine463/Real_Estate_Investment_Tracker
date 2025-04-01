package com.example.servlet;

import org.bson.types.ObjectId;

public class User {
    private ObjectId id;
    private String username;
    private String password;
    private String email;
    private String phoneno;

    public User() {}
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public User(String username, String password, String email, String phoneno) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneno=phoneno;
    }

    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneno(){return phoneno; }
    public void setPhoneno(String phoneno){this.phoneno = phoneno; }

}

