package io.github.dingyi222666.visualizer_app.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import io.github.dingyi222666.visualizer_app.drawable.BaseDrawable
import io.github.dingyi222666.visualizer_app.drawable.DrawableFactory
import io.github.dingyi222666.visualizer_app.utils.AnimatedDataQueue
import io.github.dingyi222666.visualizer_app.visualizer.AudioVisualizer
import io.github.dingyi222666.visualizer_app.visualizer.VisualizerConfig

/**
 * A view that displays audio visualizations using various drawable types
 */
class VisualizerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseRenderView(
    context,
    attrs,
    defStyleAttr
), AnimatedDataQueue.BufferConsumerListener {

    // Current drawable for visualization
    private var drawable: BaseDrawable? = null

    // Animation queue for smooth transitions
    private val animatedQueue = AnimatedDataQueue()

    // Configuration
    private var config = DrawableFactory.createDefaultConfig()

    // Audio visualizer
    private var audioVisualizer: AudioVisualizer? = null

    // For FPS display if enabled
    private val debugPaint by lazy {
        Paint().apply {
            color = Color.BLACK
            textSize = 36f
            isAntiAlias = true
        }
    }

    // Performance tracking

    private val fpsText = StringBuilder(16)

    // Flag to track if we need to acquire a new buffer
    private var needsNewBuffer = true

    init {
        // Set up the animation queue

        animatedQueue.setConsumerListener(this)

        // Default drawable is Circle
        setDrawableType(DrawableFactory.VisualizationType.CIRCLE)
    }

    /**
     * Called when a new buffer is available for drawing
     */
    override fun onBufferAvailable() {
        needsNewBuffer = true
        postInvalidateOnAnimation()
    }

    /**
     * Set the audio session ID for visualization
     */
    fun setAudioSessionId(sessionId: Int) {
        // Release any existing visualizer
        release()

        // Create a new audio visualizer
        audioVisualizer = AudioVisualizer(sessionId).apply {
            // Set up data capture listener
            setDataCaptureListener(object : AudioVisualizer.VisualizerDataListener {
                override fun onFftDataCapture(fftData: DoubleArray): Boolean {
                    // Add data to the animation queue
                    animatedQueue.addData(fftData)
                    return true
                }
            })

            // Use the default pipeline
            useDefaultPipeline()
        }
    }

    /**
     * Change the visualization drawable type
     */
    fun setDrawableType(type: DrawableFactory.VisualizationType) {
        // Release the old drawable
        drawable?.release()

        // Create a new drawable
        drawable = DrawableFactory.createDrawable(context, type, config).also {
            it.setDataQueue(animatedQueue)
            it.init()
        }
    }

    /**
     * Set a custom configuration
     */
    fun setConfig(config: VisualizerConfig) {
        this.config = config

        // Update dependent components
        drawable?.setConfig(config)
        //   animatedQueue.setConfig(config)
    }

    /**
     * Get the current configuration
     */
    fun getConfig(): VisualizerConfig {
        return config
    }

    /**
     * Start visualization
     */
    fun start() {
        audioVisualizer?.start()
        animatedQueue.start()
    }

    /**
     * Stop visualization
     */
    fun stop() {
        audioVisualizer?.stop()
        animatedQueue.stop()
    }

    /**
     * Release resources
     */
    fun release() {
        stop()
        audioVisualizer?.release()
        audioVisualizer = null
        drawable?.release()
    }

    /**
     * Draw the visualization
     */
    override fun renderFrame(canvas: Canvas) {
        // Draw the visualization
        drawable?.onDraw(canvas)

        // Calculate FPS if enabled
        if (config.showFps) {
            // Use StringBuilder to avoid string concatenation
            fpsText.clear()
            fpsText.append("FPS: ")
            fpsText.append(currentFps)
            canvas.drawText(fpsText, 0, fpsText.length, 20f, 50f, debugPaint)
        }

    }


} 