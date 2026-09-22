package com.example.photometal1.Dao;

import com.example.photometal1.Database.Database_Connection;
import com.example.photometal1.GlobalMethods.SessionManager;
import com.example.photometal1.Models.Playground;
import com.example.photometal1.Models.PlaygroundSimple;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PlaygroundDao {

    public void createPlayground(Playground playground) throws Exception {
        // Κληση της συναρτησης για την συνδεση με την βαση
        try(Connection conn = Database_Connection.connect();


        ) {
            //παιρνω τον κωδικο του χρηστη
            int newUserId = SessionManager.getUserId();


            // Γινεται για να εμφανιζει στο insert photographer την τιμη του user_id
            PreparedStatement ps = conn.prepareStatement("SELECT set_app_user_id(?)");
            ps.setInt(1, newUserId);
            ps.execute();


            //δημιουργια του playground
            CallableStatement stmt = conn.prepareCall("CALL create_playground(?, ?, ?, ?,?)");
            stmt.setString(1, playground.getName());
            stmt.setString(2, playground.getAddress());
            stmt.setString(3, playground.getPhone());
            stmt.setString(4, playground.getOpenTime());
            stmt.registerOutParameter(5, java.sql.Types.INTEGER);
            stmt.execute();

            int newPlaygroundId = stmt.getInt(5);

            if(newPlaygroundId > 0) {
                store_house_stockDao stockDao = new store_house_stockDao();
                stockDao.createStoreHouseStock(newPlaygroundId);
            }
            else {
                System.out.println("❌ Playground creation failed.");
            }

        }
        catch (Exception e) {
            System.out.println("❌ Error creating table: " + e.getMessage());
        }

    }

    public void deletePlayground(int playground_id) throws Exception {
        try(Connection conn = Database_Connection.connect();){
            PreparedStatement ps = conn.prepareStatement("SELECT set_app_user_id(?)");
            ps.setInt(1, SessionManager.getUserId());
            ps.execute();

            CallableStatement stmt = conn.prepareCall("CALL delete_playground(?)");
            stmt.setInt(1, playground_id);
            stmt.execute();
        }
        catch (Exception e) {
            System.out.println("❌ Error creating table: " + e.getMessage());
        }

    }

    public void updatePlayground(Playground playground) throws Exception {
        try(Connection conn = Database_Connection.connect();){
            PreparedStatement ps = conn.prepareStatement("SELECT set_app_user_id(?)");
            ps.setInt(1, SessionManager.getUserId());
            ps.execute();

            CallableStatement stmt = conn.prepareCall("CALL update_playground(?, ?, ?, ?, ?)");
            stmt.setInt(1, playground.getId());
            stmt.setString(2, playground.getName());
            stmt.setString(3, playground.getAddress());
            stmt.setString(4, playground.getPhone());
            stmt.setString(5, playground.getOpenTime());
            stmt.execute();
        }
        catch (Exception e) {
            System.out.println("❌ Error creating table: " + e.getMessage());
        }

    }
    //Λίστα με ολα τα playgrounds ως τιμή το id και το name
    public ObservableList<PlaygroundSimple> getAllPlaygroundsSimple() throws Exception {
        ObservableList<PlaygroundSimple> list = FXCollections.observableArrayList();
        String SQL = "SELECT * FROM GetAllPlaygroundsSimple()";

        try (Connection conn = Database_Connection.connect();
             PreparedStatement stmt = conn.prepareStatement(SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                list.add(new PlaygroundSimple(id, name));
            }
        }
        return list;
    }


    //Μου δίνει τις πληροφορίες του playground
    public Playground getPlayground(int playground_id) throws Exception {

        try(Connection conn = Database_Connection.connect();
            PreparedStatement stmt = conn.prepareStatement("Select * FROM InfoOfOnePlaygroynd(?) ")) {

            stmt.setInt(1, playground_id);
            ResultSet rs = stmt.executeQuery();
            if(rs.next())
            {
                Playground playground = new Playground();
                playground.setId(rs.getInt(1));
                playground.setName(rs.getString(2));
                playground.setAddress(rs.getString(3));
                playground.setPhone(rs.getString(4));
                playground.setOpenTime(rs.getString(5));
                return playground;
            }

        }


        return null;
    }

}
