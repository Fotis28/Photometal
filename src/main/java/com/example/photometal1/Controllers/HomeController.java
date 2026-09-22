package com.example.photometal1.Controllers;

import com.example.photometal1.GlobalMethods.SessionManager;
import com.example.photometal1.GlobalMethods.Spliter_Screen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import com.example.photometal1.Dao.ProductsDao;
import com.example.photometal1.Models.Product;
import com.example.photometal1.Models.MonthlySale;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.ComboBox;
import java.io.IOException;
import java.time.LocalDate;

public class HomeController{
    @FXML
    private Label welcomeText;
    @FXML
    private ComboBox<Product> cbProducts;

    @FXML
    private LineChart<String, Number> productSalesChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    private final ProductsDao productsDao = new ProductsDao();




    @FXML
    private void onProductSelected() {
        loadChartForSelectedProduct();
    }

    @FXML
    private void initialize() {
        loadProductsForChart();



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
        //SessionManager.clearSession();
        Spliter_Screen.MoveForUser(event,"Log_out");

    }

    private void loadProductsForChart() {
        try {
            ObservableList<Product> products = productsDao.getAllProductsExist();
            cbProducts.setItems(products);

            if (!products.isEmpty()) {
                cbProducts.getSelectionModel().selectFirst();
                loadChartForSelectedProduct();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void loadChartForSelectedProduct() {
        Product selected = cbProducts.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        int currentYear = LocalDate.now().getYear(); // <--- ΕΔΩ ΤΟ ΒΑΖΕΙΣ

        try {
            ObservableList<MonthlySale> sales =
                    productsDao.getMonthlySalesForProductYear(selected.getId(), currentYear);

            productSalesChart.getData().clear();

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(selected.getName() + " - " + currentYear);

            for (MonthlySale ms : sales) {
                series.getData().add(
                        new XYChart.Data<>(ms.getMonthLabel(), ms.getTotalSold())
                );
            }

            productSalesChart.getData().add(series);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}