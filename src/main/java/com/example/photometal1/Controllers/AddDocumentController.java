package com.example.photometal1.Controllers;// AddDocumentController.java

import com.example.photometal1.Dao.PlaygroundDao;
import com.example.photometal1.Dao.ProductsDao;
import com.example.photometal1.Dao.WorkReportDao;
import com.example.photometal1.GlobalMethods.SessionManager;
import com.example.photometal1.Models.PlaygroundSimple;
import com.example.photometal1.Models.ProductSaleItem;
import com.example.photometal1.Models.report_details;
import com.example.photometal1.Models.work_reports;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
//import com.example.photometal1.Dao.WorkReportDao; // Το DAO για την εισαγωγή
// ... (άλλα απαραίτητα imports, π.χ., DatePicker, ComboBox)

public class AddDocumentController {

    @FXML
    private DatePicker datePicker;

  @FXML
  private ComboBox<PlaygroundSimple> playgroundCombo;

  @FXML
  private TextField partiesField;

  @FXML
  private TextField customersField;

  @FXML
  private TextField papersField;

    @FXML private TableView<ProductSaleItem> salesTable;
    @FXML private TableColumn<ProductSaleItem, String> productColumn;
    @FXML private TableColumn<ProductSaleItem, Integer> quantityColumn;

    private work_reports currentReport;       // null = νέο, όχι null = edit
    private boolean editMode = false;

    private final PlaygroundDao playgroundDao = new PlaygroundDao();
    private final ProductsDao productsDao = new ProductsDao();
    private final WorkReportDao workReportDao = new WorkReportDao();


    public void initialize() {

        PlaygroundDao playgroundDao = new PlaygroundDao();
        ProductsDao productsDao = new ProductsDao();

        try {
            // 1. Γέμισε combo playground
            ObservableList<PlaygroundSimple> playgroundList = playgroundDao.getAllPlaygroundsSimple();
            playgroundCombo.setItems(playgroundList);

            // 2. Γέμισε Table με προϊόντα
            ObservableList<ProductSaleItem> productsList = productsDao.getAllProductsForSalesTable();
            salesTable.setItems(productsList);


            productColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getDisplayName())
            );


            quantityColumn.setCellValueFactory(cellData ->
                    cellData.getValue().quantitySoldProperty().asObject()
            );

            // Κάνουμε το TableView editable
            salesTable.setEditable(true);

            // Επιτρέπουμε στον χρήστη να γράψει αριθμό
            quantityColumn.setCellFactory(
                    TextFieldTableCell.forTableColumn(new IntegerStringConverter())
            );

