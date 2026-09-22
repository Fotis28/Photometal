package com.example.photometal1.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database_Connection {
    private static final String driverClassName = "org.postgresql.Driver";
    private static final String url ="jdbc:postgresql://localhost:5432/Photometal";
    private static final String user = "postgres";
    private static final String password = "12345f789";

    public static Connection connect() {
        Connection conn = null;
        try {
            Class.forName(driverClassName);
            conn = DriverManager.getConnection(url, user, password);
            System.out.println(" Connected successfully to PostgreSQL!");
        } catch (ClassNotFoundException e) {
            System.out.println(" JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println(" Database connection failed: " + e.getMessage());
        }
        return conn;
    }


}
