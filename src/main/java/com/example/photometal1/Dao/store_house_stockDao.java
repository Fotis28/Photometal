package com.example.photometal1.Dao;

import com.example.photometal1.Database.Database_Connection;
import com.example.photometal1.GlobalMethods.SessionManager;
import com.example.photometal1.Models.Store_House_Stock_For_View;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class store_house_stockDao {

    // Αρχικοποίηση stock για ΕΝΑ playground (δημιουργεί γραμμές για όλα τα products)
    public void createStoreHouseStock(int playgroundId) throws Exception {

        try (Connection con = Database_Connection.connect()) {

            // 1. Περνάμε το user_id στη βάση για το log_audit
            PreparedStatement ps = con.prepareStatement("SELECT set_app_user_id(?)");
            ps.setInt(1, SessionManager.getUserId());
            ps.execute();

            // 2. Καλούμε την procedure που ήδη έχεις στο SQL:
            // CREATE OR REPLACE PROCEDURE initialize_new_playground_stock(p_playground_id INT)
            CallableStatement stmt = con.prepareCall("CALL initialize_new_playground_stock(?)");
            stmt.setInt(1, playgroundId);
            stmt.execute();

            System.out.println("✅ Stock initialized for playground id = " + playgroundId);
        }
        catch (Exception e) {
            System.out.println("❌ Error initializing stock: " + e.getMessage());
            throw e;
        }
    }

    // Προαιρετικό: για να αλλάζεις χειροκίνητα ποσότητα
    public void adjustStock(int productId, int playgroundId, int newAmount) throws Exception {
        try (Connection con = Database_Connection.connect()) {

            PreparedStatement ps = con.prepareStatement("SELECT set_app_user_id(?)");
            ps.setInt(1, SessionManager.getUserId());
            ps.execute();

            // Χρησιμοποιείς είτε adjust_stock είτε update_stock_amount, ανάλογα τι θέλεις
            CallableStatement stmt = con.prepareCall("CALL adjust_stock(?, ?, ?)");
            stmt.setInt(1, productId);
            stmt.setInt(2, playgroundId);
            stmt.setInt(3, newAmount);
            stmt.execute();

            System.out.println("✅ Stock adjusted for product " + productId + " at playground " + playgroundId);
        }
        catch (Exception e) {
            System.out.println("❌ Error adjusting stock: " + e.getMessage());
            throw e;
        }
    }



    public ObservableList<Store_House_Stock_For_View> getStockForPlayground(int playgroundId) throws Exception {
        ObservableList<Store_House_Stock_For_View> list = FXCollections.observableArrayList();

        String sql = "SELECT * FROM get_store_house_stock(?)";

        try (Connection con = Database_Connection.connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, playgroundId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int productId = rs.getInt("product_id");
                    String name = rs.getString("product_name");
                    String desc = rs.getString("product_description");
                    int quantity = rs.getInt("quantity");

                    Store_House_Stock_For_View item = new Store_House_Stock_For_View(
                            productId, name, desc, quantity,playgroundId
                    );
                    list.add(item);
                }
            }
        }

        return list;
    }

    public void updateStockQuantity(int playgroundId, int productId, int newQuantity) throws Exception {
        try (Connection con = Database_Connection.connect()) {

            PreparedStatement ps = con.prepareStatement("SELECT set_app_user_id(?)");
            ps.setInt(1, SessionManager.getUserId());
            ps.execute();

            CallableStatement stmt = con.prepareCall("CALL update_stock_amount(?, ?, ?)");
            stmt.setInt(1, productId);
            stmt.setInt(2, playgroundId);
            stmt.setInt(3, newQuantity);
            stmt.execute();
        }
    }

}

