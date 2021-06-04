package com.dingyi.visualizer.bean;

public class VisualizerColorBean {
    
    private float arcPaintWidth=4f;
    private int arcPaintColor=0xFFEC407A;
    private float circlePaintWidth=13f;
    private int circlePaintColor=0xFFEC407A;

    public int getLinePaintColor() {
        return linePaintColor;
    }

    public void setLinePaintColor(int linePaintColor) {
        this.linePaintColor = linePaintColor;
    }

    private int linePaintColor=0xFFEC407A;

    public void setArcPaintWidth(float arcPaintWidth) {
        this.arcPaintWidth = arcPaintWidth;
    }

    public float getArcPaintWidth() {
        return arcPaintWidth;
    }

    public void setArcPaintColor(int arcPaintColor) {
        this.arcPaintColor = arcPaintColor;
    }

    public int getArcPaintColor() {
        return arcPaintColor;
    }

    public void setCirclePaintWidth(float circlePaintWidth) {
        this.circlePaintWidth = circlePaintWidth;
    }

    public float getCirclePaintWidth() {
        return circlePaintWidth;
    }

    public void setCirclePaintColor(int circlePaintColor) {
        this.circlePaintColor = circlePaintColor;
    }

    public int getCirclePaintColor() {
        return circlePaintColor;
    }
 }
 
