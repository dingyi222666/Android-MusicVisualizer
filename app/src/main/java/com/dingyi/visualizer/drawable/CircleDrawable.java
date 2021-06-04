package com.dingyi.visualizer.drawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.dingyi.visualizer.util.LogUtil;
import com.dingyi.visualizer.util.MathUtil;

import java.util.Arrays;
import java.util.Collections;

public class CircleDrawable extends BaseDrawable {


    private float[] linesArray;

    private float[] pointsArray;

    private float cx;

    private float cy;

    private float baseCr;

    private float cr;

    private final Paint circlePaint = new Paint();

    private final Paint arcPaint = new Paint();

    private final PointF circlePointF = new PointF();

    private final PointF newCirclePointF = new PointF();

    private final PointF twoCirclePointF = new PointF();


    public CircleDrawable(Context context) {
        super(context);
    }


    @Override
    public void init() {

        //circle paint
        circlePaint.setColor(getDataBean().getColorData().getCirclePaintColor());
        circlePaint.setStrokeWidth(getDataBean().getColorData().getCirclePaintWidth());
        circlePaint.setStrokeCap(Paint.Cap.ROUND);
        circlePaint.setStyle(Paint.Style.FILL);

        //arc paint
        arcPaint.setColor(getDataBean().getColorData().getArcPaintColor());
        arcPaint.setColor(getDataBean().getColorData().getCirclePaintColor());
        arcPaint.setStrokeCap(Paint.Cap.ROUND);
        arcPaint.setStyle(Paint.Style.STROKE);
    }



    @Override
    public void onDraw(Canvas canvas) {
        if (cy == 0) {
            readyData(canvas);
        }

        System.gc();

       for (int i = 0; i < getDataBean().getCircleLineCount(); i = i + 1) {

            MathUtil.getCirclePoint(
                    cx, cy, cr + dp2px(3), i * getDataBean().getCircleAngle()+270, circlePointF);


            float des = (float) getData()[getDataBean().getSkipStartArrayIndex()+i];


            MathUtil.getPoint(
                    cx, cy,
                    MathUtil.getPointDistance(cx, cy, circlePointF.x, circlePointF.y) + des,
                    MathUtil.getPointAngle(cx, cy, circlePointF.x, circlePointF.y), newCirclePointF);

            linesArray[i * 4] = circlePointF.x;
            linesArray[i * 4 + 1] = circlePointF.y;
            linesArray[i * 4 + 2] = newCirclePointF.x;
            linesArray[i * 4 + 3] = newCirclePointF.y;


            MathUtil.getPoint(
                    cx, cy,
                    MathUtil.getPointDistance(cx, cy, circlePointF.x, circlePointF.y) - des - dp2px(4),
                    MathUtil.getPointAngle(cx, cy, circlePointF.x, circlePointF.y), twoCirclePointF);

            pointsArray[i * 2] = twoCirclePointF.x;
            pointsArray[i * 2 + 1] = twoCirclePointF.y;


        }


        canvas.drawArc(cx - cr, cy - cr, cx + cr, cy + cr, 0, 360, false, arcPaint);
        canvas.drawLines(linesArray, circlePaint);
        if (getDataBean().isShowCirclePoint()){
            canvas.drawPoints(pointsArray, circlePaint);
        }


    }

    private void readyData(Canvas canvas) {
        linesArray = new float[(int) getDataBean().getCircleLineCount() * 4];
        pointsArray = new float[(int) getDataBean().getCircleLineCount() * 2];
        cx = canvas.getWidth() / 2;
        cy = canvas.getHeight() / 2;
        baseCr = getDataBean().getCircleRadius();
        cr = baseCr;
    }


}
