package com.example.photometal1.Models;

import java.util.Date;

public class work_reports {

    private int id;
    private int playgroundId;
    private int photographerId;
    private int parties;
    private int Customer;
    private int totalAmount;
    private String created_at;
    private int papers;
    private Date report_date;
    private String playgroundName;

    public work_reports() {}

    public work_reports(int playgroundId, int photographerId, int parties, int customer, int totalAmount, int papers) {
        this.playgroundId = playgroundId;
        this.photographerId = photographerId;
        this.parties = parties;
        Customer = customer;
        this.totalAmount = totalAmount;
        this.papers = papers;
    }

    public work_reports(int id, int playgroundId, String playgroundName,
                        int photographerId, Date report_date,
                        int parties, int customers, int papers, int totalAmount) {

        this.id = id;
        this.playgroundId = playgroundId;
        this.playgroundName = playgroundName;
        this.photographerId = photographerId;
        this.report_date = report_date;
        this.parties = parties;
        this.Customer = customers;
        this.papers = papers;
        this.totalAmount = totalAmount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlaygroundId() {
        return playgroundId;
    }

    public void setPlaygroundId(int playgroundId) {
        this.playgroundId = playgroundId;
    }

    public int getphotographerId() {
        return photographerId;
    }

    public void setphotographerId(int photographerId) {
        this.photographerId = photographerId;
    }



    public int getParties() {
        return parties;
    }

    public void setParties(int parties) {
        this.parties = parties;
    }

    public int getCustomer() {
        return Customer;
    }

    public void setCustomer(int customer) {
        Customer = customer;
    }



    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getPlaygroundName() {
        return playgroundName;
    }

    public void setPlaygroundName(String playgroundName) {
        this.playgroundName = playgroundName;
    }

    public int getPapers() {
        return papers;
    }

    public void setPapers(int papers) {
        this.papers = papers;
    }

    public Date getReport_date() {
        return report_date;
    }

    public void setReport_date(Date report_date) {
        this.report_date = report_date;
    }

}
