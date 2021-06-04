package com.dingyi.visualizer.bean;

public class VisualizerDataBean {

    private int maxFps;
    private float circleLineCount;
    private float circleAngle;
    private int skipArrayIndex=3;
    private float circleRadius;
    private SmoothMode smoothMode=SmoothMode.Linear3;
    private boolean showCirclePoint=true;
    private boolean showFps=false;


    private int skipStartArrayIndex=8;

    private float lineMargin=24;

    private float lineWidth;

    public enum SmoothMode {
        /**
         * 三次平滑
         */
        Linear3,
        /**
         * 五次平滑
         */
        Linear5,
        Linear7,
        NONE,
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    public float getLineMargin() {
        return lineMargin;
    }

    public void setLineMargin(float lineMargin) {
        this.lineMargin = lineMargin;
    }

    public int getSkipStartArrayIndex() {
        return skipStartArrayIndex;
    }

    public void setSkipStartArrayIndex(int skipStartArrayIndex) {
        this.skipStartArrayIndex = skipStartArrayIndex;
    }


    public SmoothMode getSmoothMode() {
        return smoothMode;
    }

    public void setSmoothMode(SmoothMode smoothMode) {
        this.smoothMode = smoothMode;
    }

    public boolean isShowCirclePoint() {
        return showCirclePoint;
    }

    public void setShowCirclePoint(boolean showCirclePoint) {
        this.showCirclePoint = showCirclePoint;
    }


    public boolean isShowFps() {
        return showFps;
    }

    public void setShowFps(boolean showFps) {
        this.showFps = showFps;
    }

    private final VisualizerColorBean colorData=new VisualizerColorBean();

    
    public VisualizerColorBean getColorData() {
        return colorData;
    }
    
    
    public void setCircleRadius(float circleRaduis) {
        this.circleRadius = circleRaduis;
    }

    public float getCircleRadius() {
        return circleRadius;
    }
    public void setSkipArrayIndex(int skipArrayIndex) {
        this.skipArrayIndex = skipArrayIndex;
    }

    public int getSkipArrayIndex() {
        return skipArrayIndex;
    }


    public void setMaxFps(int maxFps) {
        this.maxFps = maxFps;
    }

    public int getMaxFps() {
        return maxFps;
    }

    public void setCircleLineCount(float circleLineCount) {
        this.circleLineCount = circleLineCount;
    }

    public float getCircleLineCount() {
        return circleLineCount;
    }

    public void setCircleAngle(float circleAngle) {
        this.circleAngle = circleAngle;
    }

    public float getCircleAngle() {
        return circleAngle;
    }
 
}
