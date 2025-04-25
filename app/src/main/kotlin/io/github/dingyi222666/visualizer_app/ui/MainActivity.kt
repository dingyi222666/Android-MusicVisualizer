package io.github.dingyi222666.visualizer_app.ui

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import io.github.dingyi222666.visualizer_app.R
import io.github.dingyi222666.visualizer_app.databinding.ActivityMainBinding
import io.github.dingyi222666.visualizer_app.utils.LogUtil
import io.github.dingyi222666.visualizer_app.view.VisualizerView
import io.github.dingyi222666.visualizer_app.visualizer.VisualizerConfig
import io.github.dingyi222666.visualizer_app.drawable.DrawableFactory
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val visualizerView: VisualizerView
        get() = binding.baseView
    
    // Track if visualization is started
    private var isStarted = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (checkPermissions()) {
            startVisualizer()
        }
    }

    private fun startVisualizer() {
        LogUtil.logI("Starting visualizer")
        
        // Set up the visualizer with audio session 0 (for global output)
        visualizerView.setAudioSessionId(0)
        
        val config = DrawableFactory.createDefaultConfig().apply {
            // Main settings
            maxFps = 120
            showFps = true
            
            // Data settings
            dataStartIndex = 16
            
            // Circle visualization settings
            circle.apply {
                radius = 360f
                angle = 2f
                lineCount = 360f / 2f
                showPoints = true
            }
            
            // Line visualization settings
            line.apply {
                margin = 1f
                width = windowManager.defaultDisplay.width / 80f
            }
            
            // Color settings
            colors.apply {
                circleLineWidth = 8f
            }
        }

        visualizerView.setConfig(config)
        visualizerView.setDrawableType(DrawableFactory.VisualizationType.CIRCLE)
        visualizerView.start()
        isStarted = true
    }


    private fun checkPermissions(): Boolean {

        try {
            var permissions = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS).requestedPermissions
            val requestPermissions = ArrayList<String>()
            var flag = 0

            permissions = permissions ?: return false

            for (permission in permissions) {
                flag = flag or checkCallingOrSelfPermission(permission)

                if (flag != 0) {
                    requestPermissions.add(permission)
                }
            }
            
            if (flag == -1) {
                requestPermissions(requestPermissions.toTypedArray(), 114514)
            }
            
            return flag == 0
        } catch (ignored: PackageManager.NameNotFoundException) {
            // Ignore
        }
        
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        var flag = 0
        for (i in grantResults) {
            flag = flag or i
        }
        
        if (flag != 0) {
            Toast.makeText(this, "有权限不同意，1000ms后退出", Toast.LENGTH_SHORT).show()
            visualizerView.postDelayed({ finish() }, SystemClock.uptimeMillis() + 1000)
        } else {
            startVisualizer()
        }
        
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        super.onDestroy()
        
        if (isStarted) {
            visualizerView.stop()
            visualizerView.release()
            isStarted = false
        }
    }
} 