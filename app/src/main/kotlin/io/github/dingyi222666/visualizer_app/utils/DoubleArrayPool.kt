package io.github.dingyi222666.visualizer_app.utils

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger

/**
 * Thread-safe object pool for reusing DoubleArray instances to reduce memory allocations
 */
class DoubleArrayPool {
    // Thread-local storage for array pools to prevent contention between threads
    private val threadLocalPools = ThreadLocal.withInitial {
        ConcurrentHashMap<Int, ConcurrentLinkedQueue<DoubleArray>>()
    }

    // Track array usage to prevent recycling arrays still in use
    private val arrayUsageCount = ConcurrentHashMap<DoubleArray, AtomicInteger>()

    /**
     * Get or create a DoubleArray of the specified size
     *
     * @param size The size of the DoubleArray
     * @return A DoubleArray instance of the specified size
     */
    fun get(size: Int): DoubleArray {
        val pools = threadLocalPools.get()
        val queue = pools.getOrPut(size) { ConcurrentLinkedQueue() }

        // Try to get from the current thread's pool first
        var array = queue.poll()

        // If not found in current thread pool, create new one
        if (array == null) {
            array = DoubleArray(size)
        }

        // Track usage
        arrayUsageCount.getOrPut(array) { AtomicInteger(0) }.incrementAndGet()

        return array
    }

    /**
     * Return a DoubleArray to the pool for reuse
     *
     * @param array The DoubleArray to return to the pool
     * @return True if successfully recycled, false if the array is still in use elsewhere
     */
    fun recycle(array: DoubleArray): Boolean {
        val counter = arrayUsageCount[array] ?: return false

        // Decrement usage count and recycle only if no more users
        if (counter.decrementAndGet() <= 0) {
            // Remove from tracking
            arrayUsageCount.remove(array)

            // Clear the array before recycling
            array.fill(0.0)

            // Add to the current thread's pool
            val pools = threadLocalPools.get()
            val size = array.size
            val queue = pools.getOrPut(size) { ConcurrentLinkedQueue() }
            queue.offer(array)

            return true
        }

        return false
    }

    /**
     * Acquire ownership of an array (increment usage count)
     * Use this when passing arrays between threads
     *
     * @param array The array to acquire
     * @return True if successfully acquired, false otherwise
     */
    fun acquire(array: DoubleArray): Boolean {
        val counter = arrayUsageCount[array] ?: return false
        counter.incrementAndGet()
        return true
    }

    /**
     * Clear all pools to free memory
     */
    fun clear() {
        threadLocalPools.remove()
        // We don't clear arrayUsageCount as some arrays might still be in use
    }

    companion object {
        val instance by lazy { DoubleArrayPool() }


    }
} 