/*
 * ScreenTransitions.kt
 * TAAWIDATY System
 * 
 * Description: Custom screen transition animations
 * Created: November 2025
 * 
 * Features:
 * - Custom enter/exit animations between screens
 * - Shared element transitions
 * - Fade-through transitions
 * - Slide transitions with spring physics
 * - Scale transitions
 */

package com.pharmatech.morocco.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset

/**
 * Fade-through transition
 */
@OptIn(ExperimentalAnimationApi::class)
fun fadeThrough(durationMillis: Int = 300): ContentTransform {
    return fadeIn(
        animationSpec = tween(durationMillis / 2, delayMillis = durationMillis / 2)
    ) togetherWith fadeOut(
        animationSpec = tween(durationMillis / 2)
    )
}

/**
 * Slide transition with spring
 */
fun slideInFromRight(): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = { it },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + fadeIn(animationSpec = tween(300))
}

fun slideOutToLeft(): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { -it },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + fadeOut(animationSpec = tween(200))
}

fun slideInFromLeft(): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = { -it },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + fadeIn(animationSpec = tween(300))
}

fun slideOutToRight(): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { it },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + fadeOut(animationSpec = tween(200))
}

/**
 * Scale transition
 */
fun scaleIn(): EnterTransition {
    return scaleIn(
        initialScale = 0.9f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + fadeIn(animationSpec = tween(300))
}

fun scaleOut(): ExitTransition {
    return scaleOut(
        targetScale = 0.9f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + fadeOut(animationSpec = tween(200))
}

/**
 * Vertical slide transitions
 */
fun slideInFromBottom(): EnterTransition {
    return slideInVertically(
        initialOffsetY = { it },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + fadeIn(animationSpec = tween(300))
}

fun slideOutToBottom(): ExitTransition {
    return slideOutVertically(
        targetOffsetY = { it },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + fadeOut(animationSpec = tween(200))
}

fun slideInFromTop(): EnterTransition {
    return slideInVertically(
        initialOffsetY = { -it },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + fadeIn(animationSpec = tween(300))
}

fun slideOutToTop(): ExitTransition {
    return slideOutVertically(
        targetOffsetY = { -it },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + fadeOut(animationSpec = tween(200))
}

/**
 * Expand/Shrink transitions
 */
fun expandIn(): EnterTransition {
    return expandHorizontally(
        expandFrom = Alignment.Start,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + fadeIn()
}

fun shrinkOut(): ExitTransition {
    return shrinkHorizontally(
        shrinkTowards = Alignment.Start,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + fadeOut()
}

/**
 * Material fade-through transition
 */
fun materialFadeThrough(): ContentTransform {
    return (fadeIn(animationSpec = tween(210, delayMillis = 90)) +
            scaleIn(initialScale = 0.92f, animationSpec = tween(210, delayMillis = 90))) togetherWith
            (fadeOut(animationSpec = tween(90)))
}

/**
 * Material shared axis X transition
 */
fun materialSharedAxisX(forward: Boolean): ContentTransform {
    val enter = slideInHorizontally(
        initialOffsetX = { if (forward) it / 10 else -it / 10 },
        animationSpec = tween(300)
    ) + fadeIn(animationSpec = tween(210, delayMillis = 90))

    val exit = slideOutHorizontally(
        targetOffsetX = { if (forward) -it / 10 else it / 10 },
        animationSpec = tween(300)
    ) + fadeOut(animationSpec = tween(90))

    return enter togetherWith exit
}

/**
 * Material shared axis Y transition
 */
fun materialSharedAxisY(forward: Boolean): ContentTransform {
    val enter = slideInVertically(
        initialOffsetY = { if (forward) it / 10 else -it / 10 },
        animationSpec = tween(300)
    ) + fadeIn(animationSpec = tween(210, delayMillis = 90))

    val exit = slideOutVertically(
        targetOffsetY = { if (forward) -it / 10 else it / 10 },
        animationSpec = tween(300)
    ) + fadeOut(animationSpec = tween(90))

    return enter togetherWith exit
}

/**
 * Material shared axis Z transition (scale)
 */
fun materialSharedAxisZ(forward: Boolean): ContentTransform {
    val enter = fadeIn(animationSpec = tween(210, delayMillis = 90)) +
            scaleIn(
                initialScale = if (forward) 0.8f else 1.1f,
                animationSpec = tween(300)
            )

    val exit = fadeOut(animationSpec = tween(90)) +
            scaleOut(
                targetScale = if (forward) 1.1f else 0.8f,
                animationSpec = tween(300)
            )

    return enter togetherWith exit
}

/**
 * Elevator transition (scale + fade)
 */
fun elevatorTransition(ascending: Boolean = true): ContentTransform {
    val enter = fadeIn(animationSpec = tween(300, delayMillis = 100)) +
            slideInVertically(
                initialOffsetY = { if (ascending) it / 2 else -it / 2 },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) +
            scaleIn(
                initialScale = 0.9f,
                animationSpec = tween(300, delayMillis = 100)
            )

    val exit = fadeOut(animationSpec = tween(150)) +
            slideOutVertically(
                targetOffsetY = { if (ascending) -it / 2 else it / 2 },
                animationSpec = tween(300)
            ) +
            scaleOut(
                targetScale = 0.9f,
                animationSpec = tween(300)
            )

    return enter togetherWith exit
}

/**
 * Zoom transition
 */
fun zoomTransition(): ContentTransform {
    return (fadeIn(animationSpec = tween(150, delayMillis = 150)) +
            scaleIn(
                initialScale = 0.5f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )) togetherWith
            (fadeOut(animationSpec = tween(150)) +
                    scaleOut(
                        targetScale = 0.5f,
                        animationSpec = tween(150)
                    ))
}

/**
 * Rotation transition
 */
fun rotationTransition(): ContentTransform {
    return (fadeIn(animationSpec = tween(300)) +
            scaleIn(
                initialScale = 0.8f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )) togetherWith
            (fadeOut(animationSpec = tween(200)) +
                    scaleOut(
                        targetScale = 0.8f,
                        animationSpec = tween(200)
                    ))
}

/**
 * Crossfade transition
 */
fun crossfade(durationMillis: Int = 300): ContentTransform {
    return fadeIn(animationSpec = tween(durationMillis)) togetherWith
            fadeOut(animationSpec = tween(durationMillis))
}

/**
 * Bounce transition
 */
fun bounceTransition(): ContentTransform {
    return (fadeIn() +
            slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )) togetherWith
            (fadeOut() +
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(200)
                    ))
}

/**
 * Flip transition
 */
fun flipTransition(): ContentTransform {
    return (fadeIn(animationSpec = tween(300, delayMillis = 150)) +
            scaleIn(
                initialScale = 0.8f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )) togetherWith
            (fadeOut(animationSpec = tween(150)) +
                    scaleOut(
                        targetScale = 0.8f,
                        animationSpec = tween(150)
                    ))
}
