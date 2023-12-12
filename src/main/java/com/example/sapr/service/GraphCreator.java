package com.example.sapr.service;

import javafx.scene.Group;

public interface GraphCreator {
    Group create(Results results, int barIndex, int precision, double samplingStep, double barLength, int stepPrecision);
}
