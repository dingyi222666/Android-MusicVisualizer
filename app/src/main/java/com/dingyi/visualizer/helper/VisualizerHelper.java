package com.dingyi.visualizer.helper;

import android.media.audiofx.Visualizer;

import com.dingyi.visualizer.bean.VisualizerDataBean;
import com.dingyi.visualizer.filter.BaseFilter;
import com.dingyi.visualizer.filter.FftFilter;

public class VisualizerHelper {

    private int visualizerId;

    private Visualizer visualizer;

    private BaseFilter filter;
    
    private VisualizerDataBean dataBean;
 
    public VisualizerHelper(int id, VisualizerDataBean bean){
        visualizerId=id;
        visualizer=new Visualizer(id);
        dataBean=bean;
        init();
    }

    public void setFilter(BaseFilter filter) {
        this.filter = filter;
    }


    public void init(){
        filter=new FftFilter(dataBean);
        visualizer.setScalingMode(Visualizer.SCALING_MODE_NORMALIZED);
        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        
    }
    
    public void setListener(final Listener listener){
        visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener(){
             @Override
             public void onWaveFormDataCapture(Visualizer p1, byte[] p2, int p3) {
             }

             @Override
             public void onFftDataCapture(Visualizer p1, byte[] p2, int p3) {                 
                 double[] fftData=new double[p2.length/2];            
                 filter.filterFftData(p2,fftData);
                 listener.onFftDataCapture(fftData);
                 System.gc();
             }
           
        }, Visualizer.getMaxCaptureRate()/2,true,true);
    }
    
    public void start(){
        visualizer.setEnabled(true);
    }
    
    public void stop(){
        visualizer.setEnabled(false);
    }
    
    public void release(){
        visualizer.release();
    }
    
    public interface Listener {
        /**
         * fft数据获取方法
         * @param fft fft数据
         */
        void onFftDataCapture(double[] fft);
        
    }
    
    
}
