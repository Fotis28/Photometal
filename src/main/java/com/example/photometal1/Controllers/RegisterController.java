package com.example.photometal1.Controllers;

import com.example.photometal1.Dao.UserDao;
import com.example.photometal1.Models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {

    @FXML
    private TextField fullName;

    @FXML
    private TextField email;

    @FXML
    private TextField password;

    @FXML
    private TextField confirmPassword;

    @FXML
    private void onButtonRegister(ActionEvent event) throws IOException  {

        // Ελεγχος για τα inputs
        if (!validateInputs()) {
            return; // Αν έχει λάθη, σταματάμε εδώ
        }

        //Δημιουργια user και protographer στην βαση δεδομένων
        User user = new User(password.getText(), email.getText(), "USER");
        UserDao userDao = new UserDao();
        userDao.registerUser(user, fullName.getText());





        // Μεταφορα στο login
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(RegisterController.class.getResource("/com/example/photometal1/Login-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load());

        // 3. Αλλάζεις τη σκηνή
        stage.setScene(scene);
        stage.show();

    }

    @FXML
    private void onButtonLogin(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource("/com/example/photometal1/Login-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load());

        // 3. Αλλάζεις τη σκηνή
        stage.setScene(scene);
        stage.show();

    }

    private boolean validateInputs() {
        String fullNameText = fullName.getText() == null ? "" : fullName.getText().trim();
        String emailText = email.getText() == null ? "" : email.getText().trim();
        String passwordText = password.getText() == null ? "" : password.getText();
        String confirmText = confirmPassword.getText() == null ? "" : confirmPassword.getText();

        StringBuilder errors = new StringBuilder();

        // 1. Empty fields
        if (fullNameText.isEmpty()) {
            errors.append("• Το πεδίο Full name είναι υποχρεωτικό.\n");
        }
        if (emailText.isEmpty()) {
            errors.append("• Το πεδίο Email είναι υποχρεωτικό.\n");
        }
        if (passwordText.isEmpty()) {
            errors.append("• Το πεδίο Password είναι υποχρεωτικό.\n");
        }
        if (confirmText.isEmpty()) {
            errors.append("• Το πεδίο Confirm password είναι υποχρεωτικό.\n");
        }

        // Αν ήδη έχουμε λάθη για κενά πεδία, σταματάμε εδώ
        if (errors.length() > 0) {
            showErrorAlert("Λάθος στοιχεία", errors.toString());
            return false;
        }

        // 2. Έλεγχος email (μόνο συγκεκριμένα domains π.χ. gmail, yahoo, hotmail)
        // Παράδειγμα: name@gmail.com, name@yahoo.com, name@hotmail.com
        if (!emailText.matches("^[A-Za-z0-9._%+-]+@(gmail\\.com|yahoo\\.com|hotmail\\.com)$")) {
            errors.append("• Το email πρέπει να είναι έγκυρο (gmail.com, yahoo.com ή hotmail.com).\n");
        }

        // 3. Έλεγχος password (μήκος + κεφαλαίο + μικρό + νούμερο)
        boolean lengthOk = passwordText.length() >= 8;
        boolean hasUpper = passwordText.matches(".*[A-Z].*");
        boolean hasLower = passwordText.matches(".*[a-z].*");
        boolean hasDigit = passwordText.matches(".*\\d.*");

        if (!lengthOk || !hasUpper || !hasLower || !hasDigit) {
            errors.append("• Το password πρέπει να έχει τουλάχιστον 8 χαρακτήρες, ")
                    .append("ένα κεφαλαίο γράμμα, ένα μικρό γράμμα και έναν αριθμό.\n");
        }

        // 4. Password == Confirm
        if (!passwordText.equals(confirmText)) {
            errors.append("• Τα password δεν ταιριάζουν.\n");
        }

        // Αν υπάρχουν λάθη, δείξε alert
        if (errors.length() > 0) {
            showErrorAlert("Λάθος στοιχεία", errors.toString());
            return false;
        }

        return true;
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    


}
