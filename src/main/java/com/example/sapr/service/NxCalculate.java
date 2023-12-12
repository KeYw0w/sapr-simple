package com.example.sapr.service;

public class NxCalculate {
    private static final String FORMATTER= "N%dx: (%f * x) + (%f)";
    private double firstArg;
    private double secondArg;
    public Double calculate(double x){
        return (firstArg*x+secondArg);
    }

    public NxCalculate(double firstArg, double secondArg) {
        this.firstArg = firstArg;
        this.secondArg = secondArg;
    }
}
