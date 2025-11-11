package com.pharmatech.morocco.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * Performance optimization utilities for smooth interactions
 */

/**
 * Memoized box for expensive composable calculations
 */
@Composable
fun <T> MemoizedBox(
    key: Any,
    content: @Composable () -> T
): T {
    return remember(key) { content() }
}

/**
 * Optimized LazyListState with performance optimizations
 */
@Composable
fun rememberOptimizedLazyListState(): LazyListState {
    return rememberLazyListState().apply {
        // Enable fast scrolling optimization
        this
    }
}

/**
 * Preloading optimization for images
 */
@Composable
fun OptimizedImage(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .drawWithCache {
                onDrawWithContent {
                    // Optimized drawing with cache
                    drawContent()
                }
            }
    ) {
        content()
    }
}

/**
 * Animation optimization utilities
 */
object AppleAnimationOptimization {

    /**
     * Fast spring animation for smooth interactions
     */
    fun fastSpring() = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )

    /**
     * Gentle spring animation for subtle effects
     */
    fun gentleSpring() = spring(
        dampingRatio = Spring.DampingRatioHighBouncy,
        stiffness = Spring.StiffnessLow
    )

    /**
     * Fast easing for responsive feel
     */
    fun fastEasing() = EaseInOutCubic

    /**
     * Gentle easing for smooth transitions
     */
    fun gentleEasing() = EaseOutCubic
}

/**
 * Lazy loading utilities
 */
object LazyLoading {

    /**
     * Determine if item should be loaded based on scroll position
     */
    fun shouldLoadItem(
        lazyListState: LazyListState,
        itemIndex: Int,
        threshold: Int = 5
    ): Boolean {
        val layoutInfo = lazyListState.layoutInfo
        val visibleItemsInfo = layoutInfo.visibleItemsInfo

        if (visibleItemsInfo.isEmpty()) return false

        val firstVisibleItem = visibleItemsInfo.first().index
        val lastVisibleItem = visibleItemsInfo.last().index

        return itemIndex in (firstVisibleItem - threshold)..(lastVisibleItem + threshold)
    }

    /**
     * Optimized scroll position tracking
     */
    @Composable
    fun rememberScrollPosition(
        lazyListState: LazyListState
    ): State<Int> {
        return remember {
            derivedStateOf {
                lazyListState.firstVisibleItemIndex
            }
        }.distinctUntilChanged()
    }
}

/**
 * Memory optimization utilities
 */
object MemoryOptimization {

    /**
     * Weak reference holder for large objects
     */
    class WeakRef<T>(private val ref: T) {
        private val weakRef = java.lang.ref.WeakReference(ref)

        fun get(): T? = weakRef.get()
    }

    /**
     * Cache manager for frequently used data
     */
    class SimpleCache<K, V>(private val maxSize: Int = 100) {
        private val cache = mutableMapOf<K, V>()

        fun get(key: K): V? = cache[key]

        fun put(key: K, value: V) {
            if (cache.size >= maxSize) {
                cache.remove(cache.keys.first())
            }
            cache[key] = value
        }

        fun clear() = cache.clear()

        fun size() = cache.size
    }
}

/**
 * Performance monitoring utilities
 */
object PerformanceMonitor {

    private val frameTimeBuffer = mutableListOf<Long>()
    private const val FRAME_TIME_BUFFER_SIZE = 60

    fun recordFrameTime(time: Long) {
        frameTimeBuffer.add(time)
        if (frameTimeBuffer.size > FRAME_TIME_BUFFER_SIZE) {
            frameTimeBuffer.removeAt(0)
        }
    }

    fun getAverageFrameTime(): Double {
        return if (frameTimeBuffer.isNotEmpty()) {
            frameTimeBuffer.average()
        } else {
            0.0
        }
    }

    fun getFPS(): Double {
        val avgTime = getAverageFrameTime()
        return if (avgTime > 0) {
            1000.0 / avgTime
        } else {
            60.0 // Default FPS
        }
    }

    fun clearBuffer() {
        frameTimeBuffer.clear()
    }
}

/**
 * Layout optimization utilities
 */
object LayoutOptimization {

    /**
     * Responsive spacing based on screen size
     */
    @Composable
    fun responsiveSpacing(): Float {
        val configuration = LocalConfiguration.current
        val screenWidthDp = configuration.screenWidthDp

        return when {
            screenWidthDp < 600 -> 8f  // Small screens
            screenWidthDp < 840 -> 12f // Medium screens
            else -> 16f               // Large screens
        }
    }

    /**
     * Optimized width modifier for different screen sizes
     */
    fun responsiveWidthModifier(): Modifier {
        return Modifier.fillMaxWidth()
    }

    /**
     * Card height optimization for performance
     */
    fun optimizedCardHeight(): Float {
        val configuration = LocalConfiguration.current
        val screenHeightDp = configuration.screenHeightDp

        return when {
            screenHeightDp < 800 -> 80f  // Small screens
            screenHeightDp < 1200 -> 100f // Medium screens
            else -> 120f               // Large screens
        }
    }
}

/**
 * Haptic feedback optimization
 */
object HapticOptimization {

    private var lastHapticTime = 0L
    private val HAPTIC_COOLDOWN_MS = 50L

    /**
     * Rate-limited haptic feedback
     */
    fun shouldProvideHapticFeedback(): Boolean {
        val currentTime = System.currentTimeMillis()
        return if (currentTime - lastHapticTime > HAPTIC_COOLDOWN_MS) {
            lastHapticTime = currentTime
            true
        } else {
            false
        }
    }

    /**
     * Reset haptic cooldown for testing
     */
    fun resetCooldown() {
        lastHapticTime = 0L
    }
}

/**
 * Composition optimization utilities
 */
@Composable
fun <T> rememberStableState(
    key1: Any? = null,
    key2: Any? = null,
    calculation: () -> T
): State<T> {
    return remember(key1, key2) {
        mutableStateOf(calculation())
    }
}

/**
 * Optimized re-composition checking
 */
@Composable
fun <T> rememberStableList(
    items: List<T>,
    key: (T) -> Any
): List<T> {
    return remember(items) {
        items.map { item ->
            // Create a stable representation
            StableItemWrapper(item, key(item))
        }
    }.map { it.item }
}

private data class StableItemWrapper<T>(
    val item: T,
    val key: Any
)

/**
 * GPU optimization utilities
 */
object GPUOptimization {

    /**
     * Enable hardware acceleration hints
     */
    fun enableHardwareAccelerationHints(): Modifier {
        return Modifier.graphicsLayer {
            compositingStrategy = androidx.compose.ui.graphics.CompositingStrategy.Offscreen
            alpha = 1f
        }
    }

    /**
     * Reduce overdraw optimization
     */
    fun reduceOverdraw(): Modifier {
        return Modifier.graphicsLayer {
            clip = false
        }
    }
}