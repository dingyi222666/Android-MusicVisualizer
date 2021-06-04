package com.dingyi.visualizer.animation;

import com.dingyi.visualizer.bean.VisualizerDataBean;
import com.dingyi.visualizer.util.LogUtil;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author dingyi
 */
public class MyDataAnimator {

   private double[] lastData;
   private double[] nowData;
   private double[] tmpData;
   private double[] lastAddData;
   private VisualizerDataBean dataBean;
   private AnimationListener animationListener;
   private boolean isLoadData=false;
   private boolean isRunning =true;
   private ThreadPoolExecutor executors;
   private float animationValue=0;

   public MyDataAnimator() {
     executors=(ThreadPoolExecutor)Executors.newCachedThreadPool();

   }

   public void setAnimationListener(AnimationListener animationListener) {
      this.animationListener = animationListener;
   }


   public void addData(double[] nowData) {
      if (tmpData == null) {
         tmpData = new double[nowData.length];
      }
      //丢弃大部分数据
      lastAddData=nowData;
      
   }


   public void setDataBean(VisualizerDataBean dataBean) {
      this.dataBean = dataBean;

      //init();
   }

   public void getNowData() {
        lastData =nowData;
        nowData = lastAddData;
        System.gc();
   }


   public boolean isRunning() {
      return !isRunning;
   }

   private void init() {
      executors.execute(() -> {
         synchronized (this) {
            while (isRunning) {
               System.gc();
               animationValue = (float) (animationValue + 0.25);
               if (animationValue > 1) {
                  animationValue = 0;
               }
               if (animationValue < 0.3 && !isLoadData) {
                  getNowData();
                  isLoadData = true;
               } else if (animationValue > 0.8 && isLoadData) {
                  isLoadData = false;
               }
               if (Arrays.equals(lastAddData,lastData)||nowData == null || lastData == null || nowData[1] == 0) {
                  isLoadData = false;
                  animationValue = 0f;
                  continue;
               }

               for (int i = 1; i < lastData.length; i++) {
                  double old = lastData[i];
                  double now = nowData[i];
                  double animValue = old + animationValue * (now - old);
                  tmpData[i] = animValue;
               }
               animationListener.onNewData(tmpData);
               try {
                  this.notifyAll();
                  this.wait(1000 / dataBean.getMaxFps());
               } catch (InterruptedException ignored) {
               }
            }
         }
      });

   }


   public void start() {
      init();
   }



   public void stop() {
      isRunning = false;

   }

   public void stopAll() {
      executors.shutdown();
   }

   public interface AnimationListener {
      /**
       * 获取新数据时候的接口
       *
       * @param data 动画数据
       */
      void onNewData(double[] data);
   }


}
