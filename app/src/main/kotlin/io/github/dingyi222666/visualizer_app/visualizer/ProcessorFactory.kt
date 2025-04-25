package io.github.dingyi222666.visualizer_app.visualizer

import io.github.dingyi222666.visualizer_app.visualizer.processors.FftBasicFilter
import io.github.dingyi222666.visualizer_app.visualizer.processors.NoiseReductionProcessor
import io.github.dingyi222666.visualizer_app.utils.DoubleArrayPool
import io.github.dingyi222666.visualizer_app.visualizer.processors.SavitzkyGolayProcessor

/**
 * Factory for creating and configuring data processors
 */
object ProcessorFactory {
    private val doubleArrayPool = DoubleArrayPool.instance
    private val fftBasicFilter = FftBasicFilter()
    
    /**
     * Creates a gain processor that amplifies the data by the given factor
     */
    fun createGainProcessor(gainFactor: Double): DataProcessor {
        return object : DataProcessor {
            override fun processRawData(data: ByteArray): DoubleArray {
                val result = fftBasicFilter.processRawData(data)
                return applyGain(result, gainFactor)
            }
            
            override fun processData(data: DoubleArray): DoubleArray {
                val result = doubleArrayPool.get(data.size)
                
                try {
                    for (i in data.indices) {
                        result[i] = data[i] * gainFactor
                    }
                    return result
                } catch (e: Exception) {
                    doubleArrayPool.recycle(result)
                    throw e
                }
            }
            
            private fun applyGain(data: DoubleArray, gain: Double): DoubleArray {
                for (i in data.indices) {
                    data[i] *= gain
                }
                return data
            }
        }
    }
    
    /**
     * Creates a threshold processor that zeros values below the threshold
     */
    fun createThresholdProcessor(threshold: Double): DataProcessor {
        return object : DataProcessor {
            override fun processRawData(data: ByteArray): DoubleArray {
                val result = fftBasicFilter.processRawData(data)
                return applyThreshold(result, threshold)
            }
            
            override fun processData(data: DoubleArray): DoubleArray {
                val result = doubleArrayPool.get(data.size)
                
                try {
                    for (i in data.indices) {
                        result[i] = if (data[i] < threshold) 0.0 else data[i]
                    }
                    return result
                } catch (e: Exception) {
                    doubleArrayPool.recycle(result)
                    throw e
                }
            }
            
            private fun applyThreshold(data: DoubleArray, threshold: Double): DoubleArray {
                for (i in data.indices) {
                    if (data[i] < threshold) {
                        data[i] = 0.0
                    }
                }
                return data
            }
        }
    }
    
    /**
     * Creates a default FFT processing pipeline
     */
    fun createDefaultFftPipeline(): DataPipeline {
        return DataPipeline().apply {
            addProcessor(FftBasicFilter())
            addProcessor(NoiseReductionProcessor())
            addProcessor(SavitzkyGolayProcessor())
        }
    }
} 