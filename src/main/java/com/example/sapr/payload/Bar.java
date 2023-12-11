package com.example.sapr.payload;

public class Bar {
    private Double length;
    private Double area;
    private Double qxLoad;
    private Double elasticMod;
    private Double permisVolt;

    public Bar(double length, double area, double qxLoad, double elasticMod, double permisVolt) {
        this.length = length;
        this.area = area;
        this.qxLoad = qxLoad;
        this.elasticMod = elasticMod;
        this.permisVolt = permisVolt;
    }
    public Bar() {
        this.area = 0.0;
        this.length = 0.0;
        this.permisVolt = 0.0;
        this.elasticMod = 0.0;
        this.qxLoad = 0.0;
    }

    public void setLenght(Double lenght) {
        this.length = lenght;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public void setQxLoad(Double qxLoad) {
        this.qxLoad = qxLoad;
    }

    public void setElasticMod(Double elasticMod) {
        this.elasticMod = elasticMod;
    }

    public void setPermisVolt(Double permisVolt) {
        this.permisVolt = permisVolt;
    }

    public Double getLenght() {
        return length;
    }

    public Double getArea() {
        return area;
    }

    public Double getQxLoad() {
        return qxLoad;
    }

    public Double getElasticMod() {
        return elasticMod;
    }

    public Double getPermisVolt() {
        return permisVolt;
    }
}