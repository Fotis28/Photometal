package com.example.photometal1.Controllers;


import com.example.photometal1.Dao.WorkReportDao;
import com.example.photometal1.GlobalMethods.SessionManager;
import com.example.photometal1.GlobalMethods.Spliter_Screen;
import com.example.photometal1.Models.AdminDocumentRow;
import com.example.photometal1.Models.PhotographerSimple;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

public class DocumentAdminController {

    @FXML
    private ComboBox<PhotographerSimple> listOfPhotographers;

    @FXML
    private TableView<AdminDocumentRow> tblDocuments;

    @FXML
    private TableColumn<AdminDocumentRow, String> colPhotographer;

    @FXML
    private TableColumn<AdminDocumentRow, String> colPlayground;

    @FXML
    private TableColumn<AdminDocumentRow, java.sql.Date> colDate;

    @FXML
    private TableColumn<AdminDocumentRow, Integer> colParties;

    @FXML
    private TableColumn<AdminDocumentRow, Integer> colCustomers;

    @FXML
    private TableColumn<AdminDocumentRow, Integer> colPapers;

    @FXML
    private TableColumn<AdminDocumentRow, Double> colTotal;

    private final WorkReportDao dao = new WorkReportDao();

    @FXML
    public void initialize() {
        // 1) Δέσιμο στηλών με properties του AdminDocumentRow
        colPhotographer.setCellValueFactory(new PropertyValueFactory<>("photographerName"));
        colPlayground.setCellValueFactory(new PropertyValueFactory<>("playgroundName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("reportDate"));
        colParties.setCellValueFactory(new PropertyValueFactory<>("parties"));
        colCustomers.setCellValueFactory(new PropertyValueFactory<>("customers"));
        colPapers.setCellValueFactory(new PropertyValueFactory<>("papers"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        // 2) Γέμισμα ComboBox με φωτογράφους
        ObservableList<PhotographerSimple> photographers = dao.getAllPhotographersSimple();
        listOfPhotographers.setItems(photographers);

        // Optional: αν θες να διαλέγεται ο πρώτος αυτόματα
        if (!photographers.isEmpty()) {
            listOfPhotographers.getSelectionModel().selectFirst();
            loadReportsForSelectedPhotographer();
        }

        // 3) Listener: όταν αλλάζει επιλογή στο ComboBox, ανανεώνει ο πίνακας
        listOfPhotographers.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            loadReportsForSelectedPhotographer();
        });
    }

    private void loadReportsForSelectedPhotographer() {
        PhotographerSimple selected = listOfPhotographers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            tblDocuments.getItems().clear();
            return;
        }

        ObservableList<AdminDocumentRow> reports =
                dao.getReportsForPhotographer(selected.getId());

        tblDocuments.setItems(reports);
    }

    // ==== menu buttons (όπως στα άλλα controllers σου) ====
    @FXML
    protected void onHelloButtonClick(ActionEvent event) throws IOException {
        Spliter_Screen.MoveForAdmin(event, "Home");
    }

    @FXML
    protected void onPlaygroundsButtonClickADD(ActionEvent event) throws IOException {
        Spliter_Screen.MoveForAdmin(event, "Playgrounds");
    }

    @FXML
    protected void onProductButtonClick(ActionEvent event) throws IOException {
        Spliter_Screen.MoveForAdmin(event, "Products");
    }

    @FXML
    protected void openLogView(ActionEvent event) throws IOException {
        Spliter_Screen.MoveForAdmin(event, "Log");
    }

    @FXML
    protected void onLogoutClick(ActionEvent event) throws IOException {
        SessionManager.clearSession();
        Spliter_Screen.MoveForUser(event, "Log_out");
    }
}
