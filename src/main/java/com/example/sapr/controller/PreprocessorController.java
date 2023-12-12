package com.example.sapr.controller;

import com.example.sapr.payload.Bar;
import com.example.sapr.payload.Constructor;
import com.example.sapr.payload.Node;
import com.example.sapr.service.MainService;
import com.example.sapr.service.Storage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.converter.DoubleStringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PreprocessorController implements Initializable {

    @FXML
    private TableView<Bar> barView;

    @FXML
    private TableView<Node> nodeView;

    @FXML
    private TableColumn<Bar, Double> length;

    @FXML
    private TableColumn<Bar, Double> area;

    @FXML
    private TableColumn<Bar, Double> qxLoad;

    @FXML
    private TableColumn<Bar, Double> elasticMod;

    @FXML
    private TableColumn<Bar, Double> permisVolt;

    @FXML
    private TableColumn<Node, Double> fxLoad;

    @FXML
    private Button addBar;

    @FXML
    private Button delBar;

    @FXML
    private Button addNodeLoad;

    @FXML
    private Button delNodeLoad;
    Storage storage=Storage.INSTANCE;

    @FXML
    private CheckBox leftSupport;

    @FXML
    private CheckBox rightSupport;

    private final MainService service = new MainService();

    @FXML
    private void draw() {
        service.draw(getConstruction());
    }

    @FXML
    private void save() {
        service.save(getConstruction());
    }

    @FXML
    private void upload(MouseEvent event) {
        Window window = ((Button) event.getSource()).getScene().getWindow();
        Constructor uploadedConstruction = service.upload(window);
        if (uploadedConstruction != null) {
            construction = uploadedConstruction;
        }
        setConstruction(uploadedConstruction);
    }

    @FXML
    private void process() {
        service.process(getConstruction());
    }




    private Constructor construction;

    public Constructor getConstruction() {
        return new Constructor(barView.getItems(), nodeView.getItems(), leftSupport.isSelected(), rightSupport.isSelected());
    }

    public void setConstruction(Constructor v) {
        barView.getItems().clear();
        barView.getItems().addAll(v.getBars());
        nodeView.getItems().clear();
        nodeView.getItems().addAll(v.getNodes());
        leftSupport.setSelected(v.getLeftSupport());
        rightSupport.setSelected(v.getRightSupport());
    }

    private void initButtons() {
        addBar.setOnMouseClicked(event -> barView.getItems().add(new Bar(0.0, 0.0, 0.0, 0.0, 0.0)));
        addNodeLoad.setOnMouseClicked(event -> nodeView.getItems().add(new Node(0.0)));
        delNodeLoad.setOnMouseClicked(event -> {
            int row = nodeView.getFocusModel().getFocusedIndex();
            if (row >= 0) {
                nodeView.getItems().remove(row);
            }
        });
        delBar.setOnMouseClicked(event -> {
            int row = barView.getFocusModel().getFocusedIndex();
            if (row >= 0) {
                barView.getItems().remove(row);
            }
        });
    }

    private void initColumns() {
        setCellValuesFactory();
        setEditable();
    }

    private void setEditable() {
        area.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        area.setOnEditCommit(event -> {
            int row = event.getTablePosition().getRow();
            Bar bar = event.getTableView().getItems().get(row);
            bar.setArea(event.getNewValue());
        });

        length.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        length.setOnEditCommit(event -> {
            int row = event.getTablePosition().getRow();
            Bar bar = event.getTableView().getItems().get(row);
            bar.setLength(event.getNewValue());
        });

        qxLoad.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        qxLoad.setOnEditCommit(event -> {
            int row = event.getTablePosition().getRow();
            Bar bar = event.getTableView().getItems().get(row);
            bar.setQxLoad(event.getNewValue());
        });

        elasticMod.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        elasticMod.setOnEditCommit(event -> {
            int row = event.getTablePosition().getRow();
            Bar bar = event.getTableView().getItems().get(row);
            bar.setElasticMod(event.getNewValue());
        });

        permisVolt.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        permisVolt.setOnEditCommit(event -> {
            int row = event.getTablePosition().getRow();
            Bar bar = event.getTableView().getItems().get(row);
            bar.setPermisVolt(event.getNewValue());
        });

        fxLoad.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        fxLoad.setOnEditCommit(event -> {
            int row = event.getTablePosition().getRow();
            Node node = event.getTableView().getItems().get(row);
            node.setFxLoad(event.getNewValue());
        });
    }

    private void setCellValuesFactory() {
        area.setCellValueFactory(new PropertyValueFactory<>("area"));
        length.setCellValueFactory(new PropertyValueFactory<>("length"));
        qxLoad.setCellValueFactory(new PropertyValueFactory<>("qxLoad"));
        elasticMod.setCellValueFactory(new PropertyValueFactory<>("elasticMod"));
        permisVolt.setCellValueFactory(new PropertyValueFactory<>("permisVolt"));
        fxLoad.setCellValueFactory(new PropertyValueFactory<>("fxLoad"));
    }

    public void postprocess(MouseEvent event) throws IOException {
        storage.setConstructor(getConstruction());
        FXMLLoader loader = new FXMLLoader(PostprocessorController.class.getResource("../postprocessor-view.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initButtons();
        initColumns();
    }
}


