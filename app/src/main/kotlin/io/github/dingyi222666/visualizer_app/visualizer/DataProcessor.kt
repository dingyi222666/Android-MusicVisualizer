package io.github.dingyi222666.visualizer_app.visualizer

/**
 * Interface for audio data processing in the visualizer pipeline
 */
interface DataProcessor {
    /**
     * Process raw audio data (ByteArray)
     * 
     * @param data Input data in byte array form
     * @return Processed data as double array
     */
    fun processRawData(data: ByteArray): DoubleArray
    
    /**
     * Process already converted audio data (DoubleArray)
     * 
     * @param data Input data in double array form
     * @return Processed data as double array
     */
    fun processData(data: DoubleArray): DoubleArray
    
    /**
     * Backward compatibility method
     * Delegates to processRawData by default
     */
    fun process(data: ByteArray): DoubleArray = processRawData(data)
} 