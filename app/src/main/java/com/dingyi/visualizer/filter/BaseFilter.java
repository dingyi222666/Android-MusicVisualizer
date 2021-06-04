package com.dingyi.visualizer.filter;

import com.dingyi.visualizer.bean.VisualizerDataBean;

public abstract class BaseFilter {

    protected VisualizerDataBean dataBean;

    public BaseFilter(VisualizerDataBean bean) {
        dataBean=bean;
    }

    /**
     * 过滤fft数据 必须实现
     * @param sourceData 源数据
     * @param data 过滤后数据源
     */
    public abstract void filterFftData(byte[] sourceData,double[] data);
   
    public void filterWaveFormDataCapture(byte[] sourceData,double[] data) {
         
    };
    
    
 }
    
    

