module com.example.photometal1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.photometal1 to javafx.fxml;
    exports com.example.photometal1;
    exports com.example.photometal1.Controllers;
    opens com.example.photometal1.Controllers to javafx.fxml;
    opens com.example.photometal1.Models to javafx.base;
}