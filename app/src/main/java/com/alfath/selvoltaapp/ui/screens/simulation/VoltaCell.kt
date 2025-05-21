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
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alfath.selvoltaapp.R
import com.alfath.selvoltaapp.ui.screens.simulation.utils.isColorDark
import kotlinx.coroutines.delay

@SuppressLint("ContextCastToActivity")
@Composable
fun VoltaCell(
    navController: NavController,
    @DrawableRes bgRes: Int = R.drawable.content_background,
    @DrawableRes cellRes: Int = R.drawable.ic_cell_diagram
) {
    val metals = listOf(
        Metal("Zn", "Zn²⁺", -0.76f, Color(0xFFA9A9A9)),
        Metal("Cu", "Cu²⁺", 0.34f, Color(0xFFCD7F32)),
        Metal("Mg", "Mg²⁺", -2.37f, Color(0xFFD3D3D3))
    )

    var anode by remember { mutableStateOf<Metal?>(null) }
    var cathode by remember { mutableStateOf<Metal?>(null) }
    var selectedMetal by remember { mutableStateOf<Metal?>(null) }
    var showPlacementMenu by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var errorCount by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    val cellE: Float? = anode?.let { a -> cathode?.let { c -> c.potential - a.potential } }
    var isAnimationRunning by remember { mutableStateOf(false) }
    var showQuestions by remember { mutableStateOf(false) }
    var questionAnswered by remember { mutableStateOf(false) }

    // State untuk pertanyaan interaktif
    var reductionAnswer by remember { mutableStateOf<String?>(null) }
    var electronAnswer by remember { mutableStateOf<String?>(null) }

    // State untuk notasi sel
    var anodeInput by remember { mutableStateOf<String?>(null) }
    var anodeIonInput by remember { mutableStateOf<String?>(null) }
    var cathodeIonInput by remember { mutableStateOf<String?>(null) }
    var cathodeInput by remember { mutableStateOf<String?>(null) }

    // State untuk dropdown menu
    var expandedAnode by remember { mutableStateOf(false) }
    var expandedAnodeIon by remember { mutableStateOf(false) }
    var expandedCathodeIon by remember { mutableStateOf(false) }
    var expandedCathode by remember { mutableStateOf(false) }

    // State untuk reaksi setengah sel
    var anodeReaction by remember { mutableStateOf("") }
    var cathodeReaction by remember { mutableStateOf("") }
    var totalReaction by remember { mutableStateOf("") }

    LaunchedEffect(cellE) {
        isAnimationRunning = cellE != null && cellE > 0
        if (isAnimationRunning) {
            delay(5000) // Tunggu animasi 5 detik
            showQuestions = true
        }
    }

    LaunchedEffect(anode, cathode) {
        errorMessage = when {
            anode == null || cathode == null -> null
            anode == cathode -> "Logam anode dan katode tidak boleh sama."
            (cathode?.potential ?: 0f) <= (anode?.potential ?: 0f) -> {
                errorCount++
                if (errorCount >= 2) "Perhatikan nilai potensialnya!" else "Katoda harus memiliki potensial lebih besar dari anoda."
            }

            else -> {
                errorCount = 0
                null
            }
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
        val btnH = maxOf(rawBtnH, 48.dp)
        val titleFont = with(density) { (screenW * 0.05f).toSp() }
        val potFont = with(density) { (screenW * 0.015f).toSp() }
        val metalFont = with(density) { (screenW * 0.01f).toSp() }
        val notationBoxWidth = screenW * 0.035f // Responsif terhadap lebar layar
        val notationFont = with(density) { (screenW * 0.012f).toSp() }

        Image(
            painter = painterResource(id = bgRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        IconButton(
            onClick = { navController.navigateUp() },
            modifier = Modifier
                .padding(pad)
                .size(32.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }

        Text(
            text = "Cara Kerja Sel Volta",
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
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(start = 10.dp, top = 10.dp)
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

                DropdownMenu(
                    expanded = showPlacementMenu,
                    onDismissRequest = { showPlacementMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Tempatkan di Anoda") },
                        onClick = {
                            if (cathode != selectedMetal) anode = selectedMetal
                            else errorMessage = "Logam sudah digunakan di katode."
                            showPlacementMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Tempatkan di Katoda") },
                        onClick = {
                            if (anode != selectedMetal) cathode = selectedMetal
                            else errorMessage = "Logam sudah digunakan di anoda."
                            showPlacementMenu = false
                        }
                    )
                }

                // Tombol Reset
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        anode = null
                        cathode = null
                        selectedMetal = null
                        errorMessage = null
                    },
                    modifier = Modifier
                        .width(btnW)
                        .height(btnH)
                ) {
                    Text("Reset", fontSize = metalFont)
                }
            }

            Spacer(Modifier.width(pad))

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
                    contentScale = ContentScale.Fit
                )

                // Jembatan Garam KNO₃
                Box(
                    Modifier
                        .offset(x = cellW * 0.46f, y = cellH * 0.38f)
                        .size(width = cellW * 0.1f, height = cellH * 0.1f)
                        .background(Color.Gray)
                ) {
                    Text("KNO₃", color = Color.White, modifier = Modifier.align(Alignment.Center))
                }

                cellE?.let { e ->
                    val animatedPotential by animateFloatAsState(
                        targetValue = e,
                        animationSpec = tween(durationMillis = 1000)
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
                }

                // Elektroda Anoda
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
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }
                }

                // Elektroda Katoda
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
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }
                }

                // Animasi Ion dari Jembatan Garam
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
                    AnodeParticleEffect(
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

                    val density = LocalDensity.current

                    SaltBridgeIonAnimation(
                        ionRes = R.drawable.ic_k, // Gambar ion K⁺
                        waypoints = listOf(
                            Offset(
                                x = with(density) { (cellW * 0.5f).toPx() },
                                y = with(density) { (cellH * 0.32f).toPx() }
                            ), // Mulai dari jembatan garam
                            Offset(
                                x = with(density) { (cellW * 0.61f).toPx() },
                                y = with(density) { (cellH * 0.32f).toPx() }
                            ),
                            Offset(
                                x = with(density) { (cellW * 0.66f).toPx() },
                                y = with(density) { (cellH * 0.7f).toPx() }
                            ) // Menuju larutan elektroda
                        ),
                        color = Color.Blue,
                        ionCount = 5,
                        delayBetweenIons = 500L
                    )

                    SaltBridgeIonAnimation(
                        ionRes = R.drawable.ic_no, // Gambar ion NO₃⁻
                        waypoints = listOf(
                            Offset(
                                x = with(density) { (cellW * 0.5f).toPx() },
                                y = with(density) { (cellH * 0.32f).toPx() }
                            ),
                            Offset(
                                x = with(density) { (cellW * 0.37f).toPx() },
                                y = with(density) { (cellH * 0.32f).toPx() }
                            ),
                            Offset(
                                x = with(density) { (cellW * 0.35f).toPx() },
                                y = with(density) { (cellH * 0.7f).toPx() }
                            )
                        ),
                        color = Color.Red,
                        ionCount = 50,
                        delayBetweenIons = 1000L
                    )
                }
            }

            Spacer(Modifier.width(pad))

            Column(
                Modifier
                    .width(screenW * 0.2f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Notasi Sel Interaktif
                Text("Notasi Sel", fontSize = notationFont, fontWeight = FontWeight.Bold)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(4.dp)
                ) {
                    // Kotak 1: Anoda
                    Box(
                        modifier = Modifier
                            .width(notationBoxWidth)
                            .height(notationBoxWidth * 0.8f)
                            .border(1.dp, Color.Gray)
                            .background(Color.White)
                    ) {
                        Text(
                            text = anodeInput ?: "Anoda",
                            color = if (anodeInput == null) Color.Gray else Color.Black,
                            fontSize = notationFont,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(2.dp)
                        )
                        DropdownMenu(
                            expanded = expandedAnode,
                            onDismissRequest = { expandedAnode = false }
                        ) {
                            metals.forEach { metal ->
                                DropdownMenuItem(
                                    text = { Text(metal.symbol, fontSize = notationFont) },
                                    onClick = {
                                        anodeInput = metal.symbol
                                        expandedAnode = false
                                    }
                                )
                            }
                        }
                        Button(
                            onClick = { expandedAnode = true },
                            modifier = Modifier.fillMaxSize(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            content = {}
                        )
                    }
                    Text("|", fontSize = notationFont)
                    // Kotak 2: Ion Anoda
                    Box(
                        modifier = Modifier
                            .width(notationBoxWidth)
                            .height(notationBoxWidth * 0.8f)
                            .border(1.dp, Color.Gray)
                            .background(Color.White)
                    ) {
                        Text(
                            text = anodeIonInput ?: "Ion",
                            color = if (anodeIonInput == null) Color.Gray else Color.Black,
                            fontSize = notationFont,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(2.dp)
                        )
                        DropdownMenu(
                            expanded = expandedAnodeIon,
                            onDismissRequest = { expandedAnodeIon = false }
                        ) {
                            metals.forEach { metal ->
                                DropdownMenuItem(
                                    text = { Text(metal.ion, fontSize = notationFont) },
                                    onClick = {
                                        anodeIonInput = metal.ion
                                        expandedAnodeIon = false
                                    }
                                )
                            }
                        }
                        Button(
                            onClick = { expandedAnodeIon = true },
                            modifier = Modifier.fillMaxSize(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            content = {}
                        )
                    }
                    Text("||", fontSize = notationFont)
                    // Kotak 3: Ion Katoda
                    Box(
                        modifier = Modifier
                            .width(notationBoxWidth)
                            .height(notationBoxWidth * 0.8f)
                            .border(1.dp, Color.Gray)
                            .background(Color.White)
                    ) {
                        Text(
                            text = cathodeIonInput ?: "Ion",
                            color = if (cathodeIonInput == null) Color.Gray else Color.Black,
                            fontSize = notationFont,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(2.dp)
                        )
                        DropdownMenu(
                            expanded = expandedCathodeIon,
                            onDismissRequest = { expandedCathodeIon = false }
                        ) {
                            metals.forEach { metal ->
                                DropdownMenuItem(
                                    text = { Text(metal.ion, fontSize = notationFont) },
                                    onClick = {
                                        cathodeIonInput = metal.ion
                                        expandedCathodeIon = false
                                    }
                                )
                            }
                        }
                        Button(
                            onClick = { expandedCathodeIon = true },
                            modifier = Modifier.fillMaxSize(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            content = {}
                        )
                    }
                    Text("|", fontSize = notationFont)
                    // Kotak 4: Katoda
                    Box(
                        modifier = Modifier
                            .width(notationBoxWidth)
                            .height(notationBoxWidth * 0.8f)
                            .border(1.dp, Color.Gray)
                            .background(Color.White)
                    ) {
                        Text(
                            text = cathodeInput ?: "Katoda",
                            color = if (cathodeInput == null) Color.Gray else Color.Black,
                            fontSize = notationFont,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(2.dp)
                        )
                        DropdownMenu(
                            expanded = expandedCathode,
                            onDismissRequest = { expandedCathode = false }
                        ) {
                            metals.forEach { metal ->
                                DropdownMenuItem(
                                    text = { Text(metal.symbol, fontSize = notationFont) },
                                    onClick = {
                                        cathodeInput = metal.symbol
                                        expandedCathode = false
                                    }
                                )
                            }
                        }
                        Button(
                            onClick = { expandedCathode = true },
                            modifier = Modifier.fillMaxSize(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            content = {}
                        )
                    }
                }
                Button(
                    onClick = {
                        val localAnode = anode
                        val localCathode = cathode
                        if (localAnode != null && localCathode != null) {
                            if (anodeInput == localAnode.symbol && anodeIonInput == localAnode.ion &&
                                cathodeIonInput == localCathode.ion && cathodeInput == localCathode.symbol
                            ) {
                                Toast.makeText(context, "Notasi Sel Benar!", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                val errorDetails = when {
                                    anodeInput != localAnode.symbol -> "Logam anoda salah."
                                    anodeIonInput != localAnode.ion -> "Ion anoda tidak sesuai dengan logam anoda."
                                    cathodeIonInput != localCathode.ion -> "Ion katoda tidak sesuai dengan logam katoda."
                                    cathodeInput != localCathode.symbol -> "Logam katoda salah."
                                    else -> "Notasi sel salah, coba lagi!"
                                }
                                Toast.makeText(context, errorDetails, Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Pilih logam untuk anoda dan katoda terlebih dahulu!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .width(btnW)
                        .height(btnH)
                        .padding(8.dp)
                ) {
                    Text("Periksa", fontSize = notationFont)
                }

                Spacer(Modifier.height(16.dp))

                // Reaksi Setengah Sel dan Total
                Text("Reaksi Setengah Sel", fontSize = notationFont, fontWeight = FontWeight.Bold)

                // Reaksi Anoda
                var expandedAnodeReaction by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .border(1.dp, Color.Gray)
                        .background(Color.White)
                        .height(32.dp)
                ) {
                    Text(
                        text = anodeReaction.ifEmpty { "Pilih reaksi anoda" },
                        color = if (anodeReaction.isEmpty()) Color.Gray else Color.Black,
                        fontSize = notationFont,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 8.dp)
                    )
                    if (anode != null && cathode != null) {
                        DropdownMenu(
                            expanded = expandedAnodeReaction,
                            onDismissRequest = { expandedAnodeReaction = false }
                        ) {
                            val anodeOptions = listOf(
                                "${anode!!.symbol} → ${anode!!.ion} + 2e⁻", // Benar
                                "${anode!!.ion} + 2e⁻ → ${anode!!.symbol}", // Salah (reduksi)
                                "${anode!!.symbol} → ${cathode!!.ion} + 2e⁻", // Salah (ion katoda)
                                "${cathode!!.symbol} → ${anode!!.ion} + 2e⁻" // Salah (logam katoda)
                            )
                            anodeOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, fontSize = notationFont) },
                                    onClick = {
                                        anodeReaction = option
                                        expandedAnodeReaction = false
                                    }
                                )
                            }
                        }
                        Button(
                            onClick = { expandedAnodeReaction = true },
                            modifier = Modifier.fillMaxSize(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            content = {}
                        )
                    }
                }

                // Reaksi Katoda
                var expandedCathodeReaction by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .border(1.dp, Color.Gray)
                        .background(Color.White)
                        .height(32.dp)
                ) {
                    Text(
                        text = cathodeReaction.ifEmpty { "Pilih reaksi katoda" },
                        color = if (cathodeReaction.isEmpty()) Color.Gray else Color.Black,
                        fontSize = notationFont,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 8.dp)
                    )
                    if (anode != null && cathode != null) {
                        DropdownMenu(
                            expanded = expandedCathodeReaction,
                            onDismissRequest = { expandedCathodeReaction = false }
                        ) {
                            val cathodeOptions = listOf(
                                "${cathode!!.ion} + 2e⁻ → ${cathode!!.symbol}", // Benar
                                "${cathode!!.symbol} → ${cathode!!.ion} + 2e⁻", // Salah (oksidasi)
                                "${anode!!.ion} + 2e⁻ → ${cathode!!.symbol}", // Salah (ion anoda)
                                "${cathode!!.ion} + 2e⁻ → ${anode!!.symbol}" // Salah (logam anoda)
                            )
                            cathodeOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, fontSize = notationFont) },
                                    onClick = {
                                        cathodeReaction = option
                                        expandedCathodeReaction = false
                                    }
                                )
                            }
                        }
                        Button(
                            onClick = { expandedCathodeReaction = true },
                            modifier = Modifier.fillMaxSize(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            content = {}
                        )
                    }
                }

                // Reaksi Total
                var expandedTotalReaction by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .border(1.dp, Color.Gray)
                        .background(Color.White)
                        .height(32.dp)
                ) {
                    Text(
                        text = totalReaction.ifEmpty { "Pilih reaksi total" },
                        color = if (totalReaction.isEmpty()) Color.Gray else Color.Black,
                        fontSize = notationFont,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 8.dp)
                    )
                    if (anode != null && cathode != null) {
                        DropdownMenu(
                            expanded = expandedTotalReaction,
                            onDismissRequest = { expandedTotalReaction = false }
                        ) {
                            val totalOptions = listOf(
                                "${anode!!.symbol} + ${cathode!!.ion} → ${anode!!.ion} + ${cathode!!.symbol}", // Benar
                                "${cathode!!.symbol} + ${anode!!.ion} → ${cathode!!.ion} + ${anode!!.symbol}", // Salah (terbalik)
                                "${anode!!.symbol} + ${anode!!.ion} → ${anode!!.ion} + ${anode!!.symbol}", // Salah (anoda saja)
                                "${cathode!!.symbol} + ${cathode!!.ion} → ${cathode!!.ion} + ${cathode!!.symbol}" // Salah (katoda saja)
                            )
                            totalOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, fontSize = notationFont) },
                                    onClick = {
                                        totalReaction = option
                                        expandedTotalReaction = false
                                    }
                                )
                            }
                        }
                        Button(
                            onClick = { expandedTotalReaction = true },
                            modifier = Modifier.fillMaxSize(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            content = {}
                        )
                    }
                }

                // Tombol Periksa
                Button(
                    onClick = {
                        val localAnode = anode
                        val localCathode = cathode
                        if (localAnode != null && localCathode != null) {
                            val correctAnode = "${localAnode.symbol} → ${localAnode.ion} + 2e⁻"
                            val correctCathode =
                                "${localCathode.ion} + 2e⁻ → ${localCathode.symbol}"
                            val correctTotal =
                                "${localAnode.symbol} + ${localCathode.ion} → ${localAnode.ion} + ${localCathode.symbol}"
                            if (anodeReaction == correctAnode && cathodeReaction == correctCathode && totalReaction == correctTotal) {
                                Toast.makeText(context, "Reaksi Benar!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Reaksi Salah, Coba Lagi!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Pilih logam untuk anoda dan katoda terlebih dahulu!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .width(btnW)
                        .height(btnH)
                        .padding(8.dp)
                ) {
                    Text("Periksa", fontSize = notationFont)
                }

                Spacer(Modifier.height(16.dp))

                // Tampilan Potensial Elektrode
                if (anode != null && cathode != null) {
                    Text("E° Anoda: %.2f V".format(anode!!.potential), fontSize = potFont)
                    Text("E° Katoda: %.2f V".format(cathode!!.potential), fontSize = potFont)
                    Text("E° Sel: %.2f V".format(cellE ?: 0f), fontSize = potFont)
                }
            }
        }

        // Pertanyaan Interaktif
        if (showQuestions && !questionAnswered) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("Pertanyaan") },
                text = {
                    Column {
                        Text("Manakah yang termasuk reaksi reduksi?")
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { reductionAnswer = cathode?.symbol },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (reductionAnswer == cathode?.symbol) Color.Green else MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(cathode?.symbol ?: "")
                            }
                            Button(
                                onClick = { reductionAnswer = anode?.symbol },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (reductionAnswer == anode?.symbol) Color.Green else MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(anode?.symbol ?: "")
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Text("Elektron akan ke arah mana?")
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val electronOptions = listOf(
                                "${anode?.symbol ?: "Anoda"} → ${cathode?.symbol ?: "Katoda"}",
                                "${cathode?.symbol ?: "Katoda"} → ${anode?.symbol ?: "Anoda"}"
                            )
                            Button(
                                onClick = { electronAnswer = electronOptions[0] },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (electronAnswer == electronOptions[0]) Color.Green else MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(electronOptions[0])
                            }
                            Button(
                                onClick = { electronAnswer = electronOptions[1] },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (electronAnswer == electronOptions[1]) Color.Green else MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(electronOptions[1])
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val correctReduction = cathode?.symbol
                        val correctElectron = "${anode?.symbol} → ${cathode?.symbol}"
                        if (reductionAnswer == correctReduction && electronAnswer == correctElectron) {
                            Toast.makeText(context, "Jawaban Benar!", Toast.LENGTH_SHORT).show()
                            questionAnswered = true
                        } else {
                            Toast.makeText(context, "Jawaban Salah, Coba Lagi!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }) {
                        Text("Submit")
                    }
                }
            )
        }

        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
}

data class Ion(
    var progress: Float = 0f,
    val waypoints: List<Offset>
)

@Composable
fun SaltBridgeIonAnimation(
    @DrawableRes ionRes: Int,
    waypoints: List<Offset>,
    color: Color,
    ionCount: Int = 5, // Jumlah maksimum ion yang ditampilkan sekaligus
    delayBetweenIons: Long = 500L // Jeda waktu antar kemunculan ion baru (ms)
) {
    val ions = remember { mutableStateListOf<Ion>() }
    val density = LocalDensity.current

    // Menambahkan ion baru secara berkala dan menghapus ion yang selesai
    LaunchedEffect(Unit) {
        while (true) {
            if (ions.size < ionCount) {
                ions.add(Ion(waypoints = waypoints))
            }
            delay(delayBetweenIons)
            ions.removeAll { it.progress >= 1f }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ions.forEach { ion ->
            val animatedProgress by animateFloatAsState(
                targetValue = ion.progress,
                animationSpec = tween(durationMillis = 2000)
            )

            // Mengupdate progres setiap ion secara independen
            LaunchedEffect(ion) {
                while (ion.progress < 1f) {
                    ion.progress += 0.01f
                    delay(16)
                }
            }

            val totalSegments = waypoints.size - 1
            val segmentProgress = animatedProgress * totalSegments
            val currentSegment = segmentProgress.toInt().coerceIn(0, totalSegments - 1)
            val segmentFraction = segmentProgress - currentSegment

            val start = waypoints[currentSegment]
            val end = waypoints[currentSegment + 1]
            val x = start.x + (end.x - start.x) * segmentFraction
            val y = start.y + (end.y - start.y) * segmentFraction

            Image(
                painter = painterResource(id = ionRes),
                contentDescription = null,
                modifier = Modifier
                    .offset(x = with(density) { x.toDp() }, y = with(density) { y.toDp() })
                    .size(20.dp)
//                    .alpha(1f - animatedProgress) // Efek transparansi opsional
            )
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
fun VoltaCellPreviews() {
    val navController = rememberNavController()
    VoltaCell(navController)
}