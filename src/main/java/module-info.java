module com.example.sapr {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires commons.math3;


    opens com.example.sapr to javafx.fxml;
    exports com.example.sapr;
    exports com.example.sapr.controller;
}