package com.example.photometal1.Controllers;

import com.example.photometal1.Dao.UserDao;
import com.example.photometal1.GlobalMethods.SessionManager;
import com.example.photometal1.GlobalMethods.Spliter_Screen;
import com.example.photometal1.Models.UserSettings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class UserSettingsController {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtFullName;
    @FXML private DatePicker dpHireDate;

    private final UserDao userDao = new UserDao();
    private UserSettings currentSettings;

    @FXML
    public void initialize() {
        try {
            currentSettings = userDao.getCurrentUserSettings();
            if (currentSettings != null) {
                txtEmail.setText(currentSettings.getEmail());
                txtPassword.setPromptText("Αφήστε κενό για να μην αλλάξει");
                txtFullName.setText(currentSettings.getFullName());
                if (currentSettings.getHireDate() != null) {
                    dpHireDate.setValue(currentSettings.getHireDate());
                }

                // 🔒 Δεν επιτρέπουμε αλλαγές σε email / hireDate
                txtEmail.setEditable(false);
                txtEmail.setDisable(true);      // αν θέλεις να είναι τελείως “γκρι”
                dpHireDate.setEditable(false);
                dpHireDate.setDisable(true);
            }
        } catch (Exception e) {
            showError("Σφάλμα φόρτωσης ρυθμίσεων: " + e.getMessage());
        }
    }


    @FXML
    protected void onHomeButtonClick(ActionEvent event) throws IOException {
        Spliter_Screen.MoveForUser(event,"Home");

    }

   @FXML
    protected void onLogoutClick(ActionEvent event) throws IOException {
        SessionManager.clearSession();
        Spliter_Screen.MoveForUser(event,"Log_out");
    }

    @FXML
    protected void onSettingButtonClick(ActionEvent event) throws IOException {
        Spliter_Screen.MoveForUser(event,"Settings");
    }




    @FXML
    private void handleSave() {
        String password = txtPassword.getText();
        String fullName = txtFullName.getText();

        StringBuilder errors = new StringBuilder();

        boolean changePassword = password != null && !password.isBlank();
        if (changePassword && password.length() < 8) {
            errors.append("- Ο νέος κωδικός πρέπει να έχει τουλάχιστον 8 χαρακτήρες.\n");
        }
        if (fullName == null || fullName.isBlank()) {
            errors.append("- Συμπληρώστε ονοματεπώνυμο.\n");
        }

        if (errors.length() > 0) {
            showError(errors.toString());
            return;
        }

        try {
            userDao.updateCurrentUserProfile(changePassword ? password : null, fullName);
            txtPassword.clear();
            showInfo("Οι αλλαγές αποθηκεύτηκαν με επιτυχία.");
        } catch (Exception e) {
            showError("Σφάλμα αποθήκευσης: " + e.getMessage());
        }
    }


    @FXML
    private void handleDeleteAccount(ActionEvent event)throws IOException {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Επιβεβαίωση");
        confirm.setHeaderText("Διαγραφή λογαριασμού");
        confirm.setContentText("Είσαι σίγουρος ότι θέλεις να διαγράψεις τον λογαριασμό σου;");

        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    userDao.deleteUser();

                    // κλείσε το παράθυρο (και ιδανικά γύρνα σε login από αλλού)
                    SessionManager.clearSession(); // αν έχεις τέτοια μέθοδο
                    Spliter_Screen.MoveForUser(event,"Log_out");

                } catch (Exception e) {
                    showError("Σφάλμα διαγραφής: " + e.getMessage());
                }
            }
        });


    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Σφάλμα");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Πληροφορία");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
