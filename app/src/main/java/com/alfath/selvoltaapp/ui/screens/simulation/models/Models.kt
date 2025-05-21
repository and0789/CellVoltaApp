package com.alfath.selvoltaapp.ui.screens.simulation.models

import androidx.compose.ui.graphics.Color

class Models {
}

data class Metal(
    val symbol: String,
    val ion: String,
    val potential: Float,
    val color: Color
)

data class Solution(
    val name: String,
    val metalSymbol: String
)

data class Bubble(
    val x: Float = (Math.random() - 0.5).toFloat() * 2,
    var progress: Float = 0f,
    val size: Float = 15f + (Math.random() * 10).toFloat()
)