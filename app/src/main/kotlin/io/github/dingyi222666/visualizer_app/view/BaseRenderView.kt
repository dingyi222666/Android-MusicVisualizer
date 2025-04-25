package io.github.dingyi222666.visualizer_app.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.View

abstract class BaseRenderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var lastFpsUpdateTime: Long = System.currentTimeMillis()
    private var frameCount: Int = 0
    protected var currentFps: Int = 0
    private var renderFrameCallback: FrameCallback? = null
    
    /**
     * Flag indicating whether rendering is currently enabled
     */
    private var renderingEnabled = true
    
    init {
        initialize()
    }
    
    /**
     * Interface for receiving frame rate updates
     */
    interface FrameCallback {
        /**
         * Called when frame rate is updated
         * @param frameRate Current frames per second
         */
        fun onFrameRateUpdate(frameRate: Int)
    }
    
    /**
     * Set a callback to receive frame rate updates
     * @param callback The callback to receive updates
     */
    fun setFrameCallback(callback: FrameCallback) {
        this.renderFrameCallback = callback
    }
    
    /**
     * Enables continuous rendering
     * @return true if rendering was previously disabled, false if it was already enabled
     */
    fun startRendering(): Boolean {
        val wasDisabled = !renderingEnabled
        if (wasDisabled) {
            renderingEnabled = true
            resetFrameStats()
            requestNextFrame()
        }
        return wasDisabled
    }
    
    /**
     * Disables continuous rendering
     * @return true if rendering was previously enabled, false if it was already disabled
     */
    fun stopRendering(): Boolean {
        val wasEnabled = renderingEnabled
        renderingEnabled = false
        return wasEnabled
    }
    
    /**
     * Checks if rendering is currently enabled
     * @return true if rendering is enabled, false otherwise
     */
    fun isRenderingEnabled(): Boolean {
        return renderingEnabled
    }
    
    /**
     * Resets frame rate statistics
     */
    private fun resetFrameStats() {
        frameCount = 0
        lastFpsUpdateTime = System.currentTimeMillis()
        currentFps = 0
    }
    
    /**
     * Calculates and updates the current frame rate
     * Updates once per second to avoid excessive calculations
     */
    protected fun updateFrameRate() {
        frameCount++
        
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - lastFpsUpdateTime
        
        // Update FPS calculation once per second
        if (elapsedTime >= 1000) {
            // Calculate frames per second
            currentFps = (frameCount * 1000 / elapsedTime).toInt()
            
            // Notify listeners about frame rate update
            renderFrameCallback?.onFrameRateUpdate(currentFps)
            
            // Reset counters
            frameCount = 0
            lastFpsUpdateTime = currentTime
        }
    }
    
    /**
     * Requests the next frame to continue the render loop
     */
    private fun requestNextFrame() {
        if (renderingEnabled) {
            this.postInvalidateOnAnimation()
        }
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        if (renderingEnabled) {
            // Clear canvas with transparent color
            // canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            
            // Perform custom rendering
            renderFrame(canvas)
            
            // Update frame rate statistics
            updateFrameRate()
            
            // Continue rendering loop by requesting next frame
            requestNextFrame()
        }
    }
    
    /**
     * Called when view is detached from window to release resources
     */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopRendering()
        cleanupResources()
    }
    
    /**
     * Initialize the view - should be implemented by subclasses
     */
    protected open fun initialize() {}
    
    /**
     * Custom rendering method to be implemented by subclasses
     * @param canvas Canvas to draw on
     */
    protected abstract fun renderFrame(canvas: Canvas)
    
    /**
     * Release resources when the view is no longer needed
     */
    protected open fun cleanupResources() {
        // Override in subclasses if needed
    }
}