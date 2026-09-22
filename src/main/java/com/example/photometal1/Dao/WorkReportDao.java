package com.example.photometal1.Dao;

import com.example.photometal1.Database.Database_Connection;
import com.example.photometal1.GlobalMethods.SessionManager;
import com.example.photometal1.Models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WorkReportDao {



    public int createWorkReportAndDetails(LocalDate reportDate, int playgroundId, String partiesText, String customersText, String papersText, List<ProductSaleItem> soldItems ) {

        int new_report_id= -1;
        // Δημιουργία των Arrays για την SQL
        List<Integer> productIds = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();

        int parties = Integer.parseInt(partiesText);
        int customers = Integer.parseInt(customersText);
        int papers = Integer.parseInt(papersText);



        for (ProductSaleItem item : soldItems) {
            if (item.getQuantitySold() > 0) {
                productIds.add(item.getId());
                quantities.add(item.getQuantitySold());
            }
        }



        try(Connection con =  Database_Connection.connect();
            CallableStatement stmt3 = con.prepareCall("CALL create_work_report_and_details(?, ?, ?, ?, ?, ?, ?, ?, ?)");
            PreparedStatement stmt = con.prepareStatement("SELECT set_app_user_id(?)");
            PreparedStatement stmt2 = con.prepareStatement("SELECT find_photographer_with_user_id(?)");
        ) {
           //PreparedStatement stmt = con.prepareStatement("SELECT set_app_user_id(?)");
            stmt.setInt(1, SessionManager.getUserId());
            stmt.execute();


            // PreparedStatement stmt2 = con.prepareStatement("SELECT find_photographer_with_user_id(?)");
            stmt2.setInt(1, SessionManager.getUserId());


            ResultSet rs = stmt2.executeQuery();


            // 3. Δημιουργία SQL Arrays
            Array sqlProductIds = con.createArrayOf("integer", productIds.toArray(new Integer[0]));
            Array sqlQuantities = con.createArrayOf("integer", quantities.toArray(new Integer[0]));

            if (!rs.next()) {
                throw new RuntimeException("Δεν βρέθηκε φωτογράφος για τον χρήστη με id = " + SessionManager.getUserId());
            }

                int id_photographer = rs.getInt(1);


               //CallableStatement stmt3 = con.prepareCall("CALL create_work_report_and_details(?, ?, ?, ?, ?, ?, ?, ?, ?)");
                stmt3.setInt(1, id_photographer);
                stmt3.setInt(2, playgroundId);
                stmt3.setDate(3, Date.valueOf(reportDate));
                stmt3.setInt(4, parties);
                stmt3.setInt(5, customers);
                stmt3.setInt(6, papers);

                // IN Array Parameters (7-8)
                stmt3.setArray(7, sqlProductIds);
                stmt3.setArray(8, sqlQuantities);

                // OUT Parameter (9)
                stmt3.registerOutParameter(9, java.sql.Types.INTEGER);

                stmt3.execute();
                new_report_id = stmt3.getInt(9);
                System.out.println("✅ Work report created with ID: " + new_report_id);





        }
        catch (Exception e) {
            System.out.println("❌ Error creating table: " + e.getMessage());
        }
        return new_report_id ;
    }

    public List<work_reports> findAll() {
        List<work_reports> result = new ArrayList<>();

        int userId = SessionManager.getUserId();

        String sql = "SELECT * FROM get_work_reports_for_user(?)";

        try (Connection con = Database_Connection.connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    work_reports wr = new work_reports();

                    wr.setId(rs.getInt("report_id"));
                    wr.setPlaygroundId(rs.getInt("playground_id"));
                    wr.setPlaygroundName(rs.getString("playground_name"));
                    wr.setReport_date(rs.getDate("report_date"));
                    wr.setParties(rs.getInt("parties_count"));
                    wr.setCustomer(rs.getInt("customers_count"));
                    wr.setPapers(rs.getInt("papers_count"));

                    // total_amount είναι NUMERIC(10,2) στη DB
                    // το model σου έχει int → κάνουμε cast (ή αλλάζεις το model σε BigDecimal αργότερα)
                    wr.setTotalAmount(
                            rs.getBigDecimal("total_amount") != null
                                    ? rs.getBigDecimal("total_amount").intValue()
                                    : 0
                    );

                    result.add(wr);
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error loading work reports: " + e.getMessage());
        }

        return result;
    }


    public List<report_details> getReportDetails(int reportId) {
        List<report_details> result = new ArrayList<>();

        String sql = "SELECT * FROM get_report_details(?)";

        try (Connection con = Database_Connection.connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, reportId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    report_details d = new report_details();
                    d.setId(rs.getInt("detail_id"));
                    d.set_work_report_id(rs.getInt("work_report_id"));
                    d.setProduct_id(rs.getInt("product_id"));
                    d.set_quantity_sold(rs.getInt("quantity_sold"));
                    d.setUnit_price(rs.getDouble("unit_price"));

                    result.add(d);
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error loading report details: " + e.getMessage());
        }

        return result;
    }


    public void updateWorkReportAndDetails(
            int reportId,
            LocalDate reportDate,
            int playgroundId,
            String partiesText,
            String customersText,
            String papersText,
            List<ProductSaleItem> soldItems
    ) {
        int parties = Integer.parseInt(partiesText);
        int customers = Integer.parseInt(customersText);
        int papers = Integer.parseInt(papersText);

        List<Integer> productIds = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();

        for (ProductSaleItem item : soldItems) {
            if (item.getQuantitySold() > 0) {
                productIds.add(item.getId());
                quantities.add(item.getQuantitySold());
            }
        }

        String sqlCall = "CALL update_work_report_and_details(?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = Database_Connection.connect();
             PreparedStatement stmtSetUser = con.prepareStatement("SELECT set_app_user_id(?)");
             PreparedStatement stmtFindPhot = con.prepareStatement("SELECT find_photographer_with_user_id(?)");
             CallableStatement stmt = con.prepareCall(sqlCall)) {

            // user στο session της βάσης
            stmtSetUser.setInt(1, SessionManager.getUserId());
            stmtSetUser.execute();

            // βρίσκουμε photographer_id
            stmtFindPhot.setInt(1, SessionManager.getUserId());
            int photographerId;
            try (ResultSet rs = stmtFindPhot.executeQuery()) {
                if (!rs.next()) {
                    throw new RuntimeException("No photographer for user " + SessionManager.getUserId());
                }
                photographerId = rs.getInt(1);
            }

            Array sqlProductIds = con.createArrayOf("integer", productIds.toArray(new Integer[0]));
            Array sqlQuantities = con.createArrayOf("integer", quantities.toArray(new Integer[0]));

            stmt.setInt(1, reportId);
            stmt.setInt(2, photographerId);
            stmt.setInt(3, playgroundId);
            stmt.setDate(4, Date.valueOf(reportDate));
            stmt.setInt(5, parties);
            stmt.setInt(6, customers);
            stmt.setInt(7, papers);
            stmt.setArray(8, sqlProductIds);
            stmt.setArray(9, sqlQuantities);

            stmt.execute();

        } catch (Exception e) {
            System.out.println("❌ Error updating work report: " + e.getMessage());
        }
    }

    public List<TopProductStat> getTopProductsForCurrentUser(int limit) {
        List<TopProductStat> list = new ArrayList<>();

        String sql = "SELECT * FROM get_top_products_for_user(?, ?)";

        try (Connection con = Database_Connection.connect();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, SessionManager.getUserId());
            stmt.setInt(2, limit);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new TopProductStat(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getLong("total_sold"),
                        rs.getString("product_description")   // 👈 ΝΕΟ
                ));
            }

        } catch (Exception e) {
            System.out.println("❌ Error getTopProductsForCurrentUser: " + e.getMessage());
        }

        return list;
    }
    public ObservableList<PhotographerSimple> getAllPhotographersSimple() {
        ObservableList<PhotographerSimple> list = FXCollections.observableArrayList();

        String sql = "SELECT * FROM get_all_photographers_simple()";

        try (Connection con = Database_Connection.connect();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new PhotographerSimple(
                        rs.getInt("photographer_id"),
                        rs.getString("full_name")
                ));
            }

        } catch (Exception e) {
            System.out.println("❌ Error loading photographers: " + e.getMessage());
        }

        return list;
    }

    public ObservableList<AdminDocumentRow> getReportsForPhotographer(int photographerId) {
        ObservableList<AdminDocumentRow> list = FXCollections.observableArrayList();

        String sql = "SELECT * FROM get_work_reports_for_photographer(?)";

        try (Connection con = Database_Connection.connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, photographerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new AdminDocumentRow(
                            rs.getInt("report_id"),
                            rs.getString("photographer_name"),
                            rs.getString("playground_name"),
                            rs.getDate("report_date"),
                            rs.getInt("parties_count"),
                            rs.getInt("customers_count"),
                            rs.getInt("papers_count"),
                            rs.getDouble("total_amount")
                    ));
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Error loading reports for photographer: " + e.getMessage());
        }

        return list;
    }

}
