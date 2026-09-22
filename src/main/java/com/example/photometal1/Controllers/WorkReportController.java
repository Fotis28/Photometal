package com.example.photometal1.Controllers;

import com.example.photometal1.Dao.WorkReportDao;
import com.example.photometal1.GlobalMethods.SessionManager;
import com.example.photometal1.GlobalMethods.Spliter_Screen;
import com.example.photometal1.Models.PlaygroundSimple;
import com.example.photometal1.Models.ProductSaleItem;
import com.example.photometal1.Models.TopProductStat;
import com.example.photometal1.Models.work_reports;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class WorkReportController {



    @FXML
    private TableView<work_reports> workReportTable;

    @FXML
    private TableColumn<work_reports, String> colPlayground;
    @FXML
    private TableColumn<work_reports, Date> colDate;       // αντί για LocalDate
    @FXML
    private TableColumn<work_reports, Integer> colParty;
    @FXML
    private TableColumn<work_reports, Integer> colCustomers;
    @FXML
    private TableColumn<work_reports, Integer> colPapers;
    @FXML
    private TableColumn<work_reports, Integer> colTotal;   // αντί για BigDecimal
    @FXML
    private TableColumn<work_reports, Void> colAction;

    @FXML
    private Label lblTop1;
    @FXML
    private Label lblTop2;
    @FXML
    private Label lblTop3;

    private final WorkReportDao workReportDao = new WorkReportDao();



    @FXML
    public void initialize() {
        // 1) Δέσιμο των στηλών με τα properties του model
        colPlayground.setCellValueFactory(new PropertyValueFactory<>("playgroundName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("report_date"));
        colParty.setCellValueFactory(new PropertyValueFactory<>("parties"));
        colCustomers.setCellValueFactory(new PropertyValueFactory<>("customer"));
        colPapers.setCellValueFactory(new PropertyValueFactory<>("papers"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        // 2) Φόρτωση δεδομένων από τη βάση (DAO)
        // Εδώ θα καλέσεις τον δικό σου DAO.
        // Παράδειγμα:

        workReportTable.setItems(FXCollections.observableArrayList(workReportDao.findAll()));

        // 3) Double-click σε γραμμή για επεξεργασία
        workReportTable.setRowFactory(tv -> {
            TableRow<work_reports> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    work_reports selected = row.getItem();
                    openEditDialog(selected);
                }
            });
            return row;
        });

        // 4) Στήλη με “βελάκι” κουμπί
        addActionButtonToTable();
        loadTopProductsStats();
    }
    private void reloadTable() {
        workReportTable.setItems(

                FXCollections.observableArrayList(workReportDao.findAll())
        );
        loadTopProductsStats();
    }


    @FXML
    protected void onHelloButtonClick(ActionEvent event) throws IOException {

        // 3. Αλλάζεις τη σκηνή
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(WorkReportController.class.getResource("/com/example/photometal1/WorkReportUsers-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();


    }

    @FXML
    protected void onSettingButtonClick(ActionEvent event) throws IOException {
        Spliter_Screen.MoveForUser(event,"Setting");
    }

    @FXML
    protected void onLogoutClick(ActionEvent event) throws IOException {
        SessionManager.clearSession();
        Spliter_Screen.MoveForUser(event,"Log_out");
    }



    private void addActionButtonToTable() {

        colAction.setCellFactory(col -> new TableCell<>() {

            private final Button btn = new Button("▶");

            {
                btn.getStyleClass().add("action-arrow-button"); // θα το στυλάρουμε με CSS
                btn.setOnAction(event -> {
                    work_reports report = getTableView().getItems().get(getIndex());
                    openEditDialog(report);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void openEditDialog(work_reports report) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/photometal1/AddDocument-view.fxml"));
            Parent root = loader.load();

            // Παίρνουμε τον controller της φόρμας
            AddDocumentController addController = loader.getController();

            // ΛΕΜΕ στη φόρμα ότι δουλεύουμε σε EDIT mode
            addController.setExistingReport(report);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Επεξεργασία Αναφοράς Εργασίας");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            // Μετά την αποθήκευση, ανανέωσε τον πίνακα

            reloadTable();
            loadTopProductsStats();

        } catch (IOException e) {
            e.printStackTrace();
            // Προαιρετικά Alert
        }
    }
    @FXML
    protected void onDocumentButtonClickADD(ActionEvent event) throws IOException {

        try {
            // 1. Δημιουργία FXMLLoader και φόρτωση του Dialog FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/photometal1/AddDocument-view.fxml"));
            Parent root = loader.load();

            // 2. Λήψη του Controller του Dialog. Αυτό είναι κρίσιμο για την επικοινωνία.
            AddDocumentController addController = loader.getController();

            // 3. Δημιουργία νέου Stage (Παραθύρου)
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Δημιουργία Νέου Πάρκου");

            // 4. Ρύθμιση Modality: Κάνουμε το παράθυρο MODAL
            //    (Δεν επιτρέπει αλληλεπίδραση με το κύριο παράθυρο μέχρι να κλείσει αυτό)
            dialogStage.initModality(Modality.WINDOW_MODAL);

//            // 5. Ορισμός ιδιοκτήτη: Βοηθάει στην οργάνωση των παραθύρων
//            //    (Παίρνουμε το Stage του κουμπιού που πατήθηκε)
//            dialogStage.initOwner(newParkButton.getScene().getWindow());

            // 6. Ορισμός Scene και εμφάνιση
            dialogStage.setScene(new Scene(root));

            // 7. Εμφάνιση του Dialog και Αναμονή (μπλοκάρει το νήμα)
            dialogStage.showAndWait();
            reloadTable();
            loadTopProductsStats();
        } catch (IOException e) {
            // ... χειρισμός σφάλματος
        }

    }

    private void loadTopProductsStats() {
        List<TopProductStat> topList = workReportDao.getTopProductsForCurrentUser(3);

        lblTop1.setText("Top 1: -");
        lblTop2.setText("Top 2: -");
        lblTop3.setText("Top 3: -");

        if (topList.size() > 0) {
            TopProductStat p1 = topList.get(0);
            lblTop1.setText(
                    "Top 1:\n" +
                            p1.getProductName() + "\n" +
                            p1.getProductDescription() + "\n" +
                            "Πωλήσεις: " + p1.getTotalSold()
            );
        }

        if (topList.size() > 1) {
            TopProductStat p2 = topList.get(1);
            lblTop2.setText(
                    "Top 2:\n" +
                            p2.getProductName() + "\n" +
                            p2.getProductDescription() + "\n" +
                            "Πωλήσεις: " + p2.getTotalSold()
            );
        }

        if (topList.size() > 2) {
            TopProductStat p3 = topList.get(2);
            lblTop3.setText(
                    "Top 3:\n" +
                            p3.getProductName() + "\n" +
                            p3.getProductDescription() + "\n" +
                            "Πωλήσεις: " + p3.getTotalSold()
            );
        }
    }

}





