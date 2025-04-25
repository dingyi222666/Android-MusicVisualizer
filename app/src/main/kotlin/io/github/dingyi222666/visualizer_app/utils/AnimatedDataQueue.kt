package io.github.dingyi222666.visualizer_app.utils

import android.os.Handler
import android.os.Looper
import io.github.dingyi222666.visualizer_app.visualizer.VisualizerConfig
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.min

/**
 * A buffer queue implementation for audio visualization that separates
 * data processing (producer) and drawing (consumer) operations.
 * Optimized version based on MyDataAnimator approach.
 */
class AnimatedDataQueue(
    private val doubleArrayPool: DoubleArrayPool = DoubleArrayPool.instance,
    private val interpolationFrames: Int = 20  // Number of frames for interpolation
) {
    // Current and previous data buffers for interpolation
    private var currentData: DoubleArray? = null
    private var previousData: DoubleArray? = null
    private var interpolatedData: DoubleArray? = null
    
    // Latest data added to the queue
    private var latestData: DoubleArray? = null
    
    // Configuration for visualization
    private var config: VisualizerConfig? = null
    
    // Animation state
    private val isRunning = AtomicBoolean(false)
    private var interpolationProgress = 0f
    private var currentFrameIndex = 0
    
    // Executor for running animation tasks
    private val executor = Executors.newSingleThreadExecutor()
    
    // Consumer handler (UI thread)
    private val consumerHandler = Handler(Looper.getMainLooper())
    
    // Listener for consumer updates
    private var listener: BufferConsumerListener? = null
    
    /**
     * Set the visualization configuration
     */
    fun setConfig(config: VisualizerConfig) {
        this.config = config
    }
    
    /**
     * Set the consumer listener to receive buffer updates
     */
    fun setConsumerListener(listener: BufferConsumerListener) {
        this.listener = listener
    }
    
    /**
     * Process new audio data (called from audio thread)
     */
    fun addData(rawData: DoubleArray) {
        if (!isRunning.get()) return
        
        // Make a copy of the incoming data
        val dataSize = rawData.size
        val newData = doubleArrayPool.get(dataSize)
        rawData.copyInto(newData)
        
        // Store as latest data
        val oldLatest = latestData
        latestData = newData
        
        // Recycle old data if needed
        oldLatest?.let { doubleArrayPool.recycle(it) }
        
        // Notify consumer that new data is available
        consumerHandler.post {
            listener?.onBufferAvailable()
        }
    }
    
    /**
     * Acquire a buffer for drawing (Consumer side, UI thread)
     * This updates the current and previous data buffers
     */
    fun acquireBuffer(): DoubleArray? {
        // Return if no new data is available
        val latest = latestData ?: return null
        
        // Move current data to previous
        val oldPrevious = previousData
        previousData = currentData
        
        // Set current data to latest
        currentData = latest
        latestData = null  // Clear reference to latest data
        
        // Recycle old previous data
        oldPrevious?.let { doubleArrayPool.recycle(it) }
        
        // Reset interpolation
        interpolationProgress = 0f
        currentFrameIndex = 0
        
        return currentData
    }
    
    /**
     * Get interpolated data for smooth transitions between buffers
     * Automatically increments the internal frame index
     */
    fun getInterpolatedData(): DoubleArray? {
        val current = currentData ?: return null
        val previous = previousData ?: return current
        
        if (previous === current) return current
        
        // Increment frame index for next call
        currentFrameIndex = min(interpolationFrames, currentFrameIndex + 1)
        
        // Calculate interpolation progress based on frame index
        interpolationProgress = currentFrameIndex.toFloat() / interpolationFrames
        
        // Reuse or create interpolated buffer
        val dataSize = current.size
        val interpolated = interpolatedData?.let {
            if (it.size != dataSize) {
                doubleArrayPool.recycle(it)
                doubleArrayPool.get(dataSize)
            } else {
                it
            }
        } ?: doubleArrayPool.get(dataSize)
        
        // Interpolate between previous and current data
        for (i in current.indices) {
            val start = if (i < previous.size) previous[i] else 0.0
            val end = current[i]
            interpolated[i] = start + (end - start) * interpolationProgress
        }
        
        // Store interpolated data for potential reuse
        interpolatedData = interpolated
        
        return interpolated
    }
    
    /**
     * Release the currently acquired buffer (Consumer side, UI thread)
     * This is a no-op in the optimized implementation, maintained for interface compatibility
     */
    fun releaseBuffer() {
        // No explicit action needed in this implementation
    }
    
    /**
     * Start the buffer queue processing
     */
    fun start() {
        if (isRunning.getAndSet(true)) return
    }
    
    /**
     * Stop the buffer queue processing
     */
    fun stop() {
        if (!isRunning.getAndSet(false)) return
        
        // Clear all pending messages
        consumerHandler.removeCallbacksAndMessages(null)
        
        // Clean up data
        currentData?.let { doubleArrayPool.recycle(it) }
        previousData?.let { doubleArrayPool.recycle(it) }
        interpolatedData?.let { doubleArrayPool.recycle(it) }
        latestData?.let { doubleArrayPool.recycle(it) }
        
        currentData = null
        previousData = null
        interpolatedData = null
        latestData = null
    }
    
    /**
     * Clean up resources
     */
    fun release() {
        stop()
        
        // Shutdown executor
        executor.shutdown()
    }
    
    /**
     * Listener interface for buffer availability
     */
    interface BufferConsumerListener {
        /**
         * Called when a new buffer is available for consumption
         */
        fun onBufferAvailable()
    }
} 