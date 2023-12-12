package com.example.sapr.service;

import java.security.PrivilegedAction;

public class UXCalculate {
    private String UX_FORMATTER = "U%dx: (%f * x^2) + (%f * x) + (%f)";
    private double firstArg;
    private double secondArg;
    private double thirdArg;
    public Double calculate(double x){
        return ((firstArg*x*x)+(secondArg*x)+thirdArg);
    }

    public UXCalculate(double firstArg, double secondArg) {
        this.firstArg = firstArg;
        this.secondArg = secondArg;
    }
}
