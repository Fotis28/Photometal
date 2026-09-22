package com.example.photometal1.Controllers;

import com.example.photometal1.Dao.PlaygroundDao;
import com.example.photometal1.Dao.store_house_stockDao;
import com.example.photometal1.GlobalMethods.SessionManager;
import com.example.photometal1.GlobalMethods.Spliter_Screen;
import com.example.photometal1.Models.Playground;
import com.example.photometal1.Models.PlaygroundSimple;
import com.example.photometal1.Models.Store_House_Stock_For_View;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.event.Event;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;

public class PlaygroundsControlles {

    @FXML
    private ListView<PlaygroundSimple> ListView;

    @FXML
    private TextField nameField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField openingTimeField;

    @FXML
    private TableView<Store_House_Stock_For_View> stockTable;

    @FXML
    private TableColumn<Store_House_Stock_For_View, String> productColumn;

    @FXML
    private TableColumn<Store_House_Stock_For_View, Integer> quantityColumn;

    @FXML
    private javafx.scene.control.Button editButton;

    @FXML
    private javafx.scene.control.Button saveButton;

    @FXML
    private Button cancelButton;


    @FXML
    private Button saveStockButton;

    // flags
    private boolean editMode = false;
    private boolean dirty = false;
//    private boolean stockDirty = false;

