package com.dingyi.visualizer.drawable;

import android.graphics.Canvas;
import com.dingyi.visualizer.bean.VisualizerDataBean;
import android.content.Context;

public abstract class BaseDrawable {
    
    
    protected Context context; 
    
    private double[] data;
      
    private VisualizerDataBean dataBean;

    public BaseDrawable(Context context){
        this.context=context;
    }

    public void setDataBean(VisualizerDataBean dataBean) {
        this.dataBean = dataBean;
    }

    public VisualizerDataBean getDataBean() {
        return dataBean;
    }

    /**
     * 初始化方法
     */
    public abstract void init();
  
    public void setData(double[] data) {
        this.data = data;
    }

    public double[] getData() {
        return data;
    }

    /**
     * 绘制接口
     * @param canvas 画布
     */
    public abstract void onDraw(Canvas canvas);
        
    public float dp2px(float dp){
        float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (float)(dp * scale + 0.5);
        
    }


}
