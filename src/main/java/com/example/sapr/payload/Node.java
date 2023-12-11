package com.example.sapr.payload;

public class Node {
    public Node(Double fxLoad) {
        this.fxLoad = fxLoad;
    }
    public Node() {
        this.fxLoad = 0.0;
    }

    public void setFxLoad(Double fxLoad) {
        this.fxLoad = fxLoad;
    }

    private Double fxLoad;

    public Double getFxLoad() {
        return fxLoad;
    }
}
