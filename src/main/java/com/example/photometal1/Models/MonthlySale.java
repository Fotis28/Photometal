package com.example.photometal1.Models;

public class MonthlySale {
    private String monthLabel;
    private int totalSold;

    public MonthlySale(String monthLabel, int totalSold) {
        this.monthLabel = monthLabel;
        this.totalSold = totalSold;
    }

    public String getMonthLabel() {
        return monthLabel;
    }

    public int getTotalSold() {
        return totalSold;
    }
}
