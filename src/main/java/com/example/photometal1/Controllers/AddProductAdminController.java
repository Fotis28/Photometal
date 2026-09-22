package com.example.photometal1.Controllers;

import com.example.photometal1.Dao.ProductsDao;
import com.example.photometal1.Models.Product;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class AddProductAdminController {

@FXML
private AnchorPane root_id;

@FXML
private TextField nameField;

@FXML
private TextField priceField;

@FXML
private TextField DescriptionField;



    private Product productToEdit;

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) root_id.getScene().getWindow();
        stage.close();
    }


    @FXML
    private void handleSave() {

        String name = nameField.getText();
        String description = DescriptionField.getText();
        String priceText = priceField.getText();
        ProductsDao productsDao = new ProductsDao();




          // --------------------------
          // 1. Έλεγχος Κενών Πεδίων
          // --------------------------
          if (name == null || name.isBlank() ||
                  description == null || description.isBlank() ||
                  priceText == null || priceText.isBlank()) {

              showError("Παρακαλώ συμπληρώστε όλα τα πεδία.");
              return;
          }

          // --------------------------
          // 2. Έλεγχος Μήκους Ονόματος
          // --------------------------
          if (name.length() > 70) {
              showError("Το όνομα προϊόντος δεν μπορεί να ξεπερνά τους 15 χαρακτήρες.");
              return;
          }

          // --------------------------
          // 3. Έλεγχος Μήκους Περιγραφής
          // --------------------------
          if (description.length() > 150) {
              showError("Η περιγραφή δεν μπορεί να ξεπερνά τους 25 χαρακτήρες.");
              return;
          }

          // --------------------------
          // 4. Έλεγχος Τιμής (αριθμός)
          // --------------------------
          BigDecimal price;
          try {
              price = new BigDecimal(priceText.replace(",", "."));
          } catch (NumberFormatException e) {
              showError("Η τιμή δεν είναι έγκυρος αριθμός.\nΠαράδειγμα: 12.50");
              return;
          }

          // --------------------------
          // 5. Έλεγχος αρνητικής τιμής
          // --------------------------
          if (price.compareTo(BigDecimal.ZERO) < 0) {
              showError("Η τιμή δεν μπορεί να είναι αρνητική.");
              return;
          }

          // --------------------------
          // 6. Αποθήκευση προϊόντος
          // --------------------------
          Product product = new Product(name, description, price);

      try {
          if (productToEdit != null) {
              boolean sameName        = name.equals(productToEdit.getName());
              boolean sameDescription = description.equals(productToEdit.getdescription());
              boolean samePrice       = price.compareTo(productToEdit.getPrice()) == 0;
              if (sameName && sameDescription && samePrice) {
                  // Δεν άλλαξε τίποτα → δεν κάνουμε update
                  Alert alert = new Alert(Alert.AlertType.INFORMATION);
                  alert.setTitle("Καμία αλλαγή");
                  alert.setHeaderText(null);
                  alert.setContentText("Δεν κάνατε καμία αλλαγή στο προϊόν.");
                  alert.showAndWait();
              }
              else {
                  product.setId(productToEdit.getId());
                  productsDao.updateProduct(product);
              }
          }
          else {
              productsDao.createProduct(product);
          }

      }
      catch (Exception e) {
          e.printStackTrace();
      }



        // --------------------------
        // 7. Κλείσιμο παραθύρου
        // --------------------------
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

    public void setProductToEdit(Product product) {
        this.productToEdit = product;
        nameField.setText(product.getName());
        DescriptionField.setText(product.getdescription());
        priceField.setText(product.getPrice().toString());
    }

}
