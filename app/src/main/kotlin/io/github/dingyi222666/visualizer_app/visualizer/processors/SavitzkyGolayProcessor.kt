package io.github.dingyi222666.visualizer_app.visualizer.processors

import io.github.dingyi222666.visualizer_app.utils.DoubleArrayPool
import io.github.dingyi222666.visualizer_app.visualizer.DataProcessor

/**
 * Savitzky-Golay filter processor for smoothing audio visualization data.
 * Provides three window size options: 5, 7, and 9 points.
 * Optimized to handle circular FFT data by wrapping tail data to beginning for proper smoothing.
 */
class SavitzkyGolayProcessor(
    private val doubleArrayPool: DoubleArrayPool = DoubleArrayPool.instance
) : DataProcessor {
    
    enum class WindowSize {
        WINDOW_5,  // 5-point window
        WINDOW_7,  // 7-point window
        WINDOW_9   // 9-point window
    }
    
    private var windowSize: WindowSize = WindowSize.WINDOW_9
    
    /**
     * Set the window size for the filter
     * @param size The window size to use
     */
    fun setWindowSize(size: WindowSize) {
        windowSize = size
    }
    
    override fun processRawData(data: ByteArray): DoubleArray {
        val inputData = doubleArrayPool.get(data.size / 2)
        
        try {
            // Convert byte data to double
            for (i in data.indices step 2) {
                inputData[i / 2] = data[i].toDouble()
            }
            
            // Apply smoothing
            return processData(inputData)
        } catch (e: Exception) {
            doubleArrayPool.recycle(inputData)
            throw e
        }
    }
    
    /**
     * 反转边缘并替换：
     * 1. 取出开头1/5部分并反转
     * 2. 取出中间部分
     * 3. 用反转后的开头替换尾部
     * 4. 返回中间+新尾部组合的数据
     */
    private fun reverseAndReplaceEdges(data: DoubleArray): DoubleArray {
        val length = data.size
        if (length < 2) {
            return data // 如果数组长度小于2，无法进行替换，直接返回原数据
        }
        
        val edgeLength = length / 16 // 边缘长度为1/5
        if (edgeLength == 0) {
            return data // 如果1/5长度为0，也无法替换，直接返回原数据
        }
        
        // 创建结果数组（大小是中间+尾部）
        val resultSize = length - edgeLength
        val result =  DoubleArray(resultSize)
        
        // 复制中间部分到结果数组
        for (i in edgeLength until length - edgeLength) {
            result[i - edgeLength] = data[i]
        }
        
        // 反转开头部分并作为新尾部
        for (i in 0 until edgeLength) {
            result[length - edgeLength - edgeLength + i] = data[edgeLength - 1 - i]
        }
        
        return result
    }
    
    /**
     * 处理FFT数据：先进行边缘反转替换，然后应用滤波器
     */
    override fun processData(data: DoubleArray): DoubleArray {
        try {
            // 反转边缘并替换
            val processedData = reverseAndReplaceEdges(data)
            
            // 应用滤波器
            val result = when (windowSize) {
                WindowSize.WINDOW_5 -> processWindow5(processedData)
                WindowSize.WINDOW_7 -> processWindow7(processedData)
                WindowSize.WINDOW_9 -> processWindow9(processedData)
            }
            
            // 回收临时数组
            doubleArrayPool.recycle(processedData)
            
            return result
        } catch (e: Exception) {
            throw e
        }
    }
    
    /**
     * Apply 5-point Savitzky-Golay filter with circular boundary handling
     * Y_i = (1/35)(-3y_{i-2} + 12y_{i-1} + 17y_i + 12y_{i+1} - 3y_{i+2})
     */
    private fun processWindow5(audioData: DoubleArray): DoubleArray {
        val result = doubleArrayPool.get(audioData.size)
        val dataLength = audioData.size
        
        try {
            // Apply filter to all points with circular wrapping for edge cases
            for (i in 0 until dataLength) {
                val im2 = (i - 2 + dataLength) % dataLength
                val im1 = (i - 1 + dataLength) % dataLength
                val ip1 = (i + 1) % dataLength
                val ip2 = (i + 2) % dataLength
                
                result[i] = (1.0 / 35.0) * (
                        -3 * audioData[im2] +
                        12 * audioData[im1] +
                        17 * audioData[i] +
                        12 * audioData[ip1] -
                        3 * audioData[ip2]
                )
            }
            
            return result
        } catch (e: Exception) {
            doubleArrayPool.recycle(result)
            throw e
        }
    }
    
    /**
     * Apply 7-point Savitzky-Golay filter with circular boundary handling
     * Y_i = (1/21)(-2y_{i-3} + 3y_{i-2} + 6y_{i-1} + 7y_i + 6y_{i+1} + 3y_{i+2} - 2y_{i+3})
     */
    private fun processWindow7(audioData: DoubleArray): DoubleArray {
        val result = doubleArrayPool.get(audioData.size)
        val dataLength = audioData.size
        
        try {
            // Apply filter to all points with circular wrapping for edge cases
            for (i in 0 until dataLength) {
                val im3 = (i - 3 + dataLength) % dataLength
                val im2 = (i - 2 + dataLength) % dataLength
                val im1 = (i - 1 + dataLength) % dataLength
                val ip1 = (i + 1) % dataLength
                val ip2 = (i + 2) % dataLength
                val ip3 = (i + 3) % dataLength
                
                result[i] = (1.0 / 21.0) * (
                        -2 * audioData[im3] +
                        3 * audioData[im2] +
                        6 * audioData[im1] +
                        7 * audioData[i] +
                        6 * audioData[ip1] +
                        3 * audioData[ip2] -
                        2 * audioData[ip3]
                )
            }
            
            return result
        } catch (e: Exception) {
            doubleArrayPool.recycle(result)
            throw e
        }
    }
    
    /**
     * Apply 9-point Savitzky-Golay filter with circular boundary handling
     * Y_i = (1/231)(-21y_{i-4} + 14y_{i-3} + 39y_{i-2} + 54y_{i-1} + 59y_i + 54y_{i+1} + 39y_{i+2} + 14y_{i+3} - 21y_{i+4})
     */
    private fun processWindow9(audioData: DoubleArray): DoubleArray {
        val result = doubleArrayPool.get(audioData.size)
        val dataLength = audioData.size
        
        try {
            // Apply filter to all points with circular wrapping for edge cases
            for (i in 0 until dataLength) {
                val im4 = (i - 4 + dataLength) % dataLength
                val im3 = (i - 3 + dataLength) % dataLength
                val im2 = (i - 2 + dataLength) % dataLength
                val im1 = (i - 1 + dataLength) % dataLength
                val ip1 = (i + 1) % dataLength
                val ip2 = (i + 2) % dataLength
                val ip3 = (i + 3) % dataLength
                val ip4 = (i + 4) % dataLength
                
                result[i] = (1.0 / 231.0) * (
                        -21 * audioData[im4] +
                        14 * audioData[im3] +
                        39 * audioData[im2] +
                        54 * audioData[im1] +
                        59 * audioData[i] +
                        54 * audioData[ip1] +
                        39 * audioData[ip2] +
                        14 * audioData[ip3] -
                        21 * audioData[ip4]
                )
            }
            
            return result
        } catch (e: Exception) {
            doubleArrayPool.recycle(result)
            throw e
        }
    }
} 