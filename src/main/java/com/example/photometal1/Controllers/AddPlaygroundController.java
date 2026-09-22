package com.example.photometal1.Controllers;

import com.example.photometal1.Dao.PlaygroundDao;
import com.example.photometal1.Models.Playground;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AddPlaygroundController {

    @FXML
    private VBox root_id;

    @FXML
    private TextField nameField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField openTimeField;





    @FXML
    private void handleSave(ActionEvent event){

        String name = nameField.getText();
        String address = addressField.getText();
        String phone = phoneField.getText();
        String openTime = openTimeField.getText();

        if (name == null || name.isBlank() ||
                address == null || address.isBlank() ||
                phone == null || phone.isBlank() ||
                openTime == null || openTime.isBlank()) {

            showError("Παρακαλώ συμπληρώστε όλα τα πεδία.");
            return;
        }

        // --------------------------
        // 2. Έλεγχος Μήκους Ονόματος
        // --------------------------
        if (name.length() > 200) {
            showError("Το όνομα προϊόντος δεν μπορεί να ξεπερνά τους 15 χαρακτήρες.");
            return;
        }

        // --------------------------
        // 3. Έλεγχος Μήκους Περιγραφής
        // --------------------------
        if (address.length() > 500) {
            showError("Η περιγραφή δεν μπορεί να ξεπερνά τους 25 χαρακτήρες.");
            return;
        }
        // --------------------------
        // 4. Έλεγχος Μήκους Phone
        // --------------------------
        if (phone.length() > 15) {
            showError("Το phone δεν μπορεί να ξεπερνά τους 15 χαρακτήρες.");
            return;
        }
        // --------------------------
        // 5. Έλεγχος Μήκους OpenTime
        // --------------------------
        if (openTime.length() > 40) {
            showError("Το OpenTime δεν μπορεί να ξεπερνά τους 15 χαρακτήρες.");
            return;
        }

        if (!(phone.contains("69") || phone.contains("2310"))) {
            showError("Το phone δεν ειναι σωστο.");
            return;
        }

        Playground playground = new Playground(name, address, phone, openTime, false);
        PlaygroundDao playgroundDao = new PlaygroundDao();

        try {
            playgroundDao.createPlayground(playground);
            Stage stage = (Stage) root_id.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            System.out.println("❌ Error creating table: " + e.getMessage());
        }





    }






    @FXML
    private void handleCancel(ActionEvent event){
        Stage stage = (Stage) root_id.getScene().getWindow();
        stage.close();

    }



    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Σφάλμα");
        alert.setHeaderText("Μη έγκυρη εισαγωγή");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