            // Αποθήκευση αλλαγών όταν ο χρήστης πατήσει Enter
            quantityColumn.setOnEditCommit(event -> {
                ProductSaleItem item = event.getRowValue();
                Integer newValue = event.getNewValue();
                if (newValue == null) newValue = 0;
                item.setQuantitySold(newValue);
            });



        } catch (Exception e) {
            System.out.println("❌ Error loading playgrounds: " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {

        StringBuilder errors = new StringBuilder();

        // 1. Ημερομηνία
        LocalDate reportDate = datePicker.getValue();
        if (reportDate == null) {
            errors.append("- Συμπληρώστε ημερομηνία.\n");
        }

        // 2. Playground
        PlaygroundSimple selectedPlayground = playgroundCombo.getSelectionModel().getSelectedItem();
        if (selectedPlayground == null) {
            errors.append("- Επιλέξτε πάρκο / κατάστημα.\n");
        }

        // 3. Parties
        String partiesText = partiesField.getText();
        if (partiesText == null || partiesText.isBlank()) {
            errors.append("- Συμπληρώστε το πλήθος Parties.\n");
        }

        // 4. Customers
        String customersText = customersField.getText();
        if (customersText == null || customersText.isBlank()) {
            errors.append("- Συμπληρώστε το πλήθος Customers.\n");
        }

        // 5. Papers
        String papersText = papersField.getText();
        if (papersText == null || papersText.isBlank()) {
            errors.append("- Συμπληρώστε το πλήθος Papers.\n");
        }

        // 6. Προϊόντα με ποσότητα > 0
        List<ProductSaleItem> soldItems = new ArrayList<>();

        for (ProductSaleItem item : salesTable.getItems()) {
            if (item.getQuantitySold() > 0) {
                soldItems.add(item);
            }
        }

//        if (soldItems.isEmpty()) {
//            errors.append("- Βάλτε τουλάχιστον μία ποσότητα προϊόντος (> 0).\n");
//        }

        Integer parties = safeParseInt(partiesText, "Parties", errors);
        Integer customers = safeParseInt(customersText, "Customers", errors);
        Integer papers = safeParseInt(papersText, "Papers", errors);

        // Αν υπάρχουν λάθη → Alert και return
        if (errors.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ελλιπή στοιχεία");
            alert.setHeaderText("Παρακαλώ συμπληρώστε τα παρακάτω πεδία:");
            alert.setContentText(errors.toString());
            alert.showAndWait();
            return;
        }

        // Αν ΟΛΑ είναι ΟΚ → Κάνουμε System.out με τα δεδομένα
        int playgroundId = selectedPlayground.getId();


        System.out.println("✅ Στοιχεία φόρμας:");
        System.out.println("Προϊόντα: " + soldItems.size());
        System.out.println("User_id: " + SessionManager.getUserId());
        System.out.println("Ημερομηνία: " + reportDate);
        System.out.println("Playground ID: " + playgroundId + " (" + selectedPlayground + ")");
        System.out.println("Parties: " + partiesText);
        System.out.println("Customers: " + customersText);
        System.out.println("Papers: " + papersText);

        System.out.println("✅ Πωλήσεις προϊόντων:");
        for (ProductSaleItem item : soldItems) {
            System.out.println(
                    "Product ID=" + item.getId() +
                            " | Name=" + item.getDisplayName() +
                            " | Quantity=" + item.getQuantitySold()
            );
        }

        if (!editMode) {
            // CREATE
            workReportDao.createWorkReportAndDetails(
                    reportDate,
                    playgroundId,
                    partiesText,
                    customersText,
                    papersText,
                    soldItems
            );
        } else {
            // UPDATE – πρέπει να υλοποιήσεις αυτή τη μέθοδο στο DAO
            workReportDao.updateWorkReportAndDetails(
                    currentReport.getId(),
                    reportDate,
                    playgroundId,
                    partiesText,
                    customersText,
                    papersText,
                    soldItems
            );

        }
        Stage stage = (Stage) datePicker.getScene().getWindow();
        stage.close();
    }


    @FXML
    private void handleCancel() {
        Stage stage = (Stage) datePicker.getScene().getWindow();
        stage.close();
    }

    private Integer safeParseInt(String text, String fieldName, StringBuilder errors) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            errors.append("- Το πεδίο ").append(fieldName).append(" πρέπει να είναι αριθμός.\n");
            return null;
        }
    }
    public void setExistingReport(work_reports report) {
        this.currentReport = report;
        this.editMode = true;


        if (report.getReport_date() != null) {
            java.util.Date a = report.getReport_date();
            // 1. Γέμισε header πεδία
            if (a instanceof java.sql.Date) {
                // 🔥 Ασφαλής τρόπος για sql.Date
                datePicker.setValue(((java.sql.Date) a).toLocalDate());
            } else {
                // Αν ποτέ είναι σκέτο java.util.Date
                datePicker.setValue(a.toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate());
            }
            partiesField.setText(String.valueOf(report.getParties()));
            customersField.setText(String.valueOf(report.getCustomer()));
            papersField.setText(String.valueOf(report.getPapers()));

            // 2. Επίλεξε σωστό playground στο combo
            for (PlaygroundSimple p : playgroundCombo.getItems()) {
                if (p.getId() == report.getPlaygroundId()) {
                    playgroundCombo.getSelectionModel().select(p);

                    break;
                }
            }

            // 3. Φόρτωσε λεπτομέρειες report από τη βάση
            var details = workReportDao.getReportDetails(report.getId()); // List<report_details>

            // 4. Πάρε όλα τα προϊόντα και γράψε πάνω τις quantities
            try {
                ObservableList<ProductSaleItem> productsList = productsDao.getAllProductsForSalesTable();

                for (ProductSaleItem item : productsList) {
                    for (report_details d : details) {
                        if (d.getProduct_id() == item.getId()) {
                            item.setQuantitySold(d.get_quantity_sold());
                            System.out.println("Item " + item.getId() + " -> quantity " + item.getQuantitySold());
                        }
                    }
                }

                salesTable.setItems(productsList);
            } catch (Exception e) {
                System.out.println("❌ Error loading products for edit: " + e.getMessage());
            }
        }
    }

}