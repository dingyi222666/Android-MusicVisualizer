package io.github.dingyi222666.visualizer_app.visualizer.processors

import io.github.dingyi222666.visualizer_app.utils.DoubleArrayPool
import io.github.dingyi222666.visualizer_app.visualizer.DataProcessor
import kotlin.math.hypot
import kotlin.math.log10
import kotlin.math.abs // Keep abs for processData, though not strictly needed for hypot

/**
 * Basic FFT filter that converts FFT data to magnitude values.
 * processRawData is optimized to filter out noise based on a relative threshold
 * before passing magnitudes to processData for potential dB scaling.
 */
class FftBasicFilter(
    private val dbValue: Double = 75.0,
    private val minDbValue: Double = 25.0,
    private val doubleArrayPool: DoubleArrayPool = DoubleArrayPool.instance
) : DataProcessor {

    companion object {
        /**
         * Noise threshold percentage. Magnitudes below this percentage of the
         * maximum magnitude in the block will be considered noise and zeroed out.
         * For example, 0.05 means 5%.
         */
        private const val NOISE_THRESHOLD_PERCENTAGE = 0.05
    }

    override fun processRawData(data: ByteArray): DoubleArray {
        val numMagnitudes = data.size / 2
        // Handle empty input: return an empty array from the pool.
        if (numMagnitudes == 0) {
            return doubleArrayPool.get(0)
        }

        // Get an array from the pool to store magnitudes. This array will be returned.
        val magnitudes = doubleArrayPool.get(numMagnitudes)
        try {
            // Step 1: Calculate all raw magnitudes from the input byte array.
            // data[i*2] is real part, data[i*2+1] is imaginary part.
            for (i in 0 until numMagnitudes) {
                magnitudes[i] = hypot(data[i * 2].toDouble(), data[i * 2 + 1].toDouble())
                // No need for abs(hypot(...)) as hypot always returns a non-negative value.
            }

            // Step 2: Find the maximum magnitude in the current block.
            // If all magnitudes are 0.0 (e.g., silence), maxMagnitude will be 0.0.
            // If the array were empty (handled above), maxOrNull would be null.
            val maxMagnitude = magnitudes.maxOrNull() ?: 0.0

            // Step 3: Calculate the actual noise threshold.
            // If maxMagnitude is 0.0, threshold will also be 0.0, so 0.0 values won't be "less than" it.
            val actualThreshold =
                if (maxMagnitude > 0.0) maxMagnitude * NOISE_THRESHOLD_PERCENTAGE else 0.0

            // Step 4: Apply the noise threshold and prepare for processData.
            // Magnitudes below the threshold are set to 0.0.
            // Then, any magnitude that is 0.0 (either originally or after thresholding)
            // is set to 1.0. This is a common practice to avoid log10(0) issues
            // in subsequent processing (like dB scaling) and provides a baseline.
            for (i in 0 until numMagnitudes) {
                if (magnitudes[i] < actualThreshold) {
                    magnitudes[i] = 0.0
                }

                if (magnitudes[i] > 0) {
                    magnitudes[i] = abs(dbValue * log10(magnitudes[i]))
                }
            }

            return magnitudes  // Return the processed magnitudes array.
        } catch (e: Exception) {
            // If any error occurs, recycle the array obtained from the pool to prevent leaks.
            doubleArrayPool.recycle(magnitudes)
            throw e // Re-throw the exception.
        }
    }

    override fun processData(data: DoubleArray): DoubleArray {
        return data
        /* // Get an array from the pool for the results of this processing stage.
         val result = doubleArrayPool.get(data.size)

         try {
             // Process the magnitudes received from processRawData.
             // These magnitudes have already been noise-filtered, and 0.0 values mapped to 1.0.
             for (i in data.indices) {
                 var base = data[i] // base is a magnitude, or 1.0 if it was noise/zero.

                 // The original logic for 0.0 check is technically redundant if processRawData
                 // always maps 0.0 to 1.0, but kept for safety/clarity.
                 if (base == 0.0) { // Should ideally not be hit if processRawData works as intended.
                     result[i] = 1.0
                     continue
                 }

                 // Apply dB scaling.
                 // Note: This scaling is only applied if base > 1.0.
                 // If magnitudes are typically small (0.0 to 1.0), this log scaling might not apply often.
                 // log10(1.0) = 0. So if base is 1.0, it remains 1.0 before minDbValue check (abs(75*0)=0, then if 0<1, it becomes 1.0 again).
                 // Wait, if base is 1.0, dbValue * log10(base) = 0. Then it's not < minDbValue unless minDbValue is negative.
                 // Let's trace base = 1.0 from processRawData:
                 // base = 1.0.
                 // if (base > 1.0) is false. So base remains 1.0.
                 // if (base < minDbValue) i.e. (1.0 < 25.0) is true. base becomes 0.0.
                 // result[i] = 0.0.
                 // This means previously zero/noise values (which became 1.0) become 0.0 finally. This is good.

                 if (base > 1.0) { // Only apply dB scaling if magnitude is greater than 1.0
                     base = abs(dbValue * log10(base))
                 }
                 // else if base is (0, 1.0], it remains as is for the next check.
                 // For example, if base is 0.5, it remains 0.5.
                 // If base is 1.0 (from noise), it remains 1.0.

                 // Filter out values below minDbValue.
                 // This acts as a final floor after any potential dB scaling.
                 if (base < minDbValue) {
                     base = 0.0
                 }

                 result[i] = base
             }

             return result // Return the final processed array.
         } catch (e: Exception) {
             // If any error, recycle the pooled array.
             doubleArrayPool.recycle(result)
             throw e // Re-throw.
         }*/
    }
}
