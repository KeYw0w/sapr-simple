package com.example.sapr.service;

import com.example.sapr.payload.Constructor;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.*;

public class MainService {
    private final Validator validator = new Validator();
    private final Drawer drawer = new Drawer();
    private final FileChooser fileChooser = new FileChooser();
    private final ObjectMapper om = new ObjectMapper();
    private final Processor processor = new Processor();

    public void draw(Constructor construction) {
        if (!validator.validate(construction)) {
            showErrorDialog("Параметры конструкции заданы неверно");
        }
        drawer.draw(construction).show();
    }

    public void save(Constructor construction) {
        var chosenDir = fileChooser.showSaveDialog(null);
        if (chosenDir != null) {
            if (chosenDir != null) {
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(chosenDir));
                    writer.write(om.writeValueAsString(construction));
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Constructor upload(Window window) {
        File chosenFile = fileChooser.showOpenDialog(window);
        if (chosenFile != null) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(chosenFile));
                String jsonConstr = reader.readLine();
                return om.readValue(jsonConstr, Constructor.class);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    public void process(Constructor construction) {
        if (!validator.validate(construction)) {
            showErrorDialog("Параметры конструкции заданы неверно");

        }
        processor.process(construction);
    }

    public void postprocess(Constructor construction) {
//        TODO("Not yet implemented")
    }

    private void showErrorDialog(String message) {
        Scene scene = new Scene(new Label(message), 300.0, 100.0);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
