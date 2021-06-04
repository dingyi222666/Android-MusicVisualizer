package com.dingyi.visualizer.drawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

public class LineDrawable extends BaseDrawable {

    private final Paint linePaint=new Paint();

    private float width;

    private float height;

    private float lineCount;

    public LineDrawable(Context context) {
        super(context);
    }

    @Override
    public void init() {
        linePaint.setStrokeWidth(2);
        linePaint.setColor(getDataBean().getColorData().getLinePaintColor());
    }


    private void readyData(Canvas canvas) {
       width=canvas.getWidth();
       height=canvas.getHeight();
       lineCount= width/ getDataBean().getLineWidth();
    }

    @Override
    public void onDraw(Canvas canvas) {

        if (lineCount == 0) {
            readyData(canvas);
        }

        for (int i=1;i<lineCount;i++) {
            float des = (float) getData()[8 + i];
            canvas.drawRect((i-1)*getDataBean().getLineWidth(),height-((height/3)/120*des),
                    i*getDataBean().getLineWidth()-getDataBean().getLineMargin(),height,linePaint);
        }

        System.gc();

    }
}
