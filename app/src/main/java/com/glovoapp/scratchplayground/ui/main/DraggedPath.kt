package com.glovoapp.scratchplayground.ui.main

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

data class DraggedPath(
    val path: Path,
    val color: Color = Color.White,
    val width: Float = 50f
)