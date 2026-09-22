package com.example.photometal1.Models;

import java.math.BigDecimal;

public class Product {
    private int id;
    private String name;
    private String description;
    private BigDecimal  price;
    private boolean is_deleted;


    public Product() {}

    public Product(String name, String description, BigDecimal price) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.id = 0;
        this.is_deleted = false;

    }

    public Product(int id, String name, String description, BigDecimal  price, boolean is_deleted) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.is_deleted = is_deleted;

    }
    public Product(boolean is_deleted, String name, String description, BigDecimal  price) {
        this.id = 0;
        this.name = name;
        this.description = description;
        this.price = price;
        this.is_deleted = is_deleted;
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

    public String getdescription() {
        return description;
    }

    public void setdescription(String description) {
        this.description = description;
    }

    public BigDecimal  getPrice() {
        return price;
    }

    public void setPrice(BigDecimal  price) {
        this.price = price;
    }

    public boolean get_is_deleted() {
        return is_deleted;
    }

    public void set_is_deleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    @Override
    public String toString() {
        return name + " - " + description;
    }
}
