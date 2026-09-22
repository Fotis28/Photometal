package com.example.photometal1.Models;

import java.sql.Date;

public class PHOTOGRAPHER {

    private int id;
    private int user_id;
    private String full_name;
    private boolean is_deleted;
    private Date HireDate;

    public PHOTOGRAPHER() {}


    public PHOTOGRAPHER(int user_id, String full_name) {
        this.user_id = user_id;
        this.full_name = full_name;


    }


    public PHOTOGRAPHER(int id, int user_id, String full_name, boolean is_deleted, Date hireDate) {
        this.id = id;
        this.user_id = user_id;
        this.full_name = full_name;
        this.is_deleted = is_deleted;
        HireDate = hireDate;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getUser_id() {
        return user_id;
    }
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    public String getFull_name() {
        return full_name;
    }
    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }
    public boolean isIs_deleted() {
        return is_deleted;
    }
    public void setIs_deleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }
    public Date getHireDate() {
        return HireDate;
    }
    public void setHireDate(Date hireDate) {
        HireDate = hireDate;
    }



}
