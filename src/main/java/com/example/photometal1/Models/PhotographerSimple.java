package com.example.photometal1.Models;

public class PhotographerSimple {
    private int id;
    private String fullName;

    public PhotographerSimple(int id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public String toString() {
        // Έτσι θα φαίνεται ωραία στο ComboBox
        return fullName;
    }
}
