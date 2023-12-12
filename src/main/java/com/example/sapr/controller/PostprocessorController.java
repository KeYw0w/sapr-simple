package com.example.sapr.controller;

import com.example.sapr.service.*;
import com.example.sapr.service.impl.GraphCreatorImpl;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringJoiner;

import static com.example.sapr.service.MainService.showErrorDialog;

public class PostprocessorController implements Initializable {
    @FXML
    private TableView<Results> resultsView;
    @FXML
    private TableColumn<Results, Double> xValue, Nx, Ux, sigmaX;
    @FXML
    private TextField samplingStep, x, barIndexes;
    private final FileChooser fileChooser = new FileChooser();
    private final Processor processor = Processor.INSTANCE;
    private final Storage storage = Storage.INSTANCE;
    private final GraphCreator graphCreator = new GraphCreatorImpl();

    private double tryParseDouble(String number) {
        return Double.parseDouble(number);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initColumns();
    }

    private void initColumns() {
        setCellValuesFactory();
    }

    private void setCellValuesFactory() {
        xValue.setCellValueFactory(new PropertyValueFactory<>("x"));
        Nx.setCellValueFactory(new PropertyValueFactory<>("NX"));
        Ux.setCellValueFactory(new PropertyValueFactory<>("UX"));
        sigmaX.setCellValueFactory(new PropertyValueFactory<>("sigma"));
    }

    public void calculateForX() {
        try {
            double X = tryParseDouble(x.getText());
            Results results = processor.calculate(storage.getConstructor(), X);
            resultsView.getItems().clear();
            resultsView.getItems().add(results);
        } catch (Exception e) {
            showErrorDialog("Ошибка");
        }
    }

    public void save(List<Results> Results, Window window) {
        File chosenFile = fileChooser.showSaveDialog(window);
        if (chosenFile == null) {
            return;
        }
        try (BufferedWriter writer = Files.newBufferedWriter(chosenFile.toPath())) {
            writer.write(prepareResultsForSaving(Results));
        } catch (IOException ex) {
            showErrorDialog("Не удалось сохранить файл");
        }

    }

    private String prepareResultsForSaving(List<Results> results) {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("x;Nx;Ux;∂x");
        for (Results result : results) {
            String resultLine = result.getX() + ";" + result.getNX() + ";" + result.getUX() + ";" + result.getSigma();
            joiner.add(resultLine);
        }
        return joiner.toString();
    }

    public void calculate() {
        try {
            double barindex = tryParseDouble(barIndexes.getText());
            double step = tryParseDouble(samplingStep.getText());
            int stepPrecision = getNumberPrecision(samplingStep.getText());
            List<Results> resultsList = processor.calculate(storage.getConstructor(), (int) barindex - 1, step, stepPrecision);
            resultsView.getItems().clear();
            resultsView.getItems().addAll(resultsList);
        } catch (Exception e) {
            showErrorDialog("Ошибка");
        }
    }

    public void save(MouseEvent event) {
        List<Results> calculatorResults = resultsView.getItems();
        save(calculatorResults, ((Button) event.getSource()).getScene().getWindow());
    }

    public void drawGraph() {
        try {
            int barIndex = (int) tryParseDouble(barIndexes.getText());
            double step = tryParseDouble(samplingStep.getText());
            int stepPrecision = getNumberPrecision(samplingStep.getText());
            double barLength = storage.getConstructor().getBars().get(barIndex - 1).getLength();
            Group group = graphCreator.create(storage.getConstructor(), barIndex - 1, step, barLength, stepPrecision);
            Scene scene = new Scene(group);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Graph");
            stage.show();
        } catch (Exception e) {
            showErrorDialog("Ошибка");
        }
    }

    private int getNumberPrecision(String number) {
        String[] dotSplit = number.split("\\.");
        if (dotSplit.length == 1) {
            return 0;
        }
        return dotSplit[1].length();
    }
}
