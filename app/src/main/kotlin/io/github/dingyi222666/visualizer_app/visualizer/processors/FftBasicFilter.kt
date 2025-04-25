package io.github.dingyi222666.visualizer_app.visualizer.processors

import io.github.dingyi222666.visualizer_app.visualizer.DataProcessor
import io.github.dingyi222666.visualizer_app.utils.DoubleArrayPool
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.log10

/**
 * Basic FFT filter that converts FFT data to magnitude values
 */
class FftBasicFilter(
    private val dbValue: Double = 75.0,
    private val minDbValue: Double = 25.0,
    private val doubleArrayPool: DoubleArrayPool = DoubleArrayPool.instance
) : DataProcessor {
    
    override fun processRawData(data: ByteArray): DoubleArray {
        val result = doubleArrayPool.get(data.size / 2)
        
        try {
            // Process FFT data to get magnitude values
            for (i in data.indices step 2) {
                var base = hypot(data[i].toDouble(), data[i + 1].toDouble())
                base = abs(base)
                
                if (base == 0.0) {
                    result[i / 2] = 1.0
                    continue
                }
                
                // Calculate decibel value
                base = abs(dbValue * log10(base))
                
                // Filter out noise below threshold
                if (base < minDbValue) {
                    base = 0.0
                }
                
                result[i / 2] = base
            }
            
            return result
        } catch (e: Exception) {
            // 如果处理过程中出现任何异常，确保回收数组以防内存泄漏
            doubleArrayPool.recycle(result)
            throw e
        }
    }
    
    override fun processData(data: DoubleArray): DoubleArray {
        val result = doubleArrayPool.get(data.size)
        
        try {
            // Process already converted FFT data
            for (i in data.indices) {
                var base = data[i]
                
                if (base == 0.0) {
                    result[i] = 1.0
                    continue
                }
                
                // Apply db scale if needed
                if (base > 1.0) {
                    base = abs(dbValue * log10(base))
                }
                
                // Filter out noise below threshold
                if (base < minDbValue) {
                    base = 0.0
                }
                
                result[i] = base
            }
            
            return result
        } catch (e: Exception) {
            doubleArrayPool.recycle(result)
            throw e
        }
    }
} 