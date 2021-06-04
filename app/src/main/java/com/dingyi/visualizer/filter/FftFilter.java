package com.dingyi.visualizer.filter;

import com.dingyi.visualizer.bean.VisualizerDataBean;
import com.dingyi.visualizer.util.MathUtil;

public class FftFilter extends BaseFilter {


    private double dbValue = 75;

    private double smailDbSize = 25;

    private double minDbValue = 15;

    public FftFilter(VisualizerDataBean bean) {
        super(bean);

    }




    @Override
    public void filterFftData(byte[] sourceData, double[] data) {

        for (int i = 0; i < sourceData.length; i = i + 2) {
            double base = Math.hypot(sourceData[i], sourceData[i + 1]);
            base = Math.abs(base);
            if (base == 0) {
                data[i / 2] = 1;
                continue;//不用参与下面的计算
            }
            //算db
            base = Math.abs(dbValue * Math.log10(base));

            data[i / 2] = base;

        }

        //去掉垃圾(噪声)db
        double[] newData = new double[data.length];

        int count = 0;
        int newindex = 0;
        for (int i = 0; i < newData.length; i = i + dataBean.getSkipArrayIndex()) {
            double d = data[i];

            if (d < minDbValue && count < smailDbSize) {
                count++;
                newindex++;
                newData[newindex] = d;
            } else if (d < minDbValue && count == smailDbSize) {
                //no todo
            } else {
                newindex++;
                newData[newindex] = d;
            }
        }



        for (int i=1;i<3;i++) {
            //平滑算法
            switch (dataBean.getSmoothMode()) {
                case Linear3:
                    newData = MathUtil.linearSmooth3(newData, newData.length);
                    break;
                case Linear5:
                    newData = MathUtil.linearSmooth5(newData, newData.length);
                    break;
                case Linear7:
                    newData = MathUtil.linearSmooth5(newData, newData.length);
                    break;
                default:
            }
        }

        double[] tmp=new double[12];

        for (int i=0;i<12;i++) {
            if (i<6) {
                tmp[i]=newData[i];
            }else {
                tmp[i]=newData[newData.length-i-1];
            }
        }

        switch (dataBean.getSmoothMode()){
            case Linear3:
                tmp=MathUtil.linearSmooth3(tmp, tmp.length);
                break;
            case Linear5:
               tmp= MathUtil.linearSmooth5(tmp, tmp.length);
                break;
            case Linear7:
               tmp= MathUtil.linearSmooth7(tmp, tmp.length);
                break;
            default:
        }

        for (int i=0;i<12;i++) {
            if (i<6) {
                newData[i]=tmp[i];
            }else {
               newData[newData.length-i-1]= tmp[i];
            }
        }



        System.arraycopy(newData, 0, data, 0, newData.length);
        System.gc();

    }


}
