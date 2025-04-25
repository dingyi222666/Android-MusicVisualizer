package io.github.dingyi222666.visualizer_app.drawable

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import io.github.dingyi222666.visualizer_app.visualizer.VisualizerConfig

/**
 * Line-based audio visualization drawable
 */
class LineDrawable(context: Context) : BaseDrawable(context) {
    
    // Paint for drawing lines
    private val linePaint = Paint()
    
    // Canvas dimensions
    private var width: Float = 0f
    private var height: Float = 0f
    
    // Number of lines to draw
    private var lineCount: Int = 0
    
    // Reusable rectangle for drawing
    private val rect = RectF()
    
    /**
     * Initialize the drawable with default settings
     */
    override fun init() {
        val config = getConfig() ?: return
        
        linePaint.apply {
            strokeWidth = 2f
            color = config.colors.primaryColor
            style = Paint.Style.FILL
        }
    }
    
    /**
     * Called when configuration changes
     */
    override fun onConfigChanged(config: VisualizerConfig) {
        init()
    }
    
    /**
     * Initialize dimensions based on canvas size
     */
    private fun prepareDrawing(canvas: Canvas) {
        val config = getConfig() ?: return
        
        width = canvas.width.toFloat()
        height = canvas.height.toFloat()
        lineCount = (width / config.line.width).toInt()
    }
    
    /**
     * Draw the visualization
     */
    override fun onDraw(canvas: Canvas) {
        val config = getConfig() ?: return
        val data = getData() ?: return
        
        // Initialize dimensions if needed
        if (lineCount == 0) {
            prepareDrawing(canvas)
        }
        
        val startIndex = config.dataStartIndex
        val lineWidth = config.line.width
        val lineMargin = config.line.margin
        
        // Draw each bar
        for (i in 1 until lineCount) {
            val dataIndex = startIndex + i
            
            // Get amplitude for this bar (default to 1 if out of bounds)
            val amplitude = if (dataIndex < data.size) data[dataIndex].toFloat() else 1f
            
            // Scale the amplitude to the display height
            val barHeight = (height / 3) / 120 * amplitude
            
            // Calculate bar rectangle
            rect.left = (i - 1) * lineWidth
            rect.top = height - barHeight
            rect.right = i * lineWidth - lineMargin
            rect.bottom = height
            
            // Draw the bar
            canvas.drawRect(rect, linePaint)
        }
    }
} 