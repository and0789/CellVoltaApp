package com.alfath.selvoltaapp.ui.screens.cover

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alfath.selvoltaapp.R

@SuppressLint("ContextCastToActivity")
@Composable
fun CoverPage(
    navController: NavController,
    @DrawableRes bgRes: Int = R.drawable.cover_background
) {
    // Safe cast LocalContext to Activity to avoid preview crash
    val context = LocalContext.current
    val activity = context as? Activity
    var showExitDialog by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        // 1. Background full‐screen
        Image(
            painter = painterResource(id = bgRes),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Hitung ukuran relatif
        val screenW = maxWidth
        val screenH = maxHeight
        val btnW = (screenW * 0.18f).coerceAtLeast(100.dp)
        val btnH = (screenH * 0.10f).coerceAtLeast(40.dp)
        val gap = 8.dp
        val offsetX = screenW * 0.025f
        val offsetY = (btnH + gap) + (screenH * 0.16f)


        // Row untuk tombol MULAI & PROFILE PENULIS
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = offsetX, y = -(offsetY))
                .wrapContentWidth(),
            horizontalArrangement = Arrangement.spacedBy(gap)
        ) {
            GlowingButton(
                onClick = { navController.navigate("menu") },
                width = btnW,
                height = btnH,
                normalColor = Color(0xFF26C6DA),
                pressedColor = Color(0xFF00BCD4)
            ) {
                Text(
                    text = "MULAI",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            GlowingButton(
                onClick = { navController.navigate("profil") },
                width = btnW * 1.5f,
                height = btnH,
                normalColor = Color(0xFF26C6DA),
                pressedColor = Color(0xFF00BCD4)
            ) {
                Text(
                    text = "PROFILE PENULIS",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Tombol EXIT di pojok kanan bawah
        GlowingButton(
            onClick = { showExitDialog = true },
            width = btnW,
            height = btnH,
            normalColor = Color(0xFFE53935),
            pressedColor = Color(0xFFD32F2F),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(10.dp)
        ) {
            Text(
                text = "EXIT",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    // Dialog konfirmasi keluar
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Konfirmasi Keluar") },
            text = { Text("Apakah Anda yakin ingin keluar?") },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    activity?.finishAffinity()  // hanya panggil jika Activity tersedia
                }) {
                    Text("Ya")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // Tangani tombol back fisik
    BackHandler(enabled = true) {
        showExitDialog = true
    }
}

// Helper Composable: Tombol dengan efek "menyala" saat ditekan
@Composable
fun GlowingButton(
    onClick: () -> Unit,
    width: Dp,
    height: Dp,
    normalColor: Color,
    pressedColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val containerColor by animateColorAsState(
        targetValue = if (isPressed) pressedColor else normalColor
    )

    Button(
        onClick = onClick,
        modifier = modifier.size(width, height),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        interactionSource = interactionSource,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        content()
    }
}

// Phone 16:9 Landscape (360×640 → 640×360)
@Preview(
    name = "Phone 16:9 L",
    showBackground = true,
    device = "spec:width=640dp,height=360dp,dpi=320"
)
// Phone 18:9 Landscape (360×740 → 740×360)
@Preview(
    name = "Phone 18:9 L",
    showBackground = true,
    device = "spec:width=740dp,height=360dp,dpi=320"
)
// Phone Tall Landscape (411×915 → 915×411)
@Preview(
    name = "Phone Tall L",
    showBackground = true,
    device = "spec:width=915dp,height=411dp,dpi=420"
)
// Tablet 10" Landscape
@Preview(
    name = "Tablet 10\" L",
    showBackground = true,
    device = "spec:width=1280dp,height=800dp,dpi=240"
)


@Composable
fun CoverPagePreview() {
    val navController = rememberNavController()
    CoverPage(navController = navController)
}
