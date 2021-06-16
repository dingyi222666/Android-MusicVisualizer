package com.dingyi.visualizer.util;

import android.graphics.PointF;

/**
 * @author dingyi
 * @date 2021-5-4
 */
public class MathUtil {

    public static void getPoint(float x, float y, float r, float a, PointF pointF) {
        float tx = (float) (x + r * Math.cos(a * Math.PI / 180));
        float ty = (float) (y + r * Math.sin(a * Math.PI / 180));
        pointF.x = tx;
        pointF.y = ty;
    }

    public static double[] linearSmooth3(double[] in, int n) {
        int i;
        int length = in.length;
        double[] out = new double[length];

        if (n < 3) {
            for (i = 0; i <= n - 1; i++) {
                out[i] = in[i];
            }
        } else {
            out[0] = (5.0 * in[0] + 2.0 * in[1] - in[2]) / 6.0;
            for (i = 1; i <= n - 2; i++) {
                out[i] = (in[i - 1] + in[i] + in[i + 1]) / 3.0;

            }
            out[n - 1] = (5.0 * in[n - 1] + 2.0 * in[n - 2] - in[n - 3]) / 6.0;
        }

        return out;

    }

    public static double[] linearSmooth7(double[] in, int n) {
        int i;
        int length = in.length;
        double[] out = new double[length];

        if (n < 7) {
            for (i = 0; i <= n - 1; i++) {
                out[i] = in[i];
            }
        } else {
            out[0] = (13.0 * in[0] + 10.0 * in[1] + 7.0 * in[2] + 4.0 * in[3] +
                    in[4] - 2.0 * in[5] - 5.0 * in[6]) / 28.0;
            out[1] = (5.0 * in[0] + 4.0 * in[1] + 3 * in[2] + 2 * in[3] +
                    in[4] - in[6]) / 14.0;
            out[2] = (7.0 * in[0] + 6.0 * in[1] + 5.0 * in[2] + 4.0 * in[3] +
                    3.0 * in[4] + 2.0 * in[5] + in[6]) / 28.0;
            for (i = 3; i <= n - 4; i++) {
                out[i] = (in[i - 3] + in[i - 2] + in[i - 1] + in[i] + in[i + 1] + in[i + 2] + in[i + 3]) / 7.0;

            }
            out[n - 3] = (7.0 * in[n - 1] + 6.0 * in[n - 2] + 5.0 * in[n - 3] +
                    4.0 * in[n - 4] + 3.0 * in[n - 5] + 2.0 * in[n - 6] + in[n - 7]) / 28.0;
            out[n - 2] = (5.0 * in[n - 1] + 4.0 * in[n - 2] + 3.0 * in[n - 3] +
                    2.0 * in[n - 4] + in[n - 5] - in[n - 7]) / 14.0;
            out[n - 1] = (13.0 * in[n - 1] + 10.0 * in[n - 2] + 7.0 * in[n - 3] +
                    4 * in[n - 4] + in[n - 5] - 2 * in[n - 6] - 5 * in[n - 7]) / 28.0;
        }

        return out;
    }



    public static double[] linearSmooth5(double[] in, int n) {
        int i;
        int length = in.length;
        double[] out = new double[length];

        if (n < 5) {
            for (i = 0; i <= n - 1; i++) {
                out[i] = in[i];
            }
        } else {
            out[0] = (3.0 * in[0] + 2.0 * in[1] + in[2] - in[4]) / 5.0;

            out[1] = (4.0 * in[0] + 3.0 * in[1] + 2 * in[2] + in[3]) / 10.0;

            for (i = 2; i <= n - 3; i++) {
                out[i] = (in[i - 2] + in[i - 1] + in[i] + in[i + 1] + in[i + 2]) / 5.0;

            }

            out[n - 2] = (4.0 * in[n - 1] + 3.0 * in[n - 2] + 2 * in[n - 3] + in[n - 4]) / 10.0;

            out[n - 1] = (3.0 * in[n - 1] + 2.0 * in[n - 2] + in[n - 3] - in[n - 5]) / 5.0;

        }

        return out;
    }


    public static float getPointAngle(float x1, float y1, float x2, float y2) {
        return (float) (Math.atan2(y2 - y1, x2 - x1) * 180 / Math.PI % 360);

    }

    public static float getPointDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow((y2 - y1), 2) + Math.pow((x2 - x1), 2));

    }


    public static double getDefaultCircle(float x) {
        return (2 * Math.PI / 360) * x;

    }

    public static void getCirclePoint(float cx, float cy, float cr, float angle, PointF pointF) {
        float x = cx + (float) Math.sin(getDefaultCircle(angle)) * cr;
        float y = cy - (float) Math.cos(getDefaultCircle(angle)) * cr;
        pointF.x = x;
        pointF.y = y;
    }

}
