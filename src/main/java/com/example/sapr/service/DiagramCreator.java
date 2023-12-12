package com.example.sapr.service;

import com.example.sapr.payload.Constructor;
import javafx.scene.Group;

public interface DiagramCreator {
    // TODO refactor this shit
    Group create(Constructor constructor, double samplingStep, int stepPrecision, double[] barLengths);
}
