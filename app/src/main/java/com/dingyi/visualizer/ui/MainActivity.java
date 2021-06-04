package com.dingyi.visualizer.ui;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import com.dingyi.visualizer.R;
import com.dingyi.visualizer.view.VisualizerOpenGLView;
import com.dingyi.visualizer.bean.VisualizerDataBean;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

   
    private VisualizerOpenGLView view;
    

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view=findViewById(R.id.baseView);

        if (view != null && checkPermissions()) {
           startVisualizer();
        }

    }


    private void startVisualizer() {
        VisualizerDataBean bean = view.getData();
        bean.setCircleRadius(260);
        bean.setMaxFps(55);
        bean.setSkipArrayIndex(1);
        bean.setCircleAngle(3);
        bean.setCircleLineCount(360 / 3);
        bean.setShowFps(true);
        bean.setShowCirclePoint(false);
        bean.setSmoothMode(VisualizerDataBean.SmoothMode.Linear3);
        bean.setLineMargin(1);
        bean.setLineWidth(getWindowManager().getDefaultDisplay().getWidth()/128);
        bean.getColorData()
                .setCirclePaintWidth(6);

        //view.setDrawable(new LineDrawable(this));

        view.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT<23) {
            return true;
        }
        try {
            String[] permissions=getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS).requestedPermissions;
            List<String> requestPermissions=new ArrayList<>();
            int flag=0;


            for (String permission:permissions) {
                flag=flag|checkCallingOrSelfPermission(permission);
                if (flag==1) {
                    requestPermissions.add(permission);
                }
            }

            if (flag==1) {
                requestPermissions(requestPermissions.toArray(new String[requestPermissions.size()]),114514);
            }

            return flag==1;

        } catch (PackageManager.NameNotFoundException ignored) {

        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int flag=0;
        for (int i:grantResults) {
            flag=flag|i;
        }
        if (flag!=0) {
            Toast.makeText(this, "有权限不同意，1000ms后退出", Toast.LENGTH_SHORT).show();
        }else {
            startVisualizer();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
       super.onDestroy();
       view.stop();
       view.release();
    }
    
    
    

}
