package com.example.photometal1.Models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;


public class Store_House_Stock_For_View {

    private int productId;
    private String productName;
    private String productDescription;
    private int playgroundId;
    private final IntegerProperty quantity = new SimpleIntegerProperty();




    public Store_House_Stock_For_View() {}
    public Store_House_Stock_For_View(int productId, String productName, String productDescription, int quantity, int playgroundId) {
        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.quantity.set(quantity);
        this.playgroundId = playgroundId;

    }



    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }



    public int getPlaygroundId() {
        return playgroundId;
    }

    public void setPlaygroundId(int playgroundId) {
        this.playgroundId = playgroundId;
    }
    public String getProductInfo() {
        return getProductName() + " - " + getProductDescription();
    }

    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

}
