package com.example.photometal1;

import com.example.photometal1.Database.Database_Connection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;

public class Photometal extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        // 1. Μεταφορά στο login
        FXMLLoader fxmlLoader = new FXMLLoader(Photometal.class.getResource("Login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {

        try (Connection conn = Database_Connection.connect();
             Statement stmt = conn.createStatement())
         {

//            stmt.execute(CreateTables.create_tables);
//            System.out.println("✅ Table 'students' created (if not exists).");

        } catch (Exception e) {
            System.out.println("❌ Error creating table: " + e.getMessage());
        }

              launch();
    }
}