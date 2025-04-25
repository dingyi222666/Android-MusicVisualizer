package io.github.dingyi222666.visualizer_app.utils

import android.graphics.PointF
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Utility class for mathematical operations used in visualization
 */
object MathUtil {
    
    /**
     * Calculate a point at a specific distance and angle from a reference point
     */
    fun getPoint(x: Float, y: Float, r: Float, a: Float, pointF: PointF) {
        val tx = (x + r * cos(a * PI / 180))
        val ty = (y + r * sin(a * PI / 180))
        pointF.x = tx.toFloat()
        pointF.y = ty.toFloat()
    }

    /**
     * Calculate a point on a circle at a specific angle
     */
    fun getCirclePoint(cx: Float, cy: Float, cr: Float, angle: Float, pointF: PointF) {
        val x = cx + sin(getDefaultCircle(angle)) * cr
        val y = cy - cos(getDefaultCircle(angle)) * cr
        pointF.x = x
        pointF.y = y
    }

    /**
     * Get the angle between two points (in degrees)
     */
    fun getPointAngle(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        return Math.toDegrees(atan2(y2 - y1, x2 - x1).toDouble()).toFloat() % 360
    }

    /**
     * Calculate the distance between two points
     */
    fun getPointDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        return sqrt((y2 - y1).pow(2) + (x2 - x1).pow(2))
    }

    /**
     * Convert degrees to radians for circle calculations
     */
    fun getDefaultCircle(x: Float): Float {
        return (2 * PI / 360 * x).toFloat()
    }
    
    
} 