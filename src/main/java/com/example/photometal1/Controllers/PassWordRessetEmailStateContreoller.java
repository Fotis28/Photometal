package com.example.photometal1.Controllers;

import com.example.photometal1.Dao.UserDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class PassWordRessetEmailStateContreoller {

    @FXML
    private TextField email;

    @FXML
    private TextField password;


    @FXML
    private Button verifyEmailButton;

    @FXML
    private Button resetButton;

    // Μεταβλητή για να κρατήσουμε το ID του χρήστη μετά την επαλήθευση
    private int verifiedUserId = -1;


    @FXML
    private void OnCheckEmail(ActionEvent event) {


        // 1. Έλεγχος Validation
        if(!(validateEmail(email.getText()))) {
            System.out.println("Email not valid");
            return;
        }
         UserDao userDao = new UserDao();
        // 2. Αναζήτηση Χρήστη στη Βάση
        verifiedUserId = userDao.findUserIdWithEmail(email.getText());
        System.out.println("Verified ID: " + verifiedUserId);

        if (verifiedUserId != -1) {
            // --- ΜΕΤΑΒΑΣΗ ΦΑΣΗΣ ---

            // Α) Απενεργοποίηση και Απόκρυψη ΦΑΣΗΣ 1
            email.setDisable(true);          // Απενεργοποιούμε το email (ώστε να μην μπορεί να αλλάξει)
            email.setVisible(false);
            verifyEmailButton.setDisable(true);
            verifyEmailButton.setVisible(false);

            // Β) Ενεργοποίηση και Εμφάνιση ΦΑΣΗΣ 2
            password.setDisable(false);
            password.setVisible(true);
            resetButton.setDisable(false);
            resetButton.setVisible(true);

            System.out.println("Εισαγωγή καινούργιου κωδικού");

        } else {
            // Αν το email είναι έγκυρο σε μορφή αλλά δεν βρέθηκε στη βάση:
            showErrorAlert("Σφάλμα Αναζήτησης", "Δεν βρέθηκε χρήστης με αυτό το email.");
        }

    }

    @FXML
    private void onButtonResetPassword(ActionEvent event) throws IOException {
        if (verifiedUserId != -1) {
            if(validatePassword(password.getText())){
                UserDao userDao = new UserDao();
                userDao.updatePasswordOfUserID(verifiedUserId, password.getText());
                System.out.println("Password changed");
                // 3. Αλλάζεις τη σκηνή
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource("/com/example/photometal1/Login-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                stage.setScene(scene);
                stage.show();

            }
        }
    }


    private boolean validateEmail(String emailText) {
        StringBuilder errors = new StringBuilder();

        if (emailText.isEmpty()) {
            errors.append("• Το πεδίο Email είναι υποχρεωτικό.\n");
        }
        // Αν ήδη έχουμε λάθη για κενά πεδία, σταματάμε εδώ

        if (!emailText.matches("^[A-Za-z0-9._%+-]+@(gmail\\.com|yahoo\\.com|hotmail\\.com)$")) {
            errors.append("• Το email πρέπει να είναι έγκυρο (gmail.com, yahoo.com ή hotmail.com).\n");
        }
        if (errors.length() > 0) {
            showErrorAlert("Λάθος στοιχεία", errors.toString());
            return false;
        }
        return true;
    }

    private boolean validatePassword(String passwordText) {
        StringBuilder errors = new StringBuilder();
        if (passwordText.isEmpty()) {
            errors.append("• Το πεδίο Password είναι υποχρεωτικό.\n");
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
        if (errors.length() > 0) {
            showInfoAlert("Λάθος στοιχεία", errors.toString());
            return false;
        }

        return true; // επιστρέφει true αν περάσει
    }

    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
