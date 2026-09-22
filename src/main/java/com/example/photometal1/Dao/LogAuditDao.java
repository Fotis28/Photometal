package com.example.photometal1.Dao;

import com.example.photometal1.Database.Database_Connection;
import com.example.photometal1.Models.Log_audit;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LogAuditDao {



        public List<Log_audit> getLog(String tableFilter) {
            List<Log_audit> logs = new ArrayList<>();

            try (Connection conn = Database_Connection.connect();
                 PreparedStatement ps = conn.prepareStatement("SELECT * FROM get_log_audit(?)")) {

                if (tableFilter == null || tableFilter.equals("Όλα")) {
                    ps.setNull(1, Types.VARCHAR);
                } else {
                    ps.setString(1, tableFilter);
                }

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    Log_audit log = new Log_audit(
                            rs.getInt("id"),
                            rs.getString("log_time"),
                            rs.getInt("user_id"),
                            rs.getString("table_name"),
                            rs.getString("action_type"),
                            rs.getInt("record_id"),
                            rs.getString("old_data"),
                            rs.getString("new_data"),
                            rs.getString("full_name")   // ⚠️ εδώ
                    );
                    logs.add(log);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return logs;
        }


}