    public void initialize() {
        setupListViewCellFactory();
        setupListViewListener(); // Ρυθμίζουμε τον Listener χωριστά
        setupStockTableColumns();
        loadPlaygrounds();

        nameField.textProperty().addListener((obs, oldVal, newVal) -> markDirty());
        addressField.textProperty().addListener((obs, oldVal, newVal) -> markDirty());
        phoneField.textProperty().addListener((obs, oldVal, newVal) -> markDirty());
        openingTimeField.textProperty().addListener((obs, oldVal, newVal) -> markDirty());

        if (!ListView.getItems().isEmpty()) {
            ListView.getSelectionModel().selectFirst();
        }// Καλούμε τη φόρτωση αμέσως

        //κανουμε κλειδώνουμε την αλλαγη
        setEditMode(false);

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

    @FXML
    private void handleCancelEdit(Event event) {
        PlaygroundSimple selected = ListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        fillDetailsPanel(selected.getId());

        // απενεργοποίηση edit mode
        setEditMode(false);
    }

   @FXML
   private void addPlayground() {
        try {


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/photometal1/AddPlaygroundAdmin-view.fxml"));
            Parent root = loader.load();

            // 2. Λήψη του Controller του Dialog. Αυτό είναι κρίσιμο για την επικοινωνία.
            AddPlaygroundController addController = loader.getController();

            // 3. Δημιουργία νέου Stage (Παραθύρου)
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Δημιουργία Νέου παιδότοπου");

            // (Δεν επιτρέπει αλληλεπίδραση με το κύριο παράθυρο μέχρι να κλείσει αυτό)
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            //Μετά το κλείσιμο, ξαναφόρτωσε τη λίστα
            loadPlaygrounds();

        }catch (IOException e) {
        //  χειρισμός σφάλματος
        }


   }

@FXML
private void handleEditPlaygroundAndStock(Event event) {
    if (ListView.getSelectionModel().getSelectedItem() == null) {
        return;
    }
    setEditMode(true);

}


    @FXML
    private void handleSavePlaygroundAndStock() {
        if (!dirty) return;

        PlaygroundSimple selected = ListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        int playgroundId = selected.getId();

        PlaygroundDao playgroundDao = new PlaygroundDao();
        store_house_stockDao stockDao = new store_house_stockDao();

        try {
            // 1) Save playground details
            Playground playground = new Playground();
            playground.setId(playgroundId);
            playground.setName(nameField.getText());
            playground.setAddress(addressField.getText());
            playground.setPhone(phoneField.getText());
            playground.setOpenTime(openingTimeField.getText());

            playgroundDao.updatePlayground(playground);

            // 2) Save stock quantities
            for (Store_House_Stock_For_View item : stockTable.getItems()) {
                stockDao.updateStockQuantity(
                        playgroundId,
                        item.getProductId(),
                        item.getQuantity()
                );
            }

            // 3) Κλείνουμε edit mode και “καθαρίζουμε” το dirty flag
            setEditMode(false);

            // (προαιρετικό) Alert επιτυχίας
            // new Alert(Alert.AlertType.INFORMATION, "Οι αλλαγές αποθηκεύτηκαν.").showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            // εδώ βάλε ένα Alert σφάλματος αν θες
        }
    }




    private void setEditMode(boolean enable) {
        this.editMode = enable;

        // TextFields
        nameField.setEditable(enable);
        addressField.setEditable(enable);
        phoneField.setEditable(enable);
        openingTimeField.setEditable(enable);

        // TableView quantities
        stockTable.setEditable(enable);

        cancelButton.setDisable(!enable);

        // ListView (προαιρετικά το κλειδώνεις για να μην αλλάξει playground ενώ κάνει edit)
        ListView.setDisable(enable);

        // Κουμπιά
        if (!enable) {
            // βγαίνουμε από edit mode → κλειδώνουμε save μέχρι επόμενη αλλαγή
            saveButton.setDisable(true);
            dirty = false;
        }
    }



    private void setupListViewListener() {
        // Λογική Listener (πλέον περιέχει τη φόρτωση λεπτομερειών)
        ListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                int selectedId = newVal.getId();
                fillDetailsPanel(selectedId); // Καλούμε τη μέθοδο ενημέρωσης
            }
        });
    }

    private void fillDetailsPanel(int playgroundId) {
        PlaygroundDao playgroundDao = new PlaygroundDao();
        setEditMode(false);
        // Λογική για τη φόρτωση λεπτομερειών (από τον κώδικα που είχατε στο Listener)
        try {
            Playground playground = playgroundDao.getPlayground(playgroundId);
            store_house_stockDao store_house_stockDao = new store_house_stockDao();

            if (playground != null) {
                nameField.setText(playground.getName());
                addressField.setText(playground.getAddress());
                phoneField.setText(playground.getPhone());
                openingTimeField.setText(playground.getOpenTime());

            }
            stockTable.setItems(store_house_stockDao.getStockForPlayground(playgroundId));
            //resetStockDirty();
        } catch (Exception e) {
            e.printStackTrace();
            // Εμφάνιση Alert σφάλματος
        }
    }

    public void loadPlaygrounds() {
        PlaygroundDao playgroundDao = new PlaygroundDao();
        try {
            // Φόρτωση των δεδομένων (χωρίς να ορίζουμε ξανά τον Listener)
            ListView.setItems(playgroundDao.getAllPlaygroundsSimple());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setupStockTableColumns() {
        // Προϊόν = Όνομα + Περιγραφή
        productColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getProductName()
                                + " - "
                                + cellData.getValue().getProductDescription()
                )
        );
        stockTable.setEditable(true);
        // Ποσότητα
        quantityColumn.setCellValueFactory(cellData ->
                cellData.getValue().quantityProperty().asObject()
        );

        // Κάνουμε τη στήλη editable με TextField
        quantityColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        // Όταν ο χρήστης αλλάξει την ποσότητα στο κελί
        quantityColumn.setOnEditCommit(event -> {
            Store_House_Stock_For_View row = event.getRowValue();
            int newValue = event.getNewValue() != null ? event.getNewValue().intValue() : 0;

            if (newValue < 0) {
                // Μικρή ασφάλεια: δεν επιτρέπουμε αρνητικά
                // επαναφέρουμε την παλιά τιμή
                row.setQuantity(event.getOldValue().intValue());
                stockTable.refresh();
                return;
            }

            row.setQuantity(newValue);
//            markStockDirty();
            markDirty();
        });

    }
    // Εδω εμφανιζω τον καδω στο γράφω για το fxml αν θελει να αλλαξεις κατι
    private void setupListViewCellFactory() {
        ListView.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {

            private final javafx.scene.control.Label nameLabel = new javafx.scene.control.Label();
            private final javafx.scene.control.Button deleteButton = new javafx.scene.control.Button();
            private final javafx.scene.layout.HBox container =
                    new javafx.scene.layout.HBox(10, nameLabel, deleteButton);

            {
                container.setFillHeight(true);
                container.setStyle("-fx-alignment: CENTER_LEFT;");
                deleteButton.setText("🗑"); // ή βάλε icon με graphic
                deleteButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
//                deleteButton.addEventFilter(MouseEvent.MOUSE_PRESSED, Event::consume);
//                deleteButton.addEventFilter(MouseEvent.MOUSE_CLICKED, Event::consume);

                deleteButton.setOnAction(e -> {
                    e.consume();
                    PlaygroundSimple item = getItem();
                    if (item == null) return;

                    // Επιβεβαίωση
                    javafx.scene.control.Alert alert =
                            new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Διαγραφή Πάρκου");
                    alert.setHeaderText("Να διαγραφεί το πάρκο \"" + item.getName() + "\";");
                    alert.setContentText("Θα διαγραφεί και το απόθεμά του (store house).");

                    var result = alert.showAndWait();
                    if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                        try {
                            PlaygroundDao dao = new PlaygroundDao();
                            dao.deletePlayground(item.getId());   // ΕΔΩ μέσα κάνει CASCADE ή procedure
                            loadPlaygrounds();                    // ξαναφόρτωσε λίστα
                            clearDetails();                       // καθάρισε panel λεπτομερειών
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }

            @Override
            protected void updateItem(PlaygroundSimple item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    nameLabel.setText(item.getName());
                    setText(null);
                    setGraphic(container);
                }
            }
        });
    }

    private void clearDetails() {
        nameField.clear();
        addressField.clear();
        phoneField.clear();
        openingTimeField.clear();
        stockTable.getItems().clear();
        //resetStockDirty();
    }

    private void markDirty() {
        if (!editMode) return;  // αγνοούμε αλλαγές εκτός edit mode
        dirty = true;
        saveButton.setDisable(false);
    }


}
