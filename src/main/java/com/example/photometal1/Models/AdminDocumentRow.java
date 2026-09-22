package com.example.photometal1.Models;

import java.sql.Date;

public class AdminDocumentRow {

    private int reportId;
    private String photographerName;
    private String playgroundName;
    private Date reportDate;
    private int parties;
    private int customers;
    private int papers;
    private double totalAmount;

    public AdminDocumentRow(int reportId,
                            String photographerName,
                            String playgroundName,
                            Date reportDate,
                            int parties,
                            int customers,
                            int papers,
                            double totalAmount) {
        this.reportId = reportId;
        this.photographerName = photographerName;
        this.playgroundName = playgroundName;
        this.reportDate = reportDate;
        this.parties = parties;
        this.customers = customers;
        this.papers = papers;
        this.totalAmount = totalAmount;
    }

    public int getReportId() {
        return reportId;
    }

    public String getPhotographerName() {
        return photographerName;
    }

    public String getPlaygroundName() {
        return playgroundName;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public int getParties() {
        return parties;
    }

    public int getCustomers() {
        return customers;
    }

    public int getPapers() {
        return papers;
    }

    public double getTotalAmount() {
        return totalAmount;
    }
}
