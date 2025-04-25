package io.github.dingyi222666.visualizer_app.drawable

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import io.github.dingyi222666.visualizer_app.utils.MathUtil
import io.github.dingyi222666.visualizer_app.visualizer.VisualizerConfig

/**
 * Circle-based audio visualization drawable
 */
class CircleDrawable(context: Context) : BaseDrawable(context) {
    
    // Arrays for drawing
    private var linesArray: FloatArray? = null
    private var pointsArray: FloatArray? = null
    
    // Canvas dimensions
    private var centerX: Float = 0f
    private var centerY: Float = 0f
    private var baseRadius: Float = 0f
    private var radius: Float = 0f
    
    // Rotation variables
    private var rotationAngle: Float = 0f
    private val rotationSpeed: Float = 0.01f
    
    // Paint objects
    private val circlePaint = Paint()
    private val arcPaint = Paint()
    
    // Reusable point objects for calculations
    private val circlePointF = PointF()
    private val newCirclePointF = PointF()
    private val twoCirclePointF = PointF()
    
    /**
     * Initialize the drawable with default settings
     */
    override fun init() {
        val config = getConfig() ?: return
        
        // Circle paint for lines and points
        circlePaint.apply {
            color = config.colors.primaryColor
            strokeWidth = config.colors.circleLineWidth
            strokeCap = Paint.Cap.ROUND
            style = Paint.Style.FILL
        }
        
        // Arc paint for the center circle
        arcPaint.apply {
            color = config.colors.circleOutlineColor
            strokeCap = Paint.Cap.ROUND
            style = Paint.Style.STROKE
            strokeWidth = config.colors.circleOutlineWidth
        }
    }
    
    /**
     * Called when configuration changes
     */
    override fun onConfigChanged(config: VisualizerConfig) {
        // Reinitialize paint objects with new colors/widths
        init()
    }
    
    /**
     * Initialize data arrays and dimensions based on canvas size
     */
    private fun prepareDrawing(canvas: Canvas) {
        val config = getConfig() ?: return
        
        val lineCount = config.circle.lineCount.toInt()
        
        // Create arrays if needed or if size has changed
        if (linesArray == null || linesArray!!.size != lineCount * 4) {
            linesArray = FloatArray(lineCount * 4)
        }
        
        if (pointsArray == null || pointsArray!!.size != lineCount * 2) {
            pointsArray = FloatArray(lineCount * 2)
        }
        
        // Calculate center and radius based on canvas dimensions
        centerX = canvas.width / 2f
        centerY = canvas.height / 2f
        baseRadius = config.circle.radius
        radius = baseRadius
    }
    
    /**
     * Draw the visualization
     */
    override fun onDraw(canvas: Canvas) {
        val config = getConfig() ?: return
        val data = getData() ?: return
        
        // Initialize dimensions if needed
        if (centerY == 0f) {
            prepareDrawing(canvas)
        }
        
        // Update rotation angle
        rotationAngle = (rotationAngle + rotationSpeed) % 360
        
        val skipStartIndex = config.dataStartIndex
        val lineCount = config.circle.lineCount.toInt()
        val angle = config.circle.angle
        val lines = linesArray ?: return
        val points = pointsArray ?: return
        
        // Calculate points for lines and dots
        for (i in 0 until lineCount) {
            // Get point on the base circle with rotation
            MathUtil.getCirclePoint(
                centerX, centerY, radius + 3.px,
                i * angle + 270 + rotationAngle, circlePointF
            )
            
            // Get amplitude at this position
            val amplitude = if (skipStartIndex + i < data.size) {
                data[skipStartIndex + i].toFloat()
            } else {
                0f
            }
            
            // Calculate outer point (extending outward from the circle)
            MathUtil.getPoint(
                centerX, centerY,
                MathUtil.getPointDistance(centerX, centerY, circlePointF.x, circlePointF.y) + amplitude,
                MathUtil.getPointAngle(centerX, centerY, circlePointF.x, circlePointF.y), 
                newCirclePointF
            )
            
            // Calculate inner point (extending inward from the circle)
            MathUtil.getPoint(
                centerX, centerY,
                MathUtil.getPointDistance(centerX, centerY, circlePointF.x, circlePointF.y) - amplitude - 4.px,
                MathUtil.getPointAngle(centerX, centerY, circlePointF.x, circlePointF.y), 
                twoCirclePointF
            )
            
            // Store line coordinates (from circle point to outer point)
            lines[i * 4] = circlePointF.x
            lines[i * 4 + 1] = circlePointF.y
            lines[i * 4 + 2] = newCirclePointF.x
            lines[i * 4 + 3] = newCirclePointF.y
            
            // Store inner point coordinates
            points[i * 2] = twoCirclePointF.x
            points[i * 2 + 1] = twoCirclePointF.y
        }
        
        // Draw the base circle
        canvas.drawArc(
            centerX - radius, 
            centerY - radius, 
            centerX + radius, 
            centerY + radius, 
            0f, 360f, false, arcPaint
        )
        
        // Draw the radial lines
        canvas.drawLines(lines, circlePaint)
        
        // Draw the inner points if enabled
        if (config.circle.showPoints) {
            canvas.drawPoints(points, circlePaint)
        }
    }
    
    /**
     * Release resources
     */
    override fun release() {
        super.release()
        linesArray = null
        pointsArray = null
    }
} 