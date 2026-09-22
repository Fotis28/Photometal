package com.example.photometal1.Models;

public class User {

    private int id;
    private String email;
    private String password;
    private String roll;
    private boolean is_deleted;

    public User() {}

    public User( String password, String email,String roll) {
        this.email = email;
        this.password = password;
        this.roll=roll;
    }

    public User(int id,  String password, String email,String roll) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.roll=roll;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public boolean isIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }
}
