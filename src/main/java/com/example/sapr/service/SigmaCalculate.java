package com.example.sapr.service;

public class SigmaCalculate {
    private final String SIGMA_FORMATTER="σ%dx: (%f * x) + (%f)";
    private double firstArg;
    private double secondArg;

    public Double calculate(double x){
        return (firstArg*x+secondArg);
    }

    public SigmaCalculate(double firstArg, double secondArg) {
        this.firstArg = firstArg;
        this.secondArg = secondArg;
    }
}

