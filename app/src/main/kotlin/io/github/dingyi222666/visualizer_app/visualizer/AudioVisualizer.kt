package io.github.dingyi222666.visualizer_app.visualizer

import android.media.audiofx.Visualizer
import io.github.dingyi222666.visualizer_app.visualizer.processors.FftBasicFilter
import io.github.dingyi222666.visualizer_app.utils.DoubleArrayPool
import io.github.dingyi222666.visualizer_app.visualizer.DataProcessor

/**
 * AudioVisualizer provides audio visualization functionality with a pipeline architecture
 * for processing audio data.
 */
class AudioVisualizer(private val audioSessionId: Int) {

    private var visualizer: Visualizer? = null
    private val pipeline = DataPipeline()
    private var isEnabled = false
    
    /**
     * Initialize the visualizer with default settings
     */
    init {
        setupVisualizer()
        // Add default FFT filter to the pipeline
        pipeline.addProcessor(FftBasicFilter())
    }
    
    /**
     * Set up the Android Visualizer
     */
    private fun setupVisualizer() {
        visualizer = Visualizer(audioSessionId).apply {
            scalingMode = Visualizer.SCALING_MODE_NORMALIZED
            captureSize = Visualizer.getCaptureSizeRange()[1]
        }
    }
    
    /**
     * Add a processor to the data pipeline
     */
    fun addProcessor(processor: DataProcessor) {
        pipeline.addProcessor(processor)
    }
    
    /**
     * Remove a processor from the data pipeline
     */
    fun removeProcessor(processor: DataProcessor) {
        pipeline.removeProcessor(processor)
    }
    
    /**
     * Set the entire pipeline
     */
    fun setPipeline(newPipeline: DataPipeline) {
        pipeline.clear()
        newPipeline.getProcessors().forEach { pipeline.addProcessor(it) }
    }
    
    /**
     * Use a factory-created default pipeline
     */
    fun useDefaultPipeline() {
        setPipeline(ProcessorFactory.createDefaultFftPipeline())
    }
    
    /**
     * Set the data capture listener to receive processed visualization data
     */
    fun setDataCaptureListener(listener: VisualizerDataListener) {
        val doubleArrayPool = DoubleArrayPool.instance
        
        visualizer?.setDataCaptureListener(object : Visualizer.OnDataCaptureListener {
            override fun onWaveFormDataCapture(visualizer: Visualizer, waveformData: ByteArray, samplingRate: Int) {
                // Not implemented in this version
            }

            override fun onFftDataCapture(visualizer: Visualizer, fftData: ByteArray, samplingRate: Int) {
                val processedData = pipeline.process(fftData)
                
                // 在传递给外部监听器之前先获取所有权
                if (processedData.isNotEmpty()) {
                    doubleArrayPool.acquire(processedData)
                }
                
                val canRecycle = listener.onFftDataCapture(processedData)
                
                // If the listener is done with the data, recycle it
                if (canRecycle) {
                    doubleArrayPool.recycle(processedData)
                }
            }
        }, Visualizer.getMaxCaptureRate() / 2, false, true)
    }
    
    /**
     * Start visualization
     */
    fun start() {
        if (!isEnabled) {
            visualizer?.enabled = true
            isEnabled = true
        }
    }
    
    /**
     * Stop visualization
     */
    fun stop() {
        if (isEnabled) {
            visualizer?.enabled = false
            isEnabled = false
        }
    }
    
    /**
     * Release resources
     */
    fun release() {
        stop()
        visualizer?.release()
        visualizer = null
    }
    
    /**
     * Data listener interface for visualization data
     */
    interface VisualizerDataListener {
        /**
         * Called when FFT data is captured and processed
         * 
         * @param fftData The processed FFT data
         * @return true if the listener has finished using the data and it can be recycled,
         *         false if the listener needs to keep the data (listener is responsible for recycling in this case)
         */
        fun onFftDataCapture(fftData: DoubleArray): Boolean
    }
}


/**
 * A pipeline that processes data through a series of processors
 */
class DataPipeline {
    private val processors = mutableListOf<DataProcessor>()
    private val doubleArrayPool = DoubleArrayPool.instance
    
    fun addProcessor(processor: DataProcessor) {
        processors.add(processor)
    }
    
    fun removeProcessor(processor: DataProcessor) {
        processors.remove(processor)
    }
    
    fun clear() {
        processors.clear()
    }
    
    fun getProcessors(): List<DataProcessor> {
        return processors.toList()
    }
    
    fun process(data: ByteArray): DoubleArray {
        if (processors.isEmpty()) {
            // Return empty array if no processors
            return doubleArrayPool.get(0)
        }
        
        // Process through the first processor using raw data
        var processedData = processors[0].processRawData(data)
        
        // Process through remaining processors using already processed DoubleArray
        for (i in 1 until processors.size) {
            // Store current processed data to recycle after processing
            val dataToRecycle = processedData
            
            // Process through next processor directly with DoubleArray
            processedData = processors[i].processData(processedData)
            
            // Recycle the previous data
            doubleArrayPool.recycle(dataToRecycle)
        }
        
        return processedData
    }
}