package com.example.photometal1.GlobalMethods;

import com.example.photometal1.Controllers.LoginController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Spliter_Screen {


    public static void splitScreen(ActionEvent event) throws IOException {
        if (SessionManager.getCurrentUserRole().equals("USER")) {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource("/com/example/photometal1/WorkReportUsers-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.show();

        }
        else
        {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource("/com/example/photometal1/homeAdmin-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.show();

        }
    }
    public static void MoveForAdmin(ActionEvent event,String screen) throws IOException {


        switch (screen) {
            case "Playgrounds":
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource("/com/example/photometal1/Playgrounds-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                stage.setScene(scene);
                stage.show();
                break;
            case "ProductForAdmin":
                Stage stage2 = (Stage) ((Node) event.getSource()).getScene().getWindow();
                FXMLLoader fxmlLoader2 = new FXMLLoader(LoginController.class.getResource("/com/example/photometal1/ProductForAdmin-View.fxml"));
                Scene scene2 = new Scene(fxmlLoader2.load());
                stage2.setScene(scene2);
                stage2.show();
                break;
            case "Log_auditAdmin":
                Stage stage4 = (Stage) ((Node) event.getSource()).getScene().getWindow();
                FXMLLoader fxmlLoader4 = new FXMLLoader(LoginController.class.getResource("/com/example/photometal1/Log_auditAdmin-view.fxml"));
                Scene scene4 = new Scene(fxmlLoader4.load());
                stage4.setScene(scene4);
                stage4.show();
                break;

            case "Home":
                Stage stage5 = (Stage) ((Node) event.getSource()).getScene().getWindow();
                FXMLLoader fxmlLoader5 = new FXMLLoader(LoginController.class.getResource("/com/example/photometal1/homeAdmin-view.fxml"));
                Scene scene5 = new Scene(fxmlLoader5.load());
                stage5.setScene(scene5);
                stage5.show();
                break;

            case "Documents":
                Stage stage6 = (Stage) ((Node) event.getSource()).getScene().getWindow();
                FXMLLoader fxmlLoader6 = new FXMLLoader(LoginController.class.getResource("/com/example/photometal1/DocumentAdmin-view.fxml"));
                Scene scene6 = new Scene(fxmlLoader6.load());
                stage6.setScene(scene6);
                stage6.show();
                break;
            case "Log_out":
                Stage stage7 = (Stage) ((Node) event.getSource()).getScene().getWindow();
                FXMLLoader fxmlLoader7 = new FXMLLoader(LoginController.class.getResource("/com/example/photometal1/Login-view.fxml"));
                Scene scene7 = new Scene(fxmlLoader7.load());
                stage7.setScene(scene7);
                break;
        }



    }
    public static void MoveForUser(ActionEvent event,String screen) throws IOException{

        switch (screen) {
            case "Home":
                Stage stage5 = (Stage) ((Node) event.getSource()).getScene().getWindow();
                FXMLLoader fxmlLoader5 = new FXMLLoader(LoginController.class.getResource("/com/example/photometal1/WorkReportUsers-view.fxml"));
                Scene scene5 = new Scene(fxmlLoader5.load());
                stage5.setScene(scene5);
                stage5.show();
                break;
            case "Setting":
                Stage stage6 = (Stage) ((Node) event.getSource()).getScene().getWindow();
                FXMLLoader fxmlLoader6 = new FXMLLoader(LoginController.class.getResource("/com/example/photometal1/SettingUser-view.fxml"));
                Scene scene6 = new Scene(fxmlLoader6.load());
                stage6.setScene(scene6);
                break;
            case "Log_out":
                Stage stage7 = (Stage) ((Node) event.getSource()).getScene().getWindow();
                FXMLLoader fxmlLoader7 = new FXMLLoader(LoginController.class.getResource("/com/example/photometal1/Login-view.fxml"));
                Scene scene7 = new Scene(fxmlLoader7.load());
                stage7.setScene(scene7);
                break;
        }


    }



}
