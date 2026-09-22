package com.example.photometal1.Dao;

import com.example.photometal1.Database.Database_Connection;
import com.example.photometal1.GlobalMethods.SessionManager;
import com.example.photometal1.Models.User;
import com.example.photometal1.Models.UserSettings;

import java.sql.*;

public class UserDao {


    public void registerUser(User user, String fullName) {

        try (Connection conn = Database_Connection.connect();)
        {
            CallableStatement createUser = conn.prepareCall(" CALL create_user(?, ?, ?, ?) ");
            createUser.setString(1, user.getEmail());
            createUser.setString(2, user.getPassword());
            if(user.getRoll().equals("ADMIN"))
            {
                createUser.setString(3, "ADMIN");
            }
            else
            {
                createUser.setString(3, "USER");
            }
            createUser.registerOutParameter(4, java.sql.Types.INTEGER);
            createUser.execute();

            int newUserId = createUser.getInt(4);


            // Γινεται για να εμφανιζει στο insert photographer την τιμη του user_id
            PreparedStatement ps = conn.prepareStatement("SELECT set_app_user_id(?)");
            ps.setInt(1, newUserId);
            ps.execute();




            System.out.println("User created with id = " + newUserId);


            CallableStatement createPhotographer = conn.prepareCall(
                    " CALL create_photographer(?, ?, ?, ?, ?) "
            );
            createPhotographer.setString(1, fullName);
            createPhotographer.setInt(2, newUserId);
            createPhotographer.setBoolean(3, false);         // is_deleted
            createPhotographer.setDate(4, java.sql.Date.valueOf(java.time.LocalDate.now()));
            createPhotographer.registerOutParameter(5, java.sql.Types.INTEGER);

            createPhotographer.execute();
            int newPhotographerId = createPhotographer.getInt(5);

            System.out.println("Photographer created with id = " + newPhotographerId);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public int LoginUser(String email, String password) {
        int userId = -1;

        // Η κλήση της Function (όπως κάναμε και πριν)
        String SQL = "Select findUser(?, ?) ";

        try (Connection conn = Database_Connection.connect();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            // 2η & 3η παράμετρος: IN (παράμετροι εισόδου)
            stmt.setString(1, email);
            stmt.setString(2, password);


            ResultSet rs = stmt.executeQuery();
            if(rs.next())
            {
                userId = rs.getInt(1);
                System.out.println("✅ Login Successful! User ID: " + userId);
                String role = getUserRoll(userId);
                // 3) Το αποθηκεύουμε στο SessionManager για JavaFX
                SessionManager.setUserId(userId, role);
            }
            else {
                // Ο ΧΡΗΣΤΗΣ ΔΕΝ ΒΡΕΘΗΚΕ (επιστράφηκε -1)
                System.out.println("❌ Invalid Credentials. Email or password is incorrect.");
            }

//            // ΕΔΩ ΓΙΝΕΤΑΙ Ο ΕΛΕΓΧΟΣ:
//            if (userId > 0) {
//                // Ο ΧΡΗΣΤΗΣ ΒΡΕΘΗΚΕ: Μπορείτε να συνδεθείτε
//                System.out.println("✅ Login Successful! User ID: " + userId);
//                // Εδώ μπορείτε να καλέσετε τη συνάρτηση set_app_user_id(userId)
//
//
//                PreparedStatement ps = conn.prepareStatement("SELECT set_app_user_id(?)");
//                ps.setInt(1, userId);
//                ps.execute();
//
//
//




        } catch (Exception e) {
            e.printStackTrace();
        }
        return userId;
    }




    public int findUserIdWithEmail(String email) {
        int userId = -1;

        // Η κλήση της Function (όπως κάναμε και πριν)


        try (Connection conn = Database_Connection.connect();
             PreparedStatement stmt = conn.prepareStatement("Select GetUserIdByEmail(?) ")) {



            // 2. IN Parameter (για το email)
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            if(rs.next())
            {
                userId = rs.getInt(1);
                System.out.println("Found user with id = " + userId);
            }
            else {
                // Ο ΧΡΗΣΤΗΣ ΔΕΝ ΒΡΕΘΗΚΕ (επιστράφηκε -1)
                System.out.println("❌ Invalid Credentials. Email or password is incorrect.");
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
        return userId;
    }






    public int updatePasswordOfUserID(int user_id,String new_password) {

        // Ο ΧΡΗΣΤΗΣ ΒΡΕΘΗΚΕ: Μπορείτε να αλλαξετε τον κωδικο του
        if (user_id > 0) {
            try(Connection conn = Database_Connection.connect();
                PreparedStatement stmt = conn.prepareStatement("SELECT set_app_user_id(?)"))
            {

                stmt.setInt(1, user_id);
                stmt.execute();

                CallableStatement updateUser = conn.prepareCall(" SELECT updatepassword(?, ?) ");
                updateUser.setInt(1, user_id);
                updateUser.setString(2, new_password);
                updateUser.execute();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }

        return user_id;
    }

    public String getUserRoll(int user_id) {
        String roll = null;
        try (Connection conn = Database_Connection.connect();
             PreparedStatement stmt = conn.prepareStatement("Select Find_user_role(?) ")) {


            stmt.setInt(1, user_id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                roll = rs.getString(1);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return roll;
    }

    public void deleteUser() {
        try (Connection conn = Database_Connection.connect();
             CallableStatement stmt = conn.prepareCall("Call delete_user(?)")) {

            stmt.setInt(1, SessionManager.getUserId());
            stmt.execute();
    }
        catch (Exception e) {
            System.out.println("❌ Error creating table: " + e.getMessage());
        }
    }
    public void updateCurrentUserProfile(String password, String fullName) throws Exception {
        int userId = SessionManager.getUserId();

        try (Connection conn = Database_Connection.connect();
             CallableStatement cs = conn.prepareCall("CALL update_current_user_profile(?, ?, ?)")) {

            cs.setInt(1, userId);
            if (password == null) {
                cs.setNull(2, java.sql.Types.VARCHAR);
            } else {
                cs.setString(2, password);
            }
            cs.setString(3, fullName);

            cs.execute();
        }
    }


    public UserSettings getCurrentUserSettings() throws Exception {
        int userId = SessionManager.getUserId();

        String sql = "SELECT * FROM get_current_user_settings(?)";

        try (Connection conn = Database_Connection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UserSettings s = new UserSettings();
                    s.setUserId(rs.getInt("user_id"));
                    s.setEmail(rs.getString("email"));
                    s.setFullName(rs.getString("full_name"));

                    java.sql.Date hire = rs.getDate("hire_date");
                    if (hire != null) {
                        s.setHireDate(hire.toLocalDate());
                    }

                    return s;
                }
            }
        }
        return null;
    }




}
