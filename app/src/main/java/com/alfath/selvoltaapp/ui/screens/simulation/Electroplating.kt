package com.alfath.selvoltaapp.ui.screens.simulation

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alfath.selvoltaapp.R
import com.alfath.selvoltaapp.ui.screens.simulation.utils.isColorDark
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

// Data class untuk bubble ion dengan animasi yang lebih smooth
data class BubbleElectroplating(
    var size: Float,
    var scale: Float = 0f,
    var progress: Float = 0f,
    var opacity: Float = 1f,
    val startOffset: Offset,
    val endOffset: Offset,
    val animationDelay: Long = 0L
)

data class MetalElectroplating(
    val symbol: String,
    val ion: String,
    val potential: Float,
    val color: Color
)

// Fungsi untuk mendapatkan ikon berdasarkan simbol logam
private fun getIconForMetal(metal: MetalElectroplating): Int {
    return when (metal.symbol) {
        "Pt" -> R.drawable.ic_pt
        "Cu" -> R.drawable.ic_cu
        "Ca" -> R.drawable.ic_ca
        "Ba" -> R.drawable.ic_ba
        else -> R.drawable.ic_default
    }
}

@SuppressLint("ContextCastToActivity")
@Composable
fun Electroplating(
    navController: NavController,
    @DrawableRes bgRes: Int = R.drawable.content_background,
    @DrawableRes cellRes: Int = R.drawable.electroplatig_background
) {
    val anodeMetals = listOf(
        MetalElectroplating("Pt", "Pt²⁺", 1.18f, Color(0xFF808080)),
        MetalElectroplating("Cu", "Cu²⁺", 0.34f, Color(0xFFCD7F32))
    )

    val cathodeMetals = listOf(
        MetalElectroplating("Ca", "Ca²⁺", -2.87f, Color(0xFF9ACD32)),
        MetalElectroplating("Ba", "Ba²⁺", -2.91f, Color(0xFFADFF2F))
    )

    var selectedAnode by remember { mutableStateOf<MetalElectroplating?>(null) }
    var selectedCathode by remember { mutableStateOf<MetalElectroplating?>(null) }
    var isElectroplating by remember { mutableStateOf(false) }

    // State untuk ion yang menempel di katoda (hanya Offset)
    val attachedIons = remember { mutableStateListOf<Offset>() }

    // State untuk ukuran anoda yang semakin mengecil (dalam faktor 0.0 - 1.0)
    var anodeSizeFactor by remember { mutableFloatStateOf(1f) }

    // State untuk ketebalan lapisan di katoda (dalam dp)
    var cathodeLayerThickness by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(selectedAnode, selectedCathode) {
        isElectroplating = selectedAnode != null && selectedCathode != null
        if (!isElectroplating) {
            attachedIons.clear()
            anodeSizeFactor = 1f
            cathodeLayerThickness = 0f
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
        val titleFont = 20.sp
        val metalFont = 12.sp

        val padX = screenW * 0.01f
        val padY = screenH * 0.03f
        val btnSize = screenH * 0.1f

        Image(
            painter = painterResource(id = bgRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.FillBounds
        )

        IconButton(
            onClick = { navController.navigateUp() },
            modifier = Modifier
                .padding(pad)
                .offset(x = padX, y = padY)
                .size(btnSize)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }

        Text(
            text = "PELAPISAN LOGAM",
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
            // Panel Seleksi Logam
            Column(
                Modifier
                    .width(listW)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Pilih Logam Anoda",
                    color = Color.Black,
                    fontSize = metalFont,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(screenH * 0.02f))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(listW * 0.05f),
                    horizontalArrangement = Arrangement.spacedBy(listW * 0.05f),
                    modifier = Modifier.height(screenH * 0.3f)
                ) {
                    items(anodeMetals) { metal ->
                        Box(modifier = Modifier
                            .width(listW * 0.3f)
                            .height(listW * 0.6f)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        if (selectedAnode == metal) Color.Green else metal.color,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            )
                            Text(
                                text = metal.symbol,
                                color = if (isColorDark(if (selectedAnode == metal) Color.Green else metal.color)) Color.White else Color.Black,
                                fontSize = metalFont,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Alignment.Center)
                            )
                            Button(
                                onClick = { selectedAnode = metal },
                                modifier = Modifier.fillMaxSize(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                content = {}
                            )
                        }
                    }
                }

                Text(
                    "Pilih Logam Katoda",
                    color = Color.Black,
                    fontSize = metalFont,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(screenH * 0.02f))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(listW * 0.05f),
                    horizontalArrangement = Arrangement.spacedBy(listW * 0.05f),
                    modifier = Modifier.height(screenH * 0.25f)
                ) {
                    items(cathodeMetals) { metal ->
                        Box(modifier = Modifier
                            .width(listW * 0.3f)
                            .height(listW * 0.6f)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        if (selectedCathode == metal) Color.Green else metal.color,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            )
                            Text(
                                text = metal.symbol,
                                color = if (isColorDark(if (selectedCathode == metal) Color.Green else metal.color)) Color.White else Color.Black,
                                fontSize = metalFont,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Alignment.Center)
                            )
                            Button(
                                onClick = { selectedCathode = metal },
                                modifier = Modifier.fillMaxSize(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                content = {}
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.width(pad))

            // Area Sel Elektrolisis dengan Animasi yang Diperbaiki
            Box(Modifier
                .width(cellW)
                .height(cellH)
                .background(Color.White)) {
                Image(
                    painter = painterResource(id = cellRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                )

                // Anoda (semakin mengecil seiring waktu)
                val anodeX = cellW * 0.15f
                val anodeY = cellH * 0.25f
                val baseAnodeWidth = cellW * 0.07f
                val baseAnodeHeight = cellH * 0.4f

                val animatedAnodeSizeFactor by animateFloatAsState(
                    targetValue = anodeSizeFactor,
                    animationSpec = tween(durationMillis = 300)
                )

                // Hitung ukuran anoda berdasarkan faktor
                val currentAnodeWidth = baseAnodeWidth * animatedAnodeSizeFactor
                val currentAnodeHeight = baseAnodeHeight * animatedAnodeSizeFactor

                // Posisi tengah anoda tetap sama
                val anodeOffsetX = anodeX + (baseAnodeWidth - currentAnodeWidth) / 2
                val anodeOffsetY = anodeY + (baseAnodeHeight - currentAnodeHeight) / 2

                Box(
                    modifier = Modifier
                        .offset(x = anodeOffsetX, y = anodeOffsetY)
                        .size(width = currentAnodeWidth, height = currentAnodeHeight)
                        .border(1.dp, Color.Gray)
                ) {
                    if (selectedAnode != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(selectedAnode!!.color.copy(alpha = 0.7f))
                        ) {
                            Text(
                                text = selectedAnode!!.symbol,
                                color = if (isColorDark(selectedAnode!!.color)) Color.White else Color.Black,
                                fontSize = metalFont,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }

                // Katoda dengan lapisan yang semakin tebal
                val cathodeX = cellW * 0.325f
                val cathodeY = cellH * 0.25f
                val baseCathodeWidth = cellW * 0.07f
                val baseCathodeHeight = cellH * 0.4f

                val animatedLayerThickness by animateFloatAsState(
                    targetValue = cathodeLayerThickness,
                    animationSpec = tween(durationMillis = 500)
                )

                // Konversi thickness ke Dp
                val layerThicknessDp = animatedLayerThickness.dp
                val currentCathodeWidth = baseCathodeWidth + layerThicknessDp
                val currentCathodeHeight = baseCathodeHeight

                // Base katoda
                Box(
                    modifier = Modifier
                        .offset(
                            x = cathodeX - layerThicknessDp / 2,
                            y = cathodeY
                        )
                        .size(width = currentCathodeWidth, height = currentCathodeHeight)
                        .border(1.dp, Color.Gray)
                        .background(selectedCathode?.color ?: Color.Transparent)
                ) {
                    if (selectedCathode != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(selectedCathode!!.color.copy(alpha = 0.7f))
                        ) {
                            Text(
                                text = selectedCathode!!.symbol,
                                color = if (isColorDark(selectedCathode!!.color)) Color.White else Color.Black,
                                fontSize = metalFont,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }

                // Lapisan logam dari anoda di katoda
                if (selectedAnode != null && animatedLayerThickness > 0) {
                    Box(
                        modifier = Modifier
                            .offset(
                                x = cathodeX - layerThicknessDp / 2,
                                y = cathodeY
                            )
                            .size(width = currentCathodeWidth, height = currentCathodeHeight)
                            .background(
                                selectedAnode!!.color.copy(alpha = 0.6f),
                                RoundedCornerShape(2.dp)
                            )
                    )
                }

                // Animasi Ion yang Diperbaiki
                if (isElectroplating && selectedAnode != null) {
                    ImprovedIonAnimation(
                        anodePosition = Offset(
                            with(density) { (anodeX + currentAnodeWidth).toPx() },
                            with(density) { (anodeY + currentAnodeHeight / 2).toPx() }
                        ),
                        cathodePosition = Offset(
                            with(density) { cathodeX.toPx() },
                            with(density) { (cathodeY + currentCathodeHeight / 2).toPx() }
                        ),
                        cathodeWidthPx = with(density) { currentCathodeWidth.toPx() },
                        cathodeHeightPx = with(density) { currentCathodeHeight.toPx() },
                        metal = selectedAnode!!,
                        onIonAttached = { offset: Offset ->
                            attachedIons.add(offset)
                            if (anodeSizeFactor > 0.75f) anodeSizeFactor -= 0.005f
                            if (cathodeLayerThickness < 10f) cathodeLayerThickness += 0.2f
                        }
                    )
                }

                // Render ion yang menempel di katoda
                attachedIons.forEach { offset ->
                    Image(
                        painter = painterResource(id = getIconForMetal(selectedAnode!!)),
                        contentDescription = "Ion menempel",
                        modifier = Modifier
                            .offset(
                                x = with(density) { offset.x.toDp() },
                                y = with(density) { offset.y.toDp() }
                            )
                            .size(20.dp)
                            .graphicsLayer { alpha = 0.8f }
                    )
                }
            }

            Spacer(Modifier.width(pad))

            // Panel Kontrol
            Column(
                Modifier
                    .width(btnW)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        selectedAnode = null
                        selectedCathode = null
                        isElectroplating = false
                        attachedIons.clear()
                        anodeSizeFactor = 1f
                        cathodeLayerThickness = 0f
                    },
                    modifier = Modifier
                        .width(btnW)
                        .height(btnH),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) {
                    Text("RESET")
                }

                Spacer(Modifier.height(16.dp))

                // Informasi status
                if (isElectroplating) {
                    Card(
                        modifier = Modifier.padding(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Status:",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Electroplating Active",
                                fontSize = 14.sp,
                                color = Color.Blue
                            )
                            Text(
                                "Anoda: ${(anodeSizeFactor * 100).toInt()}%",
                                fontSize = 14.sp
                            )
                            Text(
                                "Lapisan: ${(cathodeLayerThickness * 10).toInt()}μm",
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ImprovedIonAnimation(
    anodePosition: Offset,
    cathodePosition: Offset,
    cathodeWidthPx: Float,
    cathodeHeightPx: Float,
    metal: MetalElectroplating,
    onIonAttached: (Offset) -> Unit
) {
    val ions = remember { mutableStateListOf<BubbleElectroplating>() }
    val density = LocalDensity.current

    // Generator ion yang keluar dari anoda
    LaunchedEffect(Unit) {
        while (true) {
            if (ions.size < 30) {
                val randomStartY = anodePosition.y + (Random.nextFloat() - 0.5f) * 80f
                val randomEndX = cathodePosition.x + (Random.nextFloat() - 0.5f) * cathodeWidthPx * 0.8f
                val randomEndY = cathodePosition.y + (Random.nextFloat() - 0.5f) * cathodeHeightPx * 0.8f

                ions.add(
                    BubbleElectroplating(
                        size = Random.nextFloat() * 12 + 8,
                        startOffset = Offset(anodePosition.x - 50f, randomStartY + 65f),
                        endOffset = Offset(randomEndX, randomEndY),
                        animationDelay = Random.nextLong(0, 1000)
                    )
                )
            }
            delay(100 + Random.nextLong(500)) // Jeda dikurangi untuk lebih banyak ion
        }
    }

    // Render dan animate setiap ion
    Box(modifier = Modifier.fillMaxSize()) {
        ions.forEach { bubble ->
            var hasStarted by remember { mutableStateOf(false) }

            // Delay sebelum animasi dimulai
            LaunchedEffect(bubble) {
                delay(bubble.animationDelay)
                hasStarted = true

                // Animasi scale in
                while (bubble.scale < 1f) {
                    bubble.scale += 0.05f
                    delay(50)
                }

                // Animasi pergerakan
                while (bubble.progress < 1f) {
                    bubble.progress += 0.008f
                    delay(16)
                }

                // Ion menempel di katoda, tambahkan posisi akhir
                onIonAttached(bubble.endOffset)

                // Fade out
                while (bubble.opacity > 0f) {
                    bubble.opacity -= 0.1f
                    delay(50)
                }
            }

            if (hasStarted) {
                val progress by animateFloatAsState(
                    targetValue = bubble.progress,
                    animationSpec = tween(durationMillis = 3000)
                )

                val scale by animateFloatAsState(
                    targetValue = bubble.scale,
                    animationSpec = tween(durationMillis = 500)
                )

                // Posisi ion dengan efek gelombang
                val currentX = bubble.startOffset.x + (bubble.endOffset.x - bubble.startOffset.x) * progress
                val currentY = bubble.startOffset.y + (bubble.endOffset.y - bubble.startOffset.y) * progress +
                        (sin(progress * PI.toFloat() * 3) * 15f) // Efek gelombang

                // Ion digambarkan sebagai ikon
                Image(
                    painter = painterResource(id = getIconForMetal(metal)),
                    contentDescription = "Ion ${metal.ion}",
                    modifier = Modifier
                        .offset(
                            x = with(density) { currentX.toDp() },
                            y = with(density) { currentY.toDp() }
                        )
                        .size(bubble.size.dp)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            alpha = bubble.opacity
                        }
                )
            }
        }

        // Bersihkan ion yang sudah selesai animasi
        LaunchedEffect(ions.size) {
            ions.removeAll { it.opacity <= 0f }
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
fun ElectroplatingReactionPreview() {
    val navController = rememberNavController()
    Electroplating(navController)
}