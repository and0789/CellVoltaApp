package com.alfath.selvoltaapp.ui.screens.simulation

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alfath.selvoltaapp.R
import com.alfath.selvoltaapp.ui.screens.simulation.utils.isColorDark

// Konstanta untuk meningkatkan keterbacaan dan memudahkan perubahan
private const val ANIMATION_DURATION_MS = 2000
private const val MAX_IONS = 60
private const val IONS_TO_REMOVE = 10
private const val ELECTRON_DELAY_MS = 300L
private const val MAX_ATTACHED_ELECTRONS = 5

@SuppressLint("ContextCastToActivity")
@Composable
fun SpontaneousReaction(
    navController: NavController,
    @DrawableRes bgRes: Int = R.drawable.content_background,
    @DrawableRes cellRes: Int = R.drawable.ic_cell_diagram
) {
    val metals = listOf(
        Metal("Ba", "Ba²⁺", -2.91f, Color(0xFF9ACD32)),
        Metal("Cr", "Cr³⁺", 0.74f, Color(0xFF800080)),
        Metal("Mn", "Mn²⁺", -1.18f, Color(0xFFFFC0CB)),
        Metal("Pb", "Pb²⁺", -0.44f, Color(0xFF696969))
    )

    var anode by remember { mutableStateOf<Metal?>(null) }
    var cathode by remember { mutableStateOf<Metal?>(null) }
    var selectedMetal by remember { mutableStateOf<Metal?>(null) }
    var showPlacementMenu by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var errorCount by remember { mutableIntStateOf(0) }
    var showPotentialTable by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Menghitung potensial sel jika anode dan katoda ada
    val cellE: Float? = anode?.let { a -> cathode?.let { c -> c.potential - a.potential } }

    var isAnimationRunning by remember { mutableStateOf(false) }

    // Validasi kombinasi anode dan katoda
    LaunchedEffect(anode, cathode) {
        if (anode != null && cathode != null) {
            if (anode == cathode) {
                errorMessage = "Logam anode dan katode tidak boleh sama."
                errorCount++
            } else if (cellE!! <= 0) {
                errorMessage = "Kombinasi tidak valid: Reaksi tidak spontan."
                errorCount++
            } else {
                errorMessage = null
                errorCount = 0
            }
            if (errorCount >= 3) {
                showPotentialTable = true
            }
        } else {
            errorMessage = null
        }
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val screenW = maxWidth
        val screenH = maxHeight
        val density = LocalDensity.current
        val pad = screenW * 0.02f
        val listW = screenW * 0.18f
        val cellW = screenW * 0.5f
        val cellH = cellW * 0.7f
        val btnW = screenW * 0.2f
        val extraTop = screenW * 0.085f
        val extraStart = screenW * 0.015f
        val rawBtnH = screenH * 0.06f
        val btnH = max(rawBtnH, 48.dp)
        val titleFont = with(density) { (screenW * 0.05f).toSp() }
        val potFont = with(density) { (screenW * 0.015f).toSp() }
        val metalFont = with(density) { (screenW * 0.01f).toSp() }

        // Latar belakang layar
        Image(
            painter = painterResource(id = bgRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.FillBounds
        )

        // Tombol kembali
        IconButton(
            onClick = { navController.navigateUp() },
            modifier = Modifier
                .padding(pad)
                .size(32.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Kembali",
                tint = Color.Black
            )
        }

        // Judul layar
        Text(
            text = "MENENTUKAN REAKSI SPONTAN",
            fontSize = titleFont,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = pad + screenW * 0.015f)
        )

        Row(
            Modifier
                .fillMaxSize()
                .padding(top = pad + extraTop, start = pad + extraStart, end = pad, bottom = pad),
            verticalAlignment = Alignment.Top
        ) {
            // Kolom pemilihan logam
            Column(
                Modifier
                    .width(listW)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "LOGAM",
                    color = Color.Black,
                    fontSize = metalFont,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(screenH * 0.05f))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(listW * 0.05f),
                    horizontalArrangement = Arrangement.spacedBy(listW * 0.05f),
                    modifier = Modifier.height(screenH * 0.5f)
                ) {
                    items(metals) { m ->
                        Box(
                            modifier = Modifier
                                .width(listW * 0.3f)
                                .height(listW * 0.8f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        color = if (anode == m || cathode == m) Color.Green else m.color,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            )
                            val textColor = if (anode == m || cathode == m) Color.Black else {
                                if (isColorDark(m.color)) Color.White else Color.Black
                            }
                            Text(
                                text = m.symbol,
                                color = textColor,
                                fontSize = metalFont,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Alignment.Center)
                            )
                            Button(
                                onClick = {
                                    selectedMetal = m
                                    showPlacementMenu = true
                                },
                                modifier = Modifier.fillMaxSize(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                content = {}
                            )
                        }
                    }
                }

                // Menu dropdown untuk memilih anode atau katoda
                DropdownMenu(
                    expanded = showPlacementMenu,
                    onDismissRequest = { showPlacementMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Tempatkan di Anoda") },
                        onClick = {
                            if (cathode != selectedMetal) {
                                anode = selectedMetal
                            } else {
                                errorMessage = "Logam sudah digunakan di katode."
                            }
                            showPlacementMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Tempatkan di Katoda") },
                        onClick = {
                            if (anode != selectedMetal) {
                                cathode = selectedMetal
                            } else {
                                errorMessage = "Logam sudah digunakan di anoda."
                            }
                            showPlacementMenu = false
                        }
                    )
                }
            }

            Spacer(Modifier.width(pad))

            // Diagram sel volta
            Box(
                Modifier
                    .width(cellW)
                    .height(cellH)
                    .background(Color.White)
            ) {
                Image(
                    painter = painterResource(id = cellRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                )

                cellE?.let { e ->
                    val animatedPotential by animateFloatAsState(
                        targetValue = e,
                        animationSpec = tween(durationMillis = ANIMATION_DURATION_MS)
                    )
                    Text(
                        text = if (e > 0f) "%.2f V".format(animatedPotential) else "Err!",
                        color = if (e > 0f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        fontSize = metalFont,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = cellH * 0.13f)
                            .offset(x = cellW * 0.023f)
                    )
                    if (e > 0f) {
                        Text(
                            text = "Reaksi Telah Spontan",
                            color = Color.Green,
                            fontSize = 14.sp,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }
                }

                // Kotak untuk anode
                Box(
                    Modifier
                        .offset(x = cellW * 0.254f, y = cellH * 0.4f)
                        .size(width = cellW * 0.1f, height = cellH * 0.4f)
                        .border(0.dp, Color.Green, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (anode != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(0.8f)
                                .background(
                                    color = anode!!.color,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        )
                        Text(
                            text = anode!!.symbol,
                            color = if (isColorDark(anode!!.color)) Color.White else Color.Black,
                            fontSize = metalFont,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                // Kotak untuk katoda
                Box(
                    Modifier
                        .offset(x = cellW * 0.67f, y = cellH * 0.4f)
                        .size(width = cellW * 0.1f, height = cellH * 0.4f)
                        .border(0.dp, Color.Green, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (cathode != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(0.8f)
                                .background(
                                    color = cathode!!.color,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        )
                        Text(
                            text = cathode!!.symbol,
                            color = if (isColorDark(cathode!!.color)) Color.White else Color.Black,
                            fontSize = metalFont,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                // Animasi jika sel valid
                if (cellE != null && cellE > 0) {
                    AnodeIonAnimation(
                        position = Offset(
                            x = with(density) { (cellW * 0.24f + (cellW * 0.1f) / 2).toPx() },
                            y = with(density) { (cellH * 0.15f + cellH * 0.4f).toPx() }
                        ),
                        widthPx = with(density) { (cellW * 0.1f).toPx() },
                        heightPx = with(density) { (cellH * 0.25f).toPx() },
                        metal = anode
                    )
                    CathodeIonAnimation(
                        position = Offset(
                            x = with(density) { (cellW * 0.65f + (cellW * 0.1f) / 2).toPx() },
                            y = with(density) { (cellH * 0.3f + cellH * 0.4f).toPx() }
                        ),
                        widthPx = with(density) { (cellW * 0.1f).toPx() },
                        heightPx = with(density) { (cellH * 0.1f).toPx() },
                        metal = cathode
                    )
                    ElectronAnimation(
                        startX = with(density) { (cellW * 0.23f + (cellW * 0.1f) / 2).toPx() },
                        startY = with(density) { (cellH * 0.4f).toPx() },
                        endX = with(density) { (cellW * 0.67f + (cellW * 0.1f) / 2).toPx() },
                        endY = with(density) { (cellH * 0.35f).toPx() },
                        topY = with(density) { (cellH * 0.175f).toPx() },
                        isRunning = isAnimationRunning
                    )
                }
            }

            Spacer(Modifier.width(pad))

            // Kolom informasi potensial dan tombol reset
            Column(
                Modifier
                    .width(screenW * 0.2f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showPotentialTable) {
                    Text("Tabel Potensial", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    metals.forEach { m ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${m.ion} + ${if (m.symbol == "Cr") "3e⁻" else "2e⁻"} → ${m.symbol}(s)",
                                modifier = Modifier.weight(1f),
                                fontSize = potFont
                            )
                            Text(
                                text = "%.2f V".format(m.potential),
                                modifier = Modifier.width(48.dp),
                                textAlign = TextAlign.End,
                                fontSize = potFont
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        anode = null
                        cathode = null
                        selectedMetal = null
                        errorMessage = null
                        errorCount = 0
                        showPotentialTable = false
                    },
                    modifier = Modifier
                        .width(btnW)
                        .height(btnH)
                ) {
                    Text("RESET")
                }
            }
        }

        // Menampilkan pesan kesalahan jika ada
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
}

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
fun SpontaneousReactionPreviews() {
    val navController = rememberNavController()
    SpontaneousReaction(navController)
}