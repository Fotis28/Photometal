package com.example.photometal1.Models;

public class report_details {

    private int id;
    private int work_report_id;
    private int product_id;
    private int quantity_sold;
    private double unit_price;




    public report_details() {}
    //Αυτό χρησιμοποιείται για το insert
    public report_details(int work_report_id, int product_id, int quantity_sold, double unit_price) {
        this.work_report_id = work_report_id;
        this.product_id = product_id;
        this.quantity_sold = quantity_sold;
        this.unit_price = unit_price;

    }

    public report_details(int id, int work_report_id, int product_id, int quantity_sold, double unit_price) {
        this.id = id;
        this.work_report_id = work_report_id;
        this.product_id = product_id;
        this.quantity_sold = quantity_sold;
        this.unit_price = unit_price;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int get_work_report_id() {
        return work_report_id;
    }

    public void set_work_report_id(int work_report_id) {
        this.work_report_id = work_report_id;
    }

    public int getProduct_id() {
        return product_id;
    }
    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int get_quantity_sold() {
        return quantity_sold;
    }

    public void set_quantity_sold(int quantity_sold) {
        this.quantity_sold = quantity_sold;
    }

    public double getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(double unit_price) {
        this.unit_price = unit_price;
    }


}
