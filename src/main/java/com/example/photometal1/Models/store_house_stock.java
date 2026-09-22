package com.example.photometal1.Models;

import java.sql.Date;

public class store_house_stock {

    private int playgroundId;
    private int productId;
    private int current_amount;
    private Date lastUpdate;
    private boolean is_deleted;


    public store_house_stock() {}
    // Aυτό χρησιμοποιείται για την αποθήκευση του stock
    public store_house_stock(int playgroundId, int productId, int current_amount) {
        this.playgroundId = playgroundId;
        this.productId = productId;
        this.current_amount = current_amount;
    }

    public store_house_stock(int playgroundId, int productId, int current_amount, Date lastUpdate) {
        this.playgroundId = playgroundId;
        this.productId = productId;
        this.current_amount = current_amount;
        this.lastUpdate = lastUpdate;
    }

    public int getPlaygroundId() {
        return playgroundId;
    }

    public int getProductId() {
        return productId;
    }

    public int getCurrentStock() {
        return current_amount;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setPlaygroundId(int playgroundId) {
        this.playgroundId = playgroundId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setCurrentStock(int current_amount) {
        this.current_amount = current_amount;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public boolean getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }


}
