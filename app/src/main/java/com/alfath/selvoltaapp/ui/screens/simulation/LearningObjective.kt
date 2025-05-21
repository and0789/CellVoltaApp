package com.alfath.selvoltaapp.ui.screens.simulation

import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alfath.selvoltaapp.R


@Composable
fun LearningObjective(
    navController: NavController,
    @DrawableRes bgRes: Int = R.drawable.cover_learning_objective
) {
    // Context for back navigation
    val context = LocalContext.current
    val activity = context as? Activity

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenW = maxWidth
        val screenH = maxHeight
        val density = LocalDensity.current

        // Dynamic sizes
        val btnSize: Dp = screenW * 0.15f
        val btnPadding: Dp = screenW * 0.005f
        val colTopPadding: Dp = screenH * 0.15f
        val titleFontSize = with(density) { (screenW * 0.06f).toSp() }
        val spacerHeight: Dp = screenH * 0.02f

        // Background image
        Image(
            painter = painterResource(id = bgRes),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )

        // Back button top-left
        IconButton(
            onClick = { navController.navigateUp() },
            modifier = Modifier
                .size(btnSize)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(btnSize * 0.5f)
            )
        }


    }
}

// Previews in landscape orientations
@Preview(
    name = "Phone 16:9 L",
    showBackground = true,
    device = "spec:width=640dp,height=360dp,dpi=320"
)
@Preview(
    name = "Phone 18:9 L",
    showBackground = true,
    device = "spec:width=740dp,height=360dp,dpi=320"
)
@Preview(
    name = "Phone Tall L",
    showBackground = true,
    device = "spec:width=915dp,height=411dp,dpi=420"
)
@Preview(
    name = "Tablet 10\" L",
    showBackground = true,
    device = "spec:width=1280dp,height=800dp,dpi=240"
)
@Composable
fun LearningObjectivePreviews() {
    val navController = rememberNavController()
    LearningObjective(navController)
}
