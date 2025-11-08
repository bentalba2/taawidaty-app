package com.pharmatech.morocco.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// TAAWIDATY Shapes - Modern, smooth corners for professional look
val TaawidatyShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

// Legacy compatibility - deprecated
@Deprecated("Use TaawidatyShapes instead", ReplaceWith("TaawidatyShapes"))
val ShifaaShapes = TaawidatyShapes
