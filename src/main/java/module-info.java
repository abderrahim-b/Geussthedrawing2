module com.example.guessthedrawing2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.guessthedrawing2 to javafx.fxml;
    exports com.example.guessthedrawing2;
}