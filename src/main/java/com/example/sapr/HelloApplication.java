package com.example.sapr;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    private double APPLICATION_WIDTH = 900.0;
    private double APPLICATION_HEIGHT = 450.0;
//    private String APPLICATION_TITLE = "sapr-bar";

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setResizable(false);
        stage.setTitle("SAPR");
        stage.setScene(scene);
        stage.show();
    }
}

