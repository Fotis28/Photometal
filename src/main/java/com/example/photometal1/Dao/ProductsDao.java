package com.example.photometal1.Dao;

import com.example.photometal1.Database.Database_Connection;
import com.example.photometal1.GlobalMethods.SessionManager;
import com.example.photometal1.Models.Product;
import com.example.photometal1.Models.ProductSaleItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.example.photometal1.Models.MonthlySale;
import java.math.BigDecimal;
import java.sql.*;

public class ProductsDao {


    public void createProduct(Product product) {


        try(Connection con = Database_Connection.connect()){
            PreparedStatement ps = con.prepareStatement("SELECT set_app_user_id(?)");
            ps.setInt(1, SessionManager.getUserId());
            ps.execute();


            CallableStatement stmt = con.prepareCall("CALL create_products(?, ?, ?, ?)");
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getdescription());
            stmt.setBigDecimal (3, product.getPrice());
            stmt.registerOutParameter(4, java.sql.Types.INTEGER);
            stmt.execute();

            int newProductId = stmt.getInt(4);

            if(newProductId > 0) {
                System.out.println("✅ Product created with ID: " + newProductId);
            }
            else {
                System.out.println("❌ Product creation failed.");
            }


          }
        catch (Exception e) {
            System.out.println("❌ Error creating table: " + e.getMessage());
        }
    }


    public void deleteProduct(int product_id) throws Exception {
        try(Connection conn = Database_Connection.connect();){
            PreparedStatement ps = conn.prepareStatement("SELECT set_app_user_id(?)");
            ps.setInt(1, SessionManager.getUserId());
            ps.execute();

            CallableStatement stmt = conn.prepareCall("CALL delete_products(?)");
            stmt.setInt(1, product_id);
            stmt.execute();
        }
        catch (Exception e) {
            System.out.println("❌ Error creating table: " + e.getMessage());



        }
    }

    public void updateProduct(Product product) throws Exception {
        try(Connection conn = Database_Connection.connect()){
            PreparedStatement ps = conn.prepareStatement("SELECT set_app_user_id(?)");
            ps.setInt(1, SessionManager.getUserId());
            ps.execute();

            CallableStatement stmt = conn.prepareCall("CALL update_products(?, ?, ?, ?)");
            stmt.setInt(1, product.getId());
            stmt.setString(2, product.getName());
            stmt.setString(3, product.getdescription());
            stmt.setBigDecimal(4, product.getPrice());
            stmt.execute();
        }
        catch (Exception e) {
            System.out.println("❌ Error creating table: " + e.getMessage());
        }
    }


    public ObservableList<ProductSaleItem> getAllProductsForSalesTable() throws Exception {
        ObservableList<ProductSaleItem> list = FXCollections.observableArrayList();// για να γινεται αυτοματα η αλλαγη και στο UI

        // SQL: Παίρνουμε ID, Name, Description, και Price
        String SQL = "SELECT * From GetAllProduct()";

        try (Connection conn = Database_Connection.connect();
             PreparedStatement stmt = conn.prepareStatement(SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("product_id");
                String name = rs.getString("product_name");
                String description = rs.getString("product_description");
                BigDecimal price = rs.getBigDecimal("product_price");

                // Δημιουργούμε το μοντέλο πώλησης με αρχική ποσότητα 0
                list.add(new ProductSaleItem(id, name, description, price));
            }
        }
        return list;
    }


    public ObservableList<Product> getAllProductsExist() throws Exception {
        ObservableList<Product> list = FXCollections.observableArrayList();

        String SQL = "SELECT * From getallProductsExist()";

        try (Connection conn = Database_Connection.connect();
             PreparedStatement stmt = conn.prepareStatement(SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("product_id");
                String name = rs.getString("product_name");
                String description = rs.getString("product_description");
                BigDecimal price = rs.getBigDecimal("product_price");


                Product p = new Product(id, name, description, price, false);
                list.add(p);
            }
        }

        return list;
    }


    public ObservableList<Product> getAllProductsNotExist() throws Exception {
        ObservableList<Product> list = FXCollections.observableArrayList();

        String SQL = "SELECT * From getallProductsNotExist()";

        try (Connection conn = Database_Connection.connect();
             PreparedStatement stmt = conn.prepareStatement(SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("product_id");
                String name = rs.getString("product_name");
                String description = rs.getString("product_description");
                BigDecimal price = rs.getBigDecimal("product_price");

                Product p = new Product(id, name, description, price, true);
                list.add(p);
            }
        }

        return list;
    }

    public void restoreProduct(int productId) throws Exception {
        try (Connection conn = Database_Connection.connect()) {

            PreparedStatement ps = conn.prepareStatement("SELECT set_app_user_id(?)");
            ps.setInt(1, SessionManager.getUserId());
            ps.execute();

            CallableStatement stmt = conn.prepareCall("CALL restore_product(?)");
            stmt.setInt(1, productId);
            stmt.execute();
        } catch (Exception e) {
            System.out.println("❌ Error restoring product: " + e.getMessage());
            throw e;
        }
    }

    public ObservableList<MonthlySale> getMonthlySalesForProductYear(int productId, int year) throws SQLException {
        ObservableList<MonthlySale> list = FXCollections.observableArrayList();

        String SQL = """
        SELECT *
        FROM get_monthly_sales_for_product_year(?, ?)
        """;

        try (Connection conn = Database_Connection.connect();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, productId);
            stmt.setInt(2, year);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String monthLabel = rs.getString("month_label");
                    int totalSold = rs.getInt("total_sold");
                    list.add(new MonthlySale(monthLabel, totalSold));
                }
            }
        }

        return list;
    }



}
