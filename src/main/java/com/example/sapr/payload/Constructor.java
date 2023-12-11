package com.example.sapr.payload;

import java.util.Collections;
import java.util.List;

public class Constructor {
    private List<Bar> bars;
    private List<com.example.sapr.payload.Node> nodes;
    private Boolean leftSupport;
    private Boolean rightSupport;
    public Constructor(List<Bar> bars, List<Node> nodes, boolean leftSupport, boolean rightSupport) {
        this.bars = bars;
        this.nodes = nodes;
        this.leftSupport = leftSupport;
        this.rightSupport = rightSupport;
    }

    public Constructor() {
        this.bars= Collections.emptyList();
        this.nodes=Collections.emptyList();
        this.rightSupport=false;
        this.leftSupport=false;
    }

    public void setBars(List<Bar> bars) {
        this.bars = bars;
    }

    public void setNodes(List<com.example.sapr.payload.Node> nodes) {
        this.nodes = nodes;
    }

    public void setLeftSupport(Boolean leftSupport) {
        this.leftSupport = leftSupport;
    }

    public void setRightSupport(Boolean rightSupport) {
        this.rightSupport = rightSupport;
    }

    public List<Bar> getBars() {
        return bars;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public Boolean getLeftSupport() {
        return leftSupport;
    }

    public Boolean getRightSupport() {
        return rightSupport;
    }
}
