package com.example.photometal1.Controllers;

import com.example.photometal1.Dao.UserDao;
import com.example.photometal1.GlobalMethods.Spliter_Screen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.PasswordField;
import java.io.IOException;

public class LoginController {

    @FXML
    private TextField username ;

    @FXML
    private PasswordField password;

    @FXML
    private void onButtonlogin(ActionEvent event) throws IOException {

        UserDao userDao = new UserDao();
      int  flag_login= userDao.LoginUser(username.getText(), password.getText());





      if (flag_login >0) {
          // 3. Αλλάζεις τη σκηνή
          Spliter_Screen.splitScreen(event);

      }

    }

    @FXML
    private void onButtonRegister(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource("/com/example/photometal1/Register-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load());

        // 3. Αλλάζεις τη σκηνή
        stage.setScene(scene);
        stage.show();

    }

    @FXML
    private void onButtonForgotPassword(ActionEvent event) throws IOException {

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource("/com/example/photometal1/PassWordRessetEmailState-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load());

        // 3. Αλλάζεις τη σκηνή
        stage.setScene(scene);
        stage.show();

    }




}
