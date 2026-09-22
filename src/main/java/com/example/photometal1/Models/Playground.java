package com.example.photometal1.Models;

public class Playground {

    private int id;
    private String name;
    private String address;
    private String phone;
    private boolean is_deleted;
    private String OpenTime;

    //Default constructor
    public Playground(){}

    //Αυτό είναι το constructor χωρίς το id(το κυρίος)
    public Playground( String name, String address, String phone, String OpenTime, boolean is_deleted) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.OpenTime = OpenTime;
        this.is_deleted = is_deleted;
        this.id = 0;
    }
    //Αυτός είναι το πλήρες constructor
    public Playground(int id, String name, String address, String phone, String OpenTime, boolean is_deleted) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.is_deleted = is_deleted;
        this.OpenTime = OpenTime;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public boolean isIs_deleted() {
        return is_deleted;
    }
    public void setIs_deleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }
    public String getOpenTime() {
        return OpenTime;
    }
    public void setOpenTime(String openTime) {
        OpenTime = openTime;
    }

}

