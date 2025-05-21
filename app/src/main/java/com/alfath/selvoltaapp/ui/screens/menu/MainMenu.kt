package com.alfath.selvoltaapp.ui.screens.menu

import android.annotation.SuppressLint
import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alfath.selvoltaapp.R

private data class MenuItem(
    val title: String,
    @DrawableRes val iconRes: Int,
    val route: String
)

@SuppressLint("ContextCastToActivity")
@Composable
fun MainMenu(
    navController: NavController,
    @DrawableRes bgRes: Int = R.drawable.main_background
) {
    val items = listOf(
        MenuItem("Penentuan E Sel Volta", R.drawable.ic_penentu_sel_volta, "cathode"),
        MenuItem("Reaksi Spontan", R.drawable.ic_reaksi_spontan, "spontaneous"),
        MenuItem("Cara Kerja Sel Volta", R.drawable.ic_cara_kerja_sel_volta, "voltaic"),
        MenuItem("Pelapisan Logam", R.drawable.ic_pelapisan_logam, "electroplating"),
        MenuItem("Deret Volta", R.drawable.ic_deret_volta, "volta"),
        MenuItem("MSDS", R.drawable.ic_msds, "msds"),
        MenuItem("Latihan Soal", R.drawable.ic_latihan_soal, "exercise"),
        MenuItem("Tujuan Pembelajaran", R.drawable.ic_learning, "objective")
    )

    var showExitDialog by remember { mutableStateOf(false) }
    val activity = LocalContext.current as? Activity

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenW = maxWidth
        val screenH = maxHeight
        val density = LocalDensity.current

        // Responsive measurements
        val horizontalPadding = screenW * 0.08f
        val topGridPadding = screenH * 0.2f
        val bottomGridPadding = screenH * 0.05f
        val gridSpacingH = screenW * 0.02f
        val gridSpacingV = screenH * 0.02f
        val cardMinSize = screenW * 0.13f
        val iconSize: Dp = screenW * 0.085f
        val contentPadding = screenW * 0.01f

        val btnWidth = screenW * 0.15f
        val btnHeight = screenH * 0.075f
        val btnPadding = screenW * 0.03f

        val rawBtnHeight = screenH * 0.075f
        val baseBtnHeight = max(rawBtnHeight, 48.dp)

        // Background
        Image(
            painter = painterResource(id = bgRes),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        // Grid of menu cards
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = cardMinSize),
            contentPadding = PaddingValues(
                start = horizontalPadding,
                end = horizontalPadding,
                top = topGridPadding,
                bottom = bottomGridPadding
            ),
            horizontalArrangement = Arrangement.spacedBy(gridSpacingH),
            verticalArrangement = Arrangement.spacedBy(gridSpacingV),
            modifier = Modifier.fillMaxSize()
        ) {
            items(items) { item ->
                Card(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clickable { navController.navigate(item.route) },
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFC107))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(contentPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = item.iconRes),
                            contentDescription = item.title,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(iconSize)
                                .padding(bottom = gridSpacingV)
                        )
                        Text(
                            text = item.title,
                            fontSize = with(density) { (iconSize * 0.22f).toSp() },
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF01579B),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Exit & Back buttons side by side at bottom center
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = btnPadding)
                .padding(horizontal = screenW * 0.03f),
            horizontalArrangement = Arrangement.spacedBy(btnPadding)
        ) {
            Button(
                onClick = { showExitDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                modifier = Modifier
                    .width(btnWidth)
                    .height(baseBtnHeight),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "EXIT",
                    fontSize = with(density) { (btnHeight * 0.4f).toSp() },
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Button(
                onClick = { navController.navigate("cover") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                modifier = Modifier
                    .width(btnWidth)
                    .height(baseBtnHeight),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "BACK",
                    fontSize = with(density) { (btnHeight * 0.4f).toSp() },
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

    }

    // Confirmation dialog for exit
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Konfirmasi Keluar") },
            text = { Text("Apakah Anda yakin ingin keluar aplikasi?") },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    activity?.finishAffinity()
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
fun MainMenuPreview() {
    val navController = rememberNavController()
    MainMenu(navController)
}
