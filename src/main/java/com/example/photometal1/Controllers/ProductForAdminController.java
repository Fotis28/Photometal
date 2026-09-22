package com.example.photometal1.Controllers;

import com.example.photometal1.Dao.ProductsDao;
import com.example.photometal1.GlobalMethods.SessionManager;
import com.example.photometal1.GlobalMethods.Spliter_Screen;
import com.example.photometal1.Models.Product;
import com.example.photometal1.Models.ProductSaleItem;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;

public class ProductForAdminController {

    @FXML
    private TableView<Product> productsTable;

    @FXML
    private TableColumn<Product, Integer> idColumn;

    @FXML
    private TableColumn<Product, String> nameColumn;

    @FXML
    private TableColumn<Product, String> descriptionColumn;

    @FXML
    private TableColumn<Product, String> priceColumn;

    @FXML
    private RadioButton Exist_Products;

    @FXML
    private RadioButton Not_Exist_Products;

    @FXML
    private TableColumn<Product, Void> actionsColumn;

    private final ToggleGroup toggleGroup = new ToggleGroup();
    private final ProductsDao productsDao = new ProductsDao();





@FXML
public void initialize() {

    // ------------ 1) Συνδέουμε τα Radio Buttons ------------
    Exist_Products.setToggleGroup(toggleGroup);
    Not_Exist_Products.setToggleGroup(toggleGroup);
    Exist_Products.setSelected(true);   // default


    // ------------ 2) Στήνουμε μία φορά τα Columns ------------

    idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

    nameColumn.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().getName()));

    descriptionColumn.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().getdescription()));

    priceColumn.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().getPrice().toString()));


    // ------------ 3) Φόρτωσε αρχικά Exist Products ------------
    // 1η φόρτωση
    loadProducts();

    // ✅ ΠΟΛΥ ΣΗΜΑΝΤΙΚΟ: στήσιμο actionsColumn
    setupActionsColumn();

    // Listener στα radio buttons
    toggleGroup.selectedToggleProperty().addListener((obs, oldT, newT) -> loadProducts());

}



    @FXML
    private void onHelloButtonClick(ActionEvent event) throws IOException {
        Spliter_Screen.MoveForAdmin(event,"Home");

    }

    @FXML
    protected void onProductButtonClick(ActionEvent event) throws IOException
    {
        Spliter_Screen.MoveForAdmin(event,"ProductForAdmin");
    }


    @FXML
    private void openLogView(ActionEvent event)throws IOException {



        Spliter_Screen.MoveForAdmin(event,"Log_auditAdmin");



    }
    @FXML
    protected void onPlaygroundsButtonClickADD(ActionEvent event) throws IOException
    {
        Spliter_Screen.MoveForAdmin(event,"Playgrounds");
    }

    @FXML
    protected void onDocumentButtonClick(ActionEvent event) throws IOException
    {
        Spliter_Screen.MoveForAdmin(event,"Documents");
    }

    @FXML
    protected void onLogoutClick(ActionEvent event) throws IOException
    {
        SessionManager.clearSession();
        Spliter_Screen.MoveForAdmin(event,"Log_out");
    }



    private void loadProducts() {
        try {

            if (Exist_Products.isSelected()) {
                ObservableList<Product> list = productsDao.getAllProductsExist();
                productsTable.setItems(list);
            }
            else {
                ObservableList<Product> list = productsDao.getAllProductsNotExist();
                productsTable.setItems(list);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    @FXML
    private void addNewProduct(){
     try {
      // 1. Δημιουργία FXMLLoader και φόρτωση του Dialog FXML
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/photometal1/AddProductAdmin-view.fxml"));
      Parent root = loader.load();

      // 2. Λήψη του Controller του Dialog. Αυτό είναι κρίσιμο για την επικοινωνία.
      AddProductAdminController addController = loader.getController();

      // 3. Δημιουργία νέου Stage (Παραθύρου)
      Stage dialogStage = new Stage();
      dialogStage.setTitle("Δημιουργία Νέου Προιόντος");

      // (Δεν επιτρέπει αλληλεπίδραση με το κύριο παράθυρο μέχρι να κλείσει αυτό)
      dialogStage.initModality(Modality.WINDOW_MODAL);
      dialogStage.setScene(new Scene(root));
      dialogStage.showAndWait();

         //Μετά το κλείσιμο, ξαναφόρτωσε τη λίστα
         loadProducts();

     } catch (IOException e) {
      // ... χειρισμός σφάλματος
     }


    }


    private void setupActionsColumn() {
        actionsColumn.setCellFactory(col -> new TableCell<Product, Void>() {

            private final ProductsDao productsDao = new ProductsDao();

            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final Button restoreButton = new Button("Restore");

            private final HBox normalActions = new HBox(8, editButton, deleteButton);
            private final HBox restoreActions = new HBox(8, restoreButton);

            {
                normalActions.setAlignment(javafx.geometry.Pos.CENTER);
                restoreActions.setAlignment(javafx.geometry.Pos.CENTER);

                // ===== EDIT =====
                editButton.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    openEditDialog(product);
                });

                // ===== DELETE (soft) =====
                deleteButton.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    try {
                        productsDao.deleteProduct(product.getId());
                        loadProducts();  // refresh μετά το delete
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                // ===== RESTORE (undo delete) =====
                restoreButton.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    try {
                        productsDao.restoreProduct(product.getId());
                        loadProducts();  // refresh μετά το restore
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                Product product = getTableView().getItems().get(getIndex());

                // Αν δεν είναι deleted → δείξε Edit + Delete
                if (!product.get_is_deleted()) {
                    setGraphic(normalActions);
                } else {
                    // Αν είναι deleted → δείξε ΜΟΝΟ Restore
                    setGraphic(restoreActions);
                }
            }
        });
    }


    private void openEditDialog(Product product) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/photometal1/AddProductAdmin-view.fxml"));
            Parent root = loader.load();

            // Παίρνουμε τον controller του dialog
            AddProductAdminController controller = loader.getController();
            // Του περνάμε το προϊόν προς επεξεργασία
            controller.setProductToEdit(product);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Product");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            // Μετά το κλείσιμο του dialog, κάνουμε refresh τον πίνακα
            loadProducts();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
