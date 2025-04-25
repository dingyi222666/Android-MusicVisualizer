package io.github.dingyi222666.visualizer_app.visualizer.processors

import io.github.dingyi222666.visualizer_app.visualizer.DataProcessor
import io.github.dingyi222666.visualizer_app.visualizer.VisualizerData
import io.github.dingyi222666.visualizer_app.utils.DoubleArrayPool

/**
 * A processor that reduces noise in FFT data
 */
class NoiseReductionProcessor(
    private val minDbValue: Double = 25.0,
    private val visualizerData: VisualizerData = VisualizerData(),
    private val doubleArrayPool: DoubleArrayPool = DoubleArrayPool.instance
) : DataProcessor {
    
    override fun processRawData(data: ByteArray): DoubleArray {
        // Convert ByteArray to DoubleArray assuming it contains FFT magnitudes
        val inputData = ByteArray(data.size).apply {
            System.arraycopy(data, 0, this, 0, data.size)
        }
        
        // Create an array for processing
        val processedData = doubleArrayPool.get(inputData.size / 2)
        
        try {
            // Convert to double values first
            for (i in inputData.indices step 2) {
                processedData[i / 2] = inputData[i].toDouble()
            }
            
            // Process the converted data
            return processData(processedData)
        } catch (e: Exception) {
            // 出现异常时确保回收数组
            doubleArrayPool.recycle(processedData)
            throw e
        }
    }
    
    override fun processData(data: DoubleArray): DoubleArray {
        val outputData = doubleArrayPool.get(data.size)
        
        try {
            // Apply noise reduction
            var index = 0
            
            for (i in data.indices step visualizerData.skipArrayIndex) {
                val value = data[i]
                
                // Skip values below the threshold
                if (value < minDbValue) {
                    continue
                }
                
                if (index < outputData.size) {
                    outputData[index++] = value
                }
            }
            
            // Fill the remaining positions with zeros
            while (index < outputData.size) {
                outputData[index++] = 0.0
            }
            
            return outputData
        } catch (e: Exception) {
            // 出现异常时确保回收数组
            doubleArrayPool.recycle(outputData)
            throw e
        }
    }
} 