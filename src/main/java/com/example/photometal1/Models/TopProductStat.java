package com.example.photometal1.Models;

public class TopProductStat {
    private int productId;
    private String productName;
    private long totalSold;
    private String productDescription;

    public TopProductStat(int productId, String productName, long totalSold, String productDescription) {
        this.productId = productId;
        this.productName = productName;
        this.totalSold = totalSold;
        this.productDescription = productDescription;
    }

    public String getProductName() { return productName; }
    public long getTotalSold() { return totalSold; }
    public String getProductDescription() { return productDescription; }
}
