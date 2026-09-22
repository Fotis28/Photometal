package com.example.photometal1.Models;

import javafx.beans.property.SimpleIntegerProperty;
import java.math.BigDecimal;

public class ProductSaleItem {

    private final int id;
    private final String name;
    private final String description;
    private final BigDecimal price; // Αυτό είναι η κρυφή τιμή
    private final SimpleIntegerProperty quantitySold; // Επεξεργάσιμη Ποσότητα

    public ProductSaleItem(int id, String name, String description, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantitySold = new SimpleIntegerProperty(0); // Αρχική ποσότητα = 0
    }

    // --- Getters για εμφάνιση (Table View) ---

    // Συνδυαστικό όνομα/περιγραφή για την στήλη "Προϊόντα"
    public String getDisplayName() {
        return name + (description != null && !description.isEmpty() ? " " + description : "");
    }

    // Property για σύνδεση με την επεξεργάσιμη στήλη "Ποσότητα"
    public SimpleIntegerProperty quantitySoldProperty() {
        return quantitySold;
    }

    // Getter για να πάρετε την τρέχουσα τιμή της ποσότητας
    public int getQuantitySold() {
        return quantitySold.get();
    }

    // Setter για να ρυθμίσετε την ποσότητα (χρησιμοποιείται από την TableView)
    public void setQuantitySold(int quantitySold) {
        this.quantitySold.set(quantitySold);
    }

    // --- Getters για υπολογισμούς (Backend) ---

    public int getId() {
        return id;
    }

    public BigDecimal getPrice() {
        return price;
    }
}