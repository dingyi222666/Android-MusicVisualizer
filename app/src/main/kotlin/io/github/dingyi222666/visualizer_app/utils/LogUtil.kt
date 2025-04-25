package io.github.dingyi222666.visualizer_app.utils

import android.util.Log

/**
 * Utility class for logging
 */
object LogUtil {
    private const val TAG = "VisualizerLog"
    
    /**
     * Log an info message
     * @param message The message to log
     */
    @JvmStatic
    fun logI(message: String) {
        Log.i(TAG, message)
    }
    
    /**
     * Log a debug message
     * @param message The message to log
     */
    @JvmStatic
    fun logD(message: String) {
        Log.d(TAG, message)
    }
    
    /**
     * Log an error message
     * @param message The message to log
     */
    @JvmStatic
    fun logE(message: String) {
        Log.e(TAG, message)
    }
} 