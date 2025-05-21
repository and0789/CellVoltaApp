package com.alfath.selvoltaapp.ui.screens.simulation.utils


import androidx.compose.ui.graphics.Color

class Utils {
}

fun isColorDark(color: Color): Boolean {
    val luminance = (0.299 * color.red + 0.587 * color.green + 0.114 * color.blue)
    return luminance < 0.5
}