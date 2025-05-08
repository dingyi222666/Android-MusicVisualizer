package io.github.dingyi222666.visualizer_app.visualizer

/**
 * Configuration for visualization
 */
data class VisualizerConfig(
    // Core settings
    var maxFps: Int = 60,
    var showFps: Boolean = false,
    
    // Visualization settings by type
    var circle: CircleConfig = CircleConfig(),
    var line: LineConfig = LineConfig(),
    
    // Data settings
    var dataStartIndex: Int = 2, // Renamed from skipStartArrayIndex for clarity
    
    // Color configuration
    var colors: ColorConfig = ColorConfig()
) {
    // Backward compatibility properties
    @Deprecated("Use dataStartIndex instead", ReplaceWith("dataStartIndex"))
    var skipStartArrayIndex: Int
        get() = dataStartIndex
        set(value) { dataStartIndex = value }
    
    @Deprecated("Use circle.lineCount instead", ReplaceWith("circle.lineCount"))
    var circleLineCount: Float
        get() = circle.lineCount
        set(value) { circle.lineCount = value }
    
    @Deprecated("Use circle.angle instead", ReplaceWith("circle.angle"))
    var circleAngle: Float
        get() = circle.angle
        set(value) { circle.angle = value }
    
    @Deprecated("Use circle.radius instead", ReplaceWith("circle.radius"))
    var circleRadius: Float
        get() = circle.radius
        set(value) { circle.radius = value }
    
    @Deprecated("Use circle.showPoints instead", ReplaceWith("circle.showPoints"))
    var showCirclePoint: Boolean
        get() = circle.showPoints
        set(value) { circle.showPoints = value }
    
    @Deprecated("No longer used")
    var skipArrayIndex: Int = 3
    
    @Deprecated("Use line.width instead", ReplaceWith("line.width"))
    var lineWidth: Float
        get() = line.width
        set(value) { line.width = value }
    
    @Deprecated("Use line.margin instead", ReplaceWith("line.margin"))
    var lineMargin: Float
        get() = line.margin
        set(value) { line.margin = value }
    
    @Deprecated("Use colors instead", ReplaceWith("colors"))
    var colorData: VisualizerColorConfig
        get() = VisualizerColorConfig(
            arcPaintWidth = colors.circleOutlineWidth,
            arcPaintColor = colors.circleOutlineColor,
            circlePaintWidth = colors.circleLineWidth,
            circlePaintColor = colors.primaryColor,
            linePaintColor = colors.primaryColor
        )
        set(value) {
            colors.circleOutlineWidth = value.arcPaintWidth
            colors.circleOutlineColor = value.arcPaintColor
            colors.circleLineWidth = value.circlePaintWidth
            colors.primaryColor = value.circlePaintColor
        }
}

/**
 * Circle visualization configuration
 */
data class CircleConfig(
    var lineCount: Float = 120f,
    var angle: Float = 3f,
    var radius: Float = 300f,
    var showPoints: Boolean = true
)

/**
 * Line visualization configuration
 */
data class LineConfig(
    var width: Float = 40f,
    var margin: Float = 24f
)

/**
 * Color configuration for visualization
 */
data class ColorConfig(
    var primaryColor: Int = 0xFFEC407A.toInt(),
    var circleOutlineColor: Int = 0xFFEC407A.toInt(),
    var circleOutlineWidth: Float = 4f,
    var circleLineWidth: Float = 13f
)

/**
 * Legacy color configuration (for backward compatibility)
 * @deprecated Use ColorConfig instead
 */
@Deprecated("Use ColorConfig instead", ReplaceWith("ColorConfig"))
data class VisualizerColorConfig(
    var arcPaintWidth: Float = 4f,
    var arcPaintColor: Int = 0xFFEC407A.toInt(),
    var circlePaintWidth: Float = 13f,
    var circlePaintColor: Int = 0xFFEC407A.toInt(),
    var linePaintColor: Int = 0xFFEC407A.toInt()
)