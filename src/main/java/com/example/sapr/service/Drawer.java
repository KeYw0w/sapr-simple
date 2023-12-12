package com.example.sapr.service;

import com.example.sapr.payload.Bar;
import com.example.sapr.payload.Constructor;
import com.example.sapr.payload.Node;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;

public class Drawer {
    private Double DEFAULT_DRAW_HEIGHT = 500.0;
    private Double DEFAULT_DRAW_WIDTH = 700.0;
    private Double MIN_BAR_LENGTH = 40.0;
    private Double MIN_BAR_AREA = 40.0;
    private Double SUPPORT_HEIGHT = 40.0;
    private Double SUPPORT_WIDTH = 20.0;
    private Color SUPPORT_COLOR = Color.BROWN;
    private Double NODE_LOAD_LENGTH = 30.0;
    private Color NODE_LOAD_COLOR = Color.GREEN;
    private Color BAR_LOAD_COLOR = Color.RED;

    public Stage draw(Constructor construction) {
        return draw(construction, DEFAULT_DRAW_WIDTH, DEFAULT_DRAW_HEIGHT);
    }

    public Stage draw(Constructor construction, Double width, Double height) {
        double widthRes = width - ((construction.getLeftSupport() ? 20 : 0) - ((construction.getRightSupport()) ? 20 : 0));
        double oneBarLength = (widthRes / construction.getBars().size());
        Group group = new Group();
        var x = 15.0;
        if (construction.getLeftSupport()) {
            group.getChildren().add(createSupport(x, height));
            x += SUPPORT_WIDTH;
        }
        for (int i = 0; i < construction.getBars().size(); i++) {
            var bar = createBar(x, construction.getBars().get(i), oneBarLength, height);
            group.getChildren().add(bar);
            var nodeLoad = createNodeLoad(x, construction.getNodes().get(i), height);
            if (nodeLoad != null) {
                group.getChildren().add(nodeLoad);
            }
            var barLoad = createBarLoad(x, construction.getBars().get(i), height, bar.getWidth());
            if (barLoad != null) {
                group.getChildren().add(barLoad);
            }
            x += bar.getWidth();
        }
        var nodeLoad = createNodeLoad(x, construction.getNodes().get(construction.getNodes().size() - 1), height);
        if (nodeLoad != null) {
            group.getChildren().add(nodeLoad);
        }
        if (construction.getRightSupport()) {
            group.getChildren().add(createSupport(x, height));
        }
        var stage = new Stage();
        stage.setScene(new Scene(group, width + (NODE_LOAD_LENGTH * 2), height));
        return stage;
    }

    private Path createNodeLoad(Double x, Node node, Double height) {
        var loadVector = createXVector(x, node.getFxLoad() > 0, NODE_LOAD_LENGTH, height);
        loadVector.setStroke(NODE_LOAD_COLOR);
        loadVector.setViewOrder(-1.0);
        return ((node.getFxLoad() != 0.0) ? loadVector : null);
    }

    private Path createBarLoad(Double x, Bar bar, Double height, Double width) {
        var loadVector = createXVector((bar.getQxLoad() > 0) ? x : x + width, bar.getQxLoad() > 0, width, height + 5);
        loadVector.setStroke(BAR_LOAD_COLOR);
        return (bar.getQxLoad() != 0.0) ? loadVector : null;
    }

    private Path createXVector(Double x, Boolean positive, Double length, Double height) {
        var sign = (positive) ? 1 : -1;
        var moveTo = new MoveTo(x, height / 2);
        var hLineTo = new HLineTo(x + length * sign);
        var newWidth = x + length * sign;
        var lineTo = new LineTo(newWidth + 3 * -sign, height / 2 - 3);
        var moveTo1 = new MoveTo(newWidth, height / 2);
        var lineTo1 = new LineTo(newWidth + 3 * -sign, height / 2 + 3);
        var path = new Path(moveTo, hLineTo, lineTo, moveTo1, lineTo1);
        path.setStrokeWidth(2.0);
        return path;
    }

    private Rectangle createSupport(Double x, Double height) {
        var rectangle = new Rectangle(x, height / 2 - SUPPORT_HEIGHT / 2, SUPPORT_WIDTH, SUPPORT_HEIGHT);
        rectangle.setFill(Color.rgb(53,89,119,0.4));
        return rectangle;
    }

    private Rectangle createBar(Double x, Bar bar, Double width, Double height) {
        var barLength = bar.getLength() * 50;
        var barArea = bar.getArea() * 40;
        if (barLength >= width) {
            barLength = width;
        }
        if (barLength < MIN_BAR_LENGTH && width > MIN_BAR_LENGTH) {
            barLength = MIN_BAR_LENGTH;
        }
        if (barArea >= height) {
            barArea = height / 3;
        }
        if (barArea < MIN_BAR_AREA) {
            barArea = MIN_BAR_AREA;
        }
        Rectangle Bar= new Rectangle(x, height / 2 - barArea / 2, barLength, barArea);
        Bar.setArcHeight(20.0d);
        Bar.setArcWidth(20.0d);
        Bar.setFill(Color.TRANSPARENT);
        Bar.setStroke(Color.BLACK);
        return Bar;
    }
}
