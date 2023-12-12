package com.example.sapr.service;

import com.example.sapr.payload.Bar;
import com.example.sapr.payload.Constructor;

public class Validator {
    public Boolean validate(Constructor construction) {
        if (!(construction.getLeftSupport() || construction.getRightSupport())) return false;
        if (construction.getNodes().size() - construction.getBars().size() != 1) return false;
        return construction.getBars().stream().allMatch(this::isBarValid);

    }

    private Boolean isBarValid(Bar bar) {
        if (bar.getArea() <= 0) return false;
        if (bar.getLength() <= 0) return false;
        if (bar.getElasticMod() <= 0) return false;
        return bar.getPermisVolt() > 0;
    }
}


