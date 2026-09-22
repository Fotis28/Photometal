package com.example.photometal1.Controllers;

import com.example.photometal1.Dao.LogAuditDao;
import com.example.photometal1.GlobalMethods.SessionManager;
import com.example.photometal1.GlobalMethods.Spliter_Screen;
import com.example.photometal1.Models.Log_audit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;


public class LogAuditAdminController {
    @FXML private TableView<Log_audit> logTable;

    @FXML private TableColumn<Log_audit, Integer> idColumn;
    @FXML private TableColumn<Log_audit, String>  userColumn;
    @FXML private TableColumn<Log_audit, String>  actionColumn;
    @FXML private TableColumn<Log_audit, String>  tableNameColumn;
    @FXML private TableColumn<Log_audit, Integer> recordColumn;
    @FXML private TableColumn<Log_audit, String>  newDataColumn;
    @FXML private TableColumn<Log_audit, String>  oldDataColumn;
    @FXML private TableColumn<Log_audit, String>  timeColumn;

    @FXML private ComboBox<String> tableFilter;

    private final LogAuditDao logDao = new LogAuditDao();

    @FXML
    public void initialize() {

        idColumn.setCellValueFactory(new PropertyValueFactory<>("log_audit_id"));
        userColumn.setCellValueFactory(new PropertyValueFactory<>("full_name"));     // ΜΟΝΟ αυτό
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("log_time"));
        tableNameColumn.setCellValueFactory(new PropertyValueFactory<>("table_name"));
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("action_type"));
        recordColumn.setCellValueFactory(new PropertyValueFactory<>("record_id"));
        oldDataColumn.setCellValueFactory(new PropertyValueFactory<>("old_data"));
        newDataColumn.setCellValueFactory(new PropertyValueFactory<>("new_data"));

        tableFilter.setItems(FXCollections.observableArrayList(
                "Όλα", "users", "playgrounds", "products",
                "work_reports", "report_details", "store_house_stock"
        ));

        tableFilter.setValue("Όλα");

        loadLog();
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
    private void loadLog() {
        String selected = tableFilter.getValue();
        ObservableList<Log_audit> list =
                FXCollections.observableArrayList(logDao.getLog(selected));
        logTable.setItems(list);
    }

    @FXML
    private void onLogoutClick(ActionEvent event) throws IOException
    {
        SessionManager.clearSession();
        Spliter_Screen.MoveForUser(event,"Log_out");
    }


}

