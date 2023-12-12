package com.example.sapr.controller;

import com.example.sapr.service.MainService;
import com.example.sapr.service.Processor;
import com.example.sapr.service.Results;
import com.example.sapr.service.Storage;
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
    private ComboBox<Integer> precisions;
    @FXML
    private TextField samplingStep, x, barIndexes;
    private final FileChooser fileChooser = new FileChooser();
    Processor processor = Processor.INSTANCE;
    Storage storage = Storage.INSTANCE;

    private double tryParseDouble(String number) {
        try {
            return Double.parseDouble(number);
        } catch (Exception e) {
            showErrorDialog("Incorrect value at TextBox");
        }
        return 0;
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
        double X = tryParseDouble(x.getText());
        Results results = processor.calculate(storage.getConstructor(), X);
        resultsView.getItems().clear();
        resultsView.getItems().add(results);
    }


    public void drawDiagram() {
    }

    public void drawGraph() {
    }

    public void save(List<Results> Results, Window window) {
        File chosenFile = fileChooser.showSaveDialog(window);
        if (chosenFile == null) {
            return;
        }
        try (BufferedWriter writer = Files.newBufferedWriter(chosenFile.toPath())) {
            writer.write(prepareResultsForSaving(Results));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
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
        double barindex = tryParseDouble(barIndexes.getText());
        double step = tryParseDouble(samplingStep.getText());
        List<Results> resultsList = processor.calculate(storage.getConstructor(), (int) barindex - 1, step);
        resultsView.getItems().clear();
        resultsView.getItems().addAll(resultsList);
    }
    public void save(MouseEvent event) {
        List<Results> calculatorResults = resultsView.getItems();
        save(calculatorResults, ((Button) event.getSource()).getScene().getWindow());
    }

    public void drawGraph(String shiftStep, int barIndex, int precision) {
        try {
            double parsedStep = tryParseDouble(shiftStep);
            if (parsedStep == 0.0)  MainService.showErrorDialog("Sampling step value can't be null");
            int stepPrecision = getNumberPrecision(shiftStep);
            double barLength = storage.getConstructor().getBars().get(barIndex - 1).getLength();
            Group group = graphCreator.create(calculator, barIndex - 1, precision, parsedStep, barLength, stepPrecision);
            Scene scene = new Scene(group);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Graph");
            stage.show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,
                    "Internal Application Error. Try again or contact to me.", ButtonType.OK).show();
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
