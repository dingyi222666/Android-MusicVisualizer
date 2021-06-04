package com.dingyi.visualizer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.dingyi.visualizer.animation.MyDataAnimator;
import com.dingyi.visualizer.bean.VisualizerDataBean;
import com.dingyi.visualizer.drawable.BaseDrawable;
import com.dingyi.visualizer.drawable.CircleDrawable;
import com.dingyi.visualizer.helper.VisualizerHelper;

public class VisualizerOpenGLView extends BaseOpenGLView {
    public VisualizerOpenGLView(Context context) {
        super(context);
    }

    public VisualizerOpenGLView(Context context, AttributeSet set) {
        super(context, set);
    }

    public VisualizerOpenGLView(Context context, AttributeSet set, int theme) {
        super(context, set, theme);
    }


    private VisualizerHelper helper;

    private BaseDrawable drawable;

    private MyDataAnimator animator;

    private VisualizerDataBean bean;

    private boolean isStarted = false;


    private Paint paint;


    public void setDrawable(BaseDrawable drawable) {
        this.drawable = drawable;
    }


    @Override
    public void baseInit() {
        //if (bean != null) {
        drawable = new CircleDrawable(getContext());
        animator = new MyDataAnimator();
        bean = new VisualizerDataBean();
        helper = new VisualizerHelper(0,bean);
        paint=new Paint();

        helper.setListener(new VisualizerHelper.Listener() {
            @Override
            public void onFftDataCapture(double[] fft) {
                animator.addData(fft);
            }
        });


        animator.setAnimationListener(new MyDataAnimator.AnimationListener() {
            @Override
            public void onNewData(double[] data) {
                drawable.setData(data);
            }

        });

        //}

        paint.setColor(0xff000000);
        paint.setStrokeWidth(8);
        paint.setTextSize(36);
    }

    public VisualizerDataBean getData() {
        return bean;
    }

    @Override
    protected void onMyDraw(Canvas canvas) {
        if (isStarted&&drawable.getData()!=null&&drawable.getData().length>0) {
            drawable.onDraw(canvas);
            if (bean.isShowFps()){
                //这里拼接会造成大量StringBuilder创建，我这懒得优化
                canvas.drawText(fps+" fps",100,200,paint);
            }
            System.gc();
        }
        calcFps();
    }

    public void start() {

        if (drawable.getDataBean() == null) {
            animator.setDataBean(bean);
            drawable.setDataBean(bean);
        }

        drawable.init();
        helper.start();
        animator.start();
        isStarted = true;
    }


    public void stop() {
        helper.stop();
        isStarted = false;
        animator.stop();
    }


    @Override
    public void release() {
        super.release();
        helper.release();

    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        if (isStarted&&drawable.getData()!=null&&drawable.getData().length>0) {
//            drawable.onDraw(canvas);
//            canvas.drawText(fps+" fps",200,300,paint);
//            System.gc();
//        }
//        calcFps();
//        invalidate();
//
//    }



}
