//package com.alfath.selvoltaapp.ui.screens.simulation
//
//import android.annotation.SuppressLint
//import androidx.annotation.DrawableRes
//import androidx.compose.animation.core.animateFloatAsState
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.BoxWithConstraints
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.offset
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.grid.GridCells
//import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//import androidx.compose.foundation.lazy.grid.items
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableFloatStateOf
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.platform.LocalDensity
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.max
//import androidx.navigation.NavController
//import androidx.navigation.compose.rememberNavController
//import com.alfath.selvoltaapp.R
//import com.alfath.selvoltaapp.ui.screens.simulation.models.Bubble
//import com.alfath.selvoltaapp.ui.screens.simulation.models.Metal
//import com.alfath.selvoltaapp.ui.screens.simulation.utils.isColorDark
//import kotlinx.coroutines.delay
//import kotlin.random.Random
//
//@SuppressLint("ContextCastToActivity")
//@Composable
//fun Electroplating(
//    navController: NavController,
//    @DrawableRes bgRes: Int = R.drawable.content_background,
//    @DrawableRes cellRes: Int = R.drawable.ic_electroplating
//) {
//    val anodeMetals = listOf(
//        Metal("Pt", "Pt²⁺", 1.18f, Color(0xFF808080)),
//        Metal("Cu", "Cu²⁺", 0.34f, Color(0xFFCD7F32))
//    )
//
//    val cathodeMetals = listOf(
//        Metal("Ca", "Ca²⁺", -2.87f, Color(0xFF9ACD32)),
//        Metal("Ba", "Ba²⁺", -2.91f, Color(0xFFADFF2F))
//    )
//
//    var selectedAnode by remember { mutableStateOf<Metal?>(null) }
//    var selectedCathode by remember { mutableStateOf<Metal?>(null) }
//    var isElectroplating by remember { mutableStateOf(false) }
//    val attachedIons = remember { mutableStateListOf<Offset>() }
//    var anodeSizeFactor by remember { mutableFloatStateOf(1f) } // Mengontrol ukuran anoda
//
//    LaunchedEffect(selectedAnode, selectedCathode) {
//        isElectroplating = selectedAnode != null && selectedCathode != null
//    }
//
//    BoxWithConstraints(Modifier.fillMaxSize()) {
//        val screenW = maxWidth
//        val screenH = maxHeight
//        val density = LocalDensity.current
//        val pad = screenW * 0.02f
//        val listW = screenW * 0.18f
//        val cellW = screenW * 0.5f
//        val cellH = cellW * 0.7f
//        val btnW = screenW * 0.2f
//        val extraTop = screenW * 0.085f
//        val extraStart = screenW * 0.015f
//        val rawBtnH = screenH * 0.06f
//        val btnH = max(rawBtnH, 48.dp)
//        val titleFont = with(density) { (screenW * 0.05f).toSp() }
//        val metalFont = with(density) { (screenW * 0.02f).toSp() }
//
//        Image(
//            painter = painterResource(id = bgRes),
//            contentDescription = null,
//            modifier = Modifier.fillMaxSize(),
//            contentScale = androidx.compose.ui.layout.ContentScale.FillBounds
//        )
//
//        IconButton(
//            onClick = { navController.navigateUp() },
//            modifier = Modifier.padding(pad).size(32.dp)
//        ) {
//            Icon(
//                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                contentDescription = "Kembali",
//                tint = Color.Black
//            )
//        }
//
//        Text(
//            text = "PELAPISAN LOGAM",
//            fontSize = titleFont,
//            fontWeight = FontWeight.Bold,
//            color = MaterialTheme.colorScheme.primary,
//            modifier = Modifier.align(Alignment.TopCenter).padding(top = pad + screenW * 0.015f)
//        )
//
//        Row(
//            Modifier.fillMaxSize().padding(top = pad + extraTop, start = pad + extraStart, end = pad, bottom = pad),
//            verticalAlignment = Alignment.Top
//        ) {
//            Column(
//                Modifier.width(listW).fillMaxHeight().verticalScroll(rememberScrollState()),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text("Pilih Logam Anoda", color = Color.Black, fontSize = metalFont, fontWeight = FontWeight.Bold)
//                Spacer(Modifier.height(screenH * 0.02f))
//                LazyVerticalGrid(
//                    columns = GridCells.Fixed(2),
//                    verticalArrangement = Arrangement.spacedBy(listW * 0.05f),
//                    horizontalArrangement = Arrangement.spacedBy(listW * 0.05f),
//                    modifier = Modifier.height(screenH * 0.3f)
//                ) {
//                    items(anodeMetals) { metal ->
//                        Box(modifier = Modifier.width(listW * 0.3f).height(listW * 0.6f)) {
//                            Box(
//                                modifier = Modifier.fillMaxSize()
//                                    .background(if (selectedAnode == metal) Color.Green else metal.color, RoundedCornerShape(8.dp))
//                                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
//                            )
//                            Text(
//                                text = metal.symbol,
//                                color = if (isColorDark(if (selectedAnode == metal) Color.Green else metal.color)) Color.White else Color.Black,
//                                fontSize = metalFont,
//                                textAlign = TextAlign.Center,
//                                modifier = Modifier.align(Alignment.Center)
//                            )
//                            Button(
//                                onClick = { selectedAnode = metal },
//                                modifier = Modifier.fillMaxSize(),
//                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
//                                content = {}
//                            )
//                        }
//                    }
//                }
//
//                Text("Pilih Logam Katoda", color = Color.Black, fontSize = metalFont, fontWeight = FontWeight.Bold)
//                Spacer(Modifier.height(screenH * 0.02f))
//                LazyVerticalGrid(
//                    columns = GridCells.Fixed(2),
//                    verticalArrangement = Arrangement.spacedBy(listW * 0.05f),
//                    horizontalArrangement = Arrangement.spacedBy(listW * 0.05f),
//                    modifier = Modifier.height(screenH * 0.25f)
//                ) {
//                    items(cathodeMetals) { metal ->
//                        Box(modifier = Modifier.width(listW * 0.3f).height(listW * 0.6f)) {
//                            Box(
//                                modifier = Modifier.fillMaxSize()
//                                    .background(if (selectedCathode == metal) Color.Green else metal.color, RoundedCornerShape(8.dp))
//                                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
//                            )
//                            Text(
//                                text = metal.symbol,
//                                color = if (isColorDark(if (selectedCathode == metal) Color.Green else metal.color)) Color.White else Color.Black,
//                                fontSize = metalFont,
//                                textAlign = TextAlign.Center,
//                                modifier = Modifier.align(Alignment.Center)
//                            )
//                            Button(
//                                onClick = { selectedCathode = metal },
//                                modifier = Modifier.fillMaxSize(),
//                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
//                                content = {}
//                            )
//                        }
//                    }
//                }
//            }
//
//            Spacer(Modifier.width(pad))
//
//            Box(Modifier.width(cellW).height(cellH).background(Color.White)) {
//                Image(
//                    painter = painterResource(id = cellRes),
//                    contentDescription = null,
//                    modifier = Modifier.fillMaxSize(),
//                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
//                )
//
//                // Anoda dengan ukuran yang berkurang
//                val anodeWidth = cellW * 0.25f
//                val anodeHeight = cellH * 0.75f
//                Box(
//                    modifier = Modifier.align(Alignment.CenterStart)
//                        .offset(x = cellW * 0.13f)
//                        .size(width = cellW * 0.1f * anodeSizeFactor, height = cellH * 0.4f * anodeSizeFactor)
//                        .border(1.dp, Color.Gray)
//                ) {
//                    if (selectedAnode != null) {
//                        Box(
//                            modifier = Modifier.fillMaxSize().background(selectedAnode!!.color)
//                        ) {
//                            Text(
//                                text = selectedAnode!!.symbol,
//                                color = if (isColorDark(selectedAnode!!.color)) Color.White else Color.Black,
//                                fontSize = metalFont,
//                                modifier = Modifier.align(Alignment.Center)
//                            )
//                        }
//                    }
//                }
//
//                // Katoda dengan lapisan yang bertambah
//                Box(
//                    modifier = Modifier.align(Alignment.CenterEnd)
//                        .offset(x = -cellW * 0.6f)
//                        .size(width = cellW * 0.1f, height = cellH * 0.4f)
//                        .border(1.dp, Color.Gray)
//                        .background(selectedCathode?.color ?: Color.Transparent)
//                ) {
//                    if (selectedCathode != null) {
//                        Box(
//                            modifier = Modifier.fillMaxSize()
//                                .background(selectedCathode!!.color.copy(alpha = 0.5f)) // Efek kilau
//                        ) {
//                            Text(
//                                text = selectedCathode!!.symbol,
//                                color = if (isColorDark(selectedCathode!!.color)) Color.White else Color.Black,
//                                fontSize = metalFont,
//                                modifier = Modifier.align(Alignment.Center)
//                            )
//                        }
//                    }
//                }
//
//                if (isElectroplating) {
//                    selectedAnode?.let { anodeMetal ->
//                        val cathodeCenterX = cellW * 0.35f
//                        val cathodeCenterY = cellH * 0.5f
//                        IonAnimation(
//                            anodeWidth = anodeWidth,
//                            anodeHeight = anodeHeight,
//                            anodeOffsetX = cellW * 0.13f,
//                            endPosition = Offset(with(density) { cathodeCenterX.toPx() }, with(density) { cathodeCenterY.toPx() }),
//                            metal = anodeMetal,
//                            cellWidth = cellW,
//                            cellHeight = cellH,
//                            onIonAttached = { offset ->
//                                attachedIons.add(offset)
//                                if (anodeSizeFactor > 0.5f) anodeSizeFactor -= 0.002f // Kurangi ukuran anoda perlahan
//                            }
//                        )
//                    }
//                }
//
//                // Tampilkan ion yang menempel di katoda
//                attachedIons.forEach { offset ->
//                    Image(
//                        painter = painterResource(id = getIconForMetal(selectedAnode!!)),
//                        contentDescription = "Ion menempel",
//                        modifier = Modifier.offset(x = with(density) { offset.x.toDp() }, y = with(density) { offset.y.toDp() })
//                            .size(10.dp)
//                            .graphicsLayer { alpha = 0.8f } // Efek visual tambahan
//                    )
//                }
//            }
//
//            Spacer(Modifier.width(pad))
//
//            Column(Modifier.width(btnW).fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
//                Button(
//                    onClick = {
//                        selectedAnode = null
//                        selectedCathode = null
//                        isElectroplating = false
//                        attachedIons.clear()
//                        anodeSizeFactor = 1f // Reset ukuran anoda
//                    },
//                    modifier = Modifier.width(btnW).height(btnH),
//                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
//                ) {
//                    Text("RESET")
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun IonAnimation(
//    anodeWidth: Dp,
//    anodeHeight: Dp,
//    anodeOffsetX: Dp,
//    endPosition: Offset,
//    metal: Metal,
//    cellWidth: Dp,
//    cellHeight: Dp,
//    onIonAttached: (Offset) -> Unit
//) {
//    val ions = remember { mutableStateListOf<Bubble>() }
//    val density = LocalDensity.current
//    val anodeWidthPx = with(density) { anodeWidth.toPx() }
//    val anodeHeightPx = with(density) { anodeHeight.toPx() }
//    val anodeOffsetXPx = with(density) { anodeOffsetX.toPx() }
//    val anodeOffsetYPx = with(density) { (cellHeight * 0.35f).toPx() } // Posisi Y anoda disesuaikan
//    val cathodeWidthPx = with(density) { (cellWidth * 0.1f).toPx() }
//    val cathodeHeightPx = with(density) { (cellHeight * 0.3f).toPx() }
//
//    LaunchedEffect(Unit) {
//        while (true) {
//            if (ions.size < 3) { // Batasi jumlah ion untuk kejelasan
//                ions.add(Bubble(size = Random.nextFloat() * 6 + 6)) // Ukuran ion lebih besar untuk kejelasan
//            }
//            delay(800) // Frekuensi kemunculan lebih lambat
//            ions.removeAll { it.progress >= 1f }
//        }
//    }
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        ions.forEach { bubble ->
//            val progress by animateFloatAsState(
//                targetValue = bubble.progress,
//                animationSpec = tween(durationMillis = 5000) // Durasi lebih panjang untuk gerakan lambat
//            )
//            LaunchedEffect(bubble) {
//                while (bubble.progress < 1f) {
//                    bubble.progress += 0.004f // Kecepatan lebih lambat
//                    delay(16)
//                }
//                val attachedOffset = Offset(
//                    x = endPosition.x + (Random.nextFloat() - 0.5f) * cathodeWidthPx * 0.3f, // Area penempelan lebih kecil
//                    y = endPosition.y + (Random.nextFloat() - 0.5f) * cathodeHeightPx * 0.3f
//                )
//                onIonAttached(attachedOffset)
//            }
//
//            // Posisi awal di sisi kanan anoda
//            val startPosition = Offset(
//                x = anodeOffsetXPx + anodeWidthPx * 0.8f, // Ion muncul dari sisi kanan anoda
//                y = anodeOffsetYPx + Random.nextFloat() * anodeHeightPx * 0.6f // Variasi vertikal kecil
//            )
//            // Lintasan lurus dengan sedikit variasi acak
//            val currentX = startPosition.x + (endPosition.x - startPosition.x) * progress + Random.nextFloat() * 5f - 2.5f
//            val currentY = startPosition.y + (endPosition.y - startPosition.y) * progress + Random.nextFloat() * 5f - 2.5f
//
//            Image(
//                painter = painterResource(id = getIconForMetal(metal)),
//                contentDescription = "Ion ${metal.ion}",
//                modifier = Modifier.offset(x = with(density) { currentX.toDp() }, y = with(density) { currentY.toDp() })
//                    .size(bubble.size.dp)
//                    .graphicsLayer { alpha = 1f - progress * 0.3f } // Memudar sedikit untuk efek realistis
//            )
//        }
//    }
//}
//
//private fun getIconForMetal(metal: Metal): Int {
//    return when (metal.symbol) {
//        "Pt" -> R.drawable.ic_pt
//        "Cu" -> R.drawable.ic_cu
//        "Ca" -> R.drawable.ic_ca
//        "Ba" -> R.drawable.ic_ba
//        else -> R.drawable.ic_default
//    }
//}
//
//@Preview(
//    name = "Phone 16:9 L",
//    showBackground = true,
//    device = "spec:width=640dp,height=360dp,dpi=320"
//)
//@Preview(
//    name = "Phone 18:9 L",
//    showBackground = true,
//    device = "spec:width=740dp,height=360dp,dpi=320"
//)
//@Preview(
//    name = "Phone Tall L",
//    showBackground = true,
//    device = "spec:width=915dp,height=411dp,dpi=420"
//)
//@Preview(
//    name = "Tablet 10\" L",
//    showBackground = true,
//    device = "spec:width=1280dp,height=800dp,dpi=240"
//)
//@Composable
//fun ElectroplatingReactionPreview() {
//    val navController = rememberNavController()
//    Electroplating(navController)
//}