package com.dingyi.visualizer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.androidCanvas.IAndroidCanvasHelper;
import com.chillingvan.canvasgl.glview.texture.GLContinuousTextureView;

public abstract class BaseOpenGLView extends GLContinuousTextureView {


    private long lastTime=System.currentTimeMillis();
    protected int fps=0;
    protected int tmpFps=0;
    private IAndroidCanvasHelper canvasHelper=IAndroidCanvasHelper.Factory.createAndroidCanvasHelper(IAndroidCanvasHelper.MODE.MODE_ASYNC);
    private BaseOpenGLView.FpsListener listener;
    private IAndroidCanvasHelper.CanvasPainter canvasPainter= (androidCanvas, drawBitmap) -> {
         androidCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//clear canvas
         onMyDraw(androidCanvas);//draw in it
    };

    public BaseOpenGLView(Context context){
        this(context,null);

    }

    public BaseOpenGLView(Context context, AttributeSet set){
        this(context,set,0);
    }

    public BaseOpenGLView(Context context,AttributeSet set,int theme){
        super(context,set,theme);
        baseInit();
        setOpaque(false);
    }


    public void setListener(BaseOpenGLView.FpsListener listener) {
        this.listener = listener;
    }

    protected void calcFps(){
        try {
            tmpFps++;
            long rangeTime=System.currentTimeMillis()-lastTime;
            if (rangeTime>=1000) {
                fps= (int) (tmpFps/rangeTime*1000);
                if (listener != null) {
                    listener.onNewFps(fps);
                }
                lastTime = System.currentTimeMillis();
            }

        }catch (RuntimeException ignored) {

        }
    }



    public abstract void baseInit();

    public void release() {
        canvasHelper.getOutputBitmap().recycle();
    }

    public interface FpsListener{
        /**
         * fps帧数监听
         * @param fps 新的fps
         */
        void onNewFps(int fps);
    }


    protected abstract void onMyDraw(Canvas canvas);


    @Override
    protected void onGLDraw(ICanvasGL canvas) {
        if (canvasHelper.getOutputBitmap()==null) {
            canvasHelper.init(canvas.getWidth(),canvas.getHeight());
        }
        canvasHelper.draw(canvasPainter);
        canvas.drawBitmap(canvasHelper.getOutputBitmap(),0,0);
        //refresh bitmap
        canvas.invalidateTextureContent(canvasHelper.getOutputBitmap());

    }
}
