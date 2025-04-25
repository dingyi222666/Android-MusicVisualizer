package io.github.dingyi222666.visualizer_app.drawable

import android.content.Context
import android.graphics.Canvas
import io.github.dingyi222666.visualizer_app.utils.AnimatedDataQueue
import io.github.dingyi222666.visualizer_app.utils.DoubleArrayPool
import io.github.dingyi222666.visualizer_app.utils.LogUtil
import io.github.dingyi222666.visualizer_app.visualizer.VisualizerConfig

/**
 * Base class for all visualization drawables
 */
abstract class BaseDrawable(protected val context: Context) {

    // Configuration
    private var config: VisualizerConfig? = null
    
    // Data queue reference
    private var dataQueue: AnimatedDataQueue? = null
    
    // Current interpolated data
    private var interpolatedData: DoubleArray? = null

    // Double array pool for memory management
    protected val doubleArrayPool = DoubleArrayPool.instance
    
    // Frame counter for tracking FPS if needed
    private var frameCounter = 0
    private var lastFpsUpdateTime = 0L
    private var currentFps = 0

    /**
     * Set the visualization configuration
     */
    fun setConfig(config: VisualizerConfig) {
        this.config = config
        onConfigChanged(config)
    }

    /**
     * Called when the configuration changes
     */
    protected open fun onConfigChanged(config: VisualizerConfig) {
        // Override in subclasses if needed
    }

    /**
     * Get the current visualization configuration
     */
    fun getConfig(): VisualizerConfig? {
        return config
    }

    /**
     * Set the data queue for this drawable
     */
    fun setDataQueue(dataQueue: AnimatedDataQueue) {
        this.dataQueue = dataQueue
        
        // Register as listener to get notified of data availability
        dataQueue.setConsumerListener(object : AnimatedDataQueue.BufferConsumerListener {
            override fun onBufferAvailable() {
                // This is called when new data is available - could trigger a redraw if needed
            }
        })
    }

    /**
     * Get the current visualization data from the queue
     * This method now retrieves interpolated data directly
     */
    protected fun getData(): DoubleArray? {
        dataQueue?.let { queue ->
            queue.acquireBuffer()
            // Get already interpolated data from the queue
            interpolatedData = queue.getInterpolatedData()

            LogUtil.logE(interpolatedData.contentToString())
            // If no data is available, return null
            if (interpolatedData == null) {
                return null
            }

            // Update FPS counter if needed
            if (config?.showFps == true) {
                updateFpsCounter()
            }

            return interpolatedData
        }
        
        return null
    }
    
    /**
     * Update FPS counter
     */
    private fun updateFpsCounter() {
        val currentTime = System.currentTimeMillis()
        frameCounter++
        
        // Update FPS calculation once per second
        if (currentTime - lastFpsUpdateTime >= 1000) {
            currentFps = frameCounter
            frameCounter = 0
            lastFpsUpdateTime = currentTime
        }
    }
    
    /**
     * Get the current FPS
     */
    protected fun getCurrentFps(): Int {
        return currentFps
    }

    /**
     * Initialize the drawable
     * This is called once when the drawable is created
     */
    abstract fun init()

    /**
     * Draw the visualization
     *
     * @param canvas The canvas to draw on
     */
    abstract fun onDraw(canvas: Canvas)

    /**
     * Convert density-independent pixels to pixels
     */
    protected val Int.px: Float
        get() {
            val scale = context.resources.displayMetrics.scaledDensity
            return (this * scale + 0.5f)
        }

    /**
     * Release all resources
     */
    open fun release() {
        interpolatedData?.let { 
            dataQueue?.release()
        }
        interpolatedData = null
    }
} 