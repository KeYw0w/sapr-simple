package com.example.sapr.service.impl;

import com.example.sapr.payload.Constructor;
import com.example.sapr.service.DiagramCreator;
import com.example.sapr.service.Processor;
import com.example.sapr.service.Result;
import javafx.scene.Group;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.util.List;

// TODO refactor this
public class DiagramCreatorImpl implements DiagramCreator {
    private final Processor processor = Processor.INSTANCE;

    @Override
    public Group create(Constructor constructor, double samplingStep, int stepPrecision, double[] barLengths) {
        AreaChart<Number, Number> nxArea = new AreaChart<>(new NumberAxis(), new NumberAxis());
        AreaChart<Number, Number> OxArea = new AreaChart<>(new NumberAxis(), new NumberAxis());
        AreaChart<Number, Number> uxArea = new AreaChart<>(new NumberAxis(), new NumberAxis());
        double leftBorder = 0.0;
        for (int i = 0; i < barLengths.length; i++) {
            XYChart.Series<Number, Number> nxSeries = new XYChart.Series<>();
            XYChart.Series<Number, Number> OxSeries = new XYChart.Series<>();
            XYChart.Series<Number, Number> uxSeries = new XYChart.Series<>();
            List<Result> resultList = processor.calculate(constructor, i, samplingStep, stepPrecision);
            for (Result result : resultList) {
                nxSeries.getData().add(new XYChart.Data<>(leftBorder + result.getX(), result.getNX()));
                OxSeries.getData().add(new XYChart.Data<>(leftBorder + result.getX(), result.getUX()));
                uxSeries.getData().add(new XYChart.Data<>(leftBorder + result.getX(), result.getSigma()));
            }
            leftBorder += barLengths[i];
            nxSeries.setName(String.valueOf(i + 1));
            OxSeries.setName(String.valueOf(i + 1));
            uxSeries.setName(String.valueOf(i + 1));
            nxArea.getData().add(nxSeries);
            OxArea.getData().add(OxSeries);
            uxArea.getData().add(uxSeries);
        }
        Tab nxTab = new Tab("Nx", nxArea);
        nxTab.setClosable(false);
        Tab uxTab = new Tab("Ux", OxArea);
        uxTab.setClosable(false);
        Tab oxTab = new Tab("∂x", uxArea);
        oxTab.setClosable(false);
        TabPane tabPane = new TabPane(nxTab, uxTab, oxTab);
        return new Group(tabPane);
    }
}
