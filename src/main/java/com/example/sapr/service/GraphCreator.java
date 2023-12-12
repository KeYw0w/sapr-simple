package com.example.sapr.service;

import com.example.sapr.payload.Constructor;
import javafx.scene.Group;

public interface GraphCreator {
    Group create(Constructor constructor, int barIndex, double samplingStep, double barLength, int stepPrecision);
}
