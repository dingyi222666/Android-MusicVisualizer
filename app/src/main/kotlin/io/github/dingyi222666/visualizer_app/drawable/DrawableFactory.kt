package io.github.dingyi222666.visualizer_app.drawable

import android.content.Context
import io.github.dingyi222666.visualizer_app.visualizer.VisualizerConfig
import io.github.dingyi222666.visualizer_app.visualizer.CircleConfig
import io.github.dingyi222666.visualizer_app.visualizer.LineConfig
import io.github.dingyi222666.visualizer_app.visualizer.ColorConfig

/**
 * Factory for creating visualization drawables
 */
object DrawableFactory {
    
    /**
     * Available visualization types
     */
    enum class VisualizationType {
        CIRCLE,
        LINE
    }
    
    /**
     * Create a drawable based on the specified type
     * 
     * @param context Android context
     * @param type Type of visualization to create
     * @param config Optional configuration (default config used if null)
     * @return A new drawable instance
     */
    fun createDrawable(
        context: Context, 
        type: VisualizationType, 
        config: VisualizerConfig? = null
    ): BaseDrawable {
        // Create the drawable
        val drawable = when (type) {
            VisualizationType.CIRCLE -> CircleDrawable(context)
            VisualizationType.LINE -> LineDrawable(context)
        }
        
        // Initialize with configuration if provided
        if (config != null) {
            drawable.setConfig(config)
        } else {
            drawable.setConfig(createDefaultConfig())
        }
        
        // Initialize the drawable
        drawable.init()
        
        return drawable
    }
    
    /**
     * Create a default visualization configuration
     */
    fun createDefaultConfig(): VisualizerConfig {
        return VisualizerConfig(
            maxFps = 60,
            showFps = false,
            circle = CircleConfig(
                lineCount = 120f,
                angle = 3f,
                radius = 300f,
                showPoints = true
            ),
            line = LineConfig(
                width = 40f,
                margin = 24f
            ),
            dataStartIndex = 8,
            colors = ColorConfig(
                primaryColor = 0xFFEC407A.toInt(),
                circleOutlineColor = 0xFFEC407A.toInt(),
                circleOutlineWidth = 4f,
                circleLineWidth = 13f
            )
        )
    }
} 