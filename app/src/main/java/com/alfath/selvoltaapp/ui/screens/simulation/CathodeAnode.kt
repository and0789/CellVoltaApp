package com.alfath.selvoltaapp.ui.screens.simulation

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.ui.draw.alpha
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
import androidx.compose.ui.unit.times
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alfath.selvoltaapp.R
import com.alfath.selvoltaapp.ui.screens.simulation.utils.isColorDark
import kotlinx.coroutines.delay
import kotlin.random.Random

data class Metal(
    val symbol: String,
    val ion: String,
    val potential: Float,
    val color: Color
)

data class Bubble(
    var progress: Float = 0f,
    val x: Float = Random.nextFloat(),
    var y: Float = 0f,
    val size: Float = Random.nextFloat() * 30 + 2,
    var attached: Boolean = false
)

@SuppressLint("ContextCastToActivity")
@Composable
fun CathodeAnode(
    navController: NavController,
    @DrawableRes bgRes: Int = R.drawable.content_background,
    @DrawableRes cellRes: Int = R.drawable.ic_cell_diagram
) {
    val metals = listOf(
        Metal("Mg", "Mg²⁺", -2.37f, Color(0xFFD3D3D3)),
        Metal("Zn", "Zn²⁺", -0.76f, Color(0xFFA9A9A9)),
        Metal("Ba", "Ba²⁺", -2.91f, Color(0xFF9ACD32)),
        Metal("Mn", "Mn²⁺", -1.18f, Color(0xFFFFC0CB)),
        Metal("Li", "Li⁺", -3.04f, Color(0xFFFF0000)),
        Metal("Pb", "Pb²⁺", -0.44f, Color(0xFF696969)),
        Metal("Cr", "Cr³⁺", 0.74f, Color(0xFF800080))
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

    LaunchedEffect(cellE) {
        isAnimationRunning = cellE != null && cellE > 0
    }

    LaunchedEffect(anode, cathode) {
        errorMessage = when {
            anode == null || cathode == null -> null
            anode == cathode -> "Logam anode dan katode tidak boleh sama. Pilih ulang logam."
            (cathode?.potential ?: 0f) <= (anode?.potential ?: 0f) -> {
                errorCount++
                if (errorCount >= 2) "Perhatikan nilai potensialnya!" else "Kombinasi tidak valid: Katoda harus memiliki potensial lebih besar dari anoda. Pilih ulang."
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
        val btnH = max(rawBtnH, 48.dp)
        val titleFont = with(density) { (screenW * 0.05f).toSp() }
        val potFont = with(density) { (screenW * 0.015f).toSp() }
        val metalFont = with(density) { (screenW * 0.01f).toSp() }

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
                .size(32.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }

        Text(
            text = "Penentuan Sel Katoda dan Anoda",
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
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent
                                ),
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
                metals.forEach { m ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${m.symbol}⇌${m.ion}",
                            modifier = Modifier.weight(1f),
                            fontSize = potFont
                        )
                        Text(
                            text = "%.2f".format(m.potential),
                            modifier = Modifier.width(48.dp),
                            textAlign = TextAlign.End,
                            fontSize = potFont
                        )
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
                    },
                    modifier = Modifier
                        .width(btnW)
                        .height(btnH)
                ) {
                    Text("RESET")
                }
            }
        }

        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
}


private fun getIconForMetal(metal: Metal?): Int {
    return when (metal?.symbol) {
        "Mg" -> R.drawable.ic_mg
        "Zn" -> R.drawable.ic_zn
        "Li" -> R.drawable.ic_li
        "Ba" -> R.drawable.ic_ba
        "Mn" -> R.drawable.ic_mn
        "Cr" -> R.drawable.ic_cr
        "Pb" -> R.drawable.ic_pb
        else -> R.drawable.ic_e
    }
}

@Composable
fun AnodeIonAnimation(
    position: Offset,
    widthPx: Float,
    heightPx: Float,
    metal: Metal?
) {
    val ions = remember { mutableStateListOf<Bubble>() }
    val density = LocalDensity.current

    val posXDp = with(density) { position.x.toDp() }
    val posYDp = with(density) { position.y.toDp() }
    val widthDp = with(density) { widthPx.toDp() }
    val heightDp = with(density) { heightPx.toDp() }

    LaunchedEffect(Unit) {
        while (true) {
            ions.add(Bubble())
            delay(200)
            ions.removeAll { it.progress >= 1f }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ions.forEach { bubble ->
            val progress by animateFloatAsState(
                targetValue = bubble.progress,
                animationSpec = tween(durationMillis = 2000)
            )
            LaunchedEffect(bubble) {
                while (bubble.progress < 1f) {
                    bubble.progress += 0.01f
                    delay(16)
                }
            }

            val x = posXDp + (bubble.x * widthDp - widthDp / 2)
            val y = posYDp + (progress * heightDp)

            Image(
                painter = painterResource(id = getIconForMetal(metal)),
                contentDescription = "Ion ${metal?.ion}",
                modifier = Modifier
                    .offset(x = x, y = y)
                    .size(bubble.size.dp)
                    .alpha(1f - progress)
            )
        }
    }
}

@Composable
fun AnodeParticleEffect(
    position: Offset,
    widthPx: Float,
    heightPx: Float,
    metal: Metal?
) {
    val particles = remember { mutableStateListOf<Bubble>() }
    val density = LocalDensity.current

    LaunchedEffect(Unit) {
        while (true) {
            particles.add(Bubble(size = Random.nextFloat() * 5 + 1))
            delay(100)
            particles.removeAll { it.progress >= 1f }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val progress by animateFloatAsState(
                targetValue = particle.progress,
                animationSpec = tween(durationMillis = 2000)
            )
            LaunchedEffect(particle) {
                while (particle.progress < 1f) {
                    particle.progress += 0.02f
                    delay(16)
                }
            }

            val x = with(density) { (position.x + (particle.x * widthPx - widthPx / 2)).toDp() }
            val y = with(density) { (position.y + (progress * heightPx)).toDp() }

            Box(
                modifier = Modifier
                    .offset(x = x, y = y)
                    .size(particle.size.dp)
                    .background(
                        metal?.color ?: Color.Gray,
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )
        }
    }
}

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun CathodeIonAnimation(
    position: Offset,
    widthPx: Float,
    heightPx: Float,
    metal: Metal?
) {
    val ions = remember { mutableStateListOf<Bubble>() }
    val metalParticles = remember { mutableStateListOf<Offset>() }
    val density = LocalDensity.current
    val maxIons = 60
    val ionsToRemove = 10

    val posXDp = with(density) { position.x.toDp() }
    val posYDp = with(density) { position.y.toDp() }
    val widthDp = with(density) { widthPx.toDp() }
    val heightDp = with(density) { heightPx.toDp() }

    LaunchedEffect(Unit) {
        while (true) {
            if (ions.size < maxIons) {
                ions.add(Bubble())
            } else {
                repeat(ionsToRemove) {
                    if (ions.isNotEmpty()) {
                        ions.removeAt(0)
                    }
                }
            }
            delay(200)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ions.forEach { bubble ->
            val progress by animateFloatAsState(
                targetValue = if (bubble.attached) 1f else bubble.progress,
                animationSpec = tween(durationMillis = 2000)
            )
            LaunchedEffect(bubble) {
                while (!bubble.attached) {
                    bubble.progress += 0.01f
                    if (bubble.progress >= 1f) {
                        bubble.attached = true
                        bubble.y = Random.nextFloat() * (heightDp.value * 1.5f)
                        val x = posXDp + (bubble.x * widthDp - widthDp / 2)
                        metalParticles.add(Offset(x.value, posYDp.value - bubble.y))
                    }
                    delay(16)
                }
            }

            val x = posXDp + (bubble.x * widthDp - widthDp / 2)
            val y = if (bubble.attached) {
                posYDp - bubble.y.dp
            } else {
                posYDp - (progress * heightDp)
            }

            Image(
                painter = painterResource(id = getIconForMetal(metal)),
                contentDescription = "Ion ${metal?.ion}",
                modifier = Modifier
                    .offset(x = x, y = y)
                    .size(bubble.size.dp)
                    .alpha(if (bubble.attached) 1f else 1f - progress)
            )

            if (!bubble.attached) {
                val animatedX by animateDpAsState(targetValue = x - 10.dp)
                val animatedY by animateDpAsState(targetValue = y + 5.dp)

                Image(
                    painter = painterResource(id = R.drawable.ic_e),
                    contentDescription = "Electron",
                    modifier = Modifier
                        .offset(x = animatedX, y = animatedY)
                        .size(16.dp)
                )
            }
        }
    }
}

@Composable
fun ElectronAnimation(
    startX: Float,
    startY: Float,
    endX: Float,
    endY: Float,
    topY: Float,
    isRunning: Boolean
) {
    val electrons = remember { mutableStateListOf<Float>() }
    val density = LocalDensity.current
    val maxAttachedElectrons = 100

    LaunchedEffect(isRunning) {
        while (isRunning) {
            if (electrons.size < maxAttachedElectrons) {
                electrons.add(0f)
            }
            delay(300)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        electrons.forEachIndexed { index, _ ->
            var progress by remember { mutableStateOf(0f) }

            LaunchedEffect(index, isRunning) {
                while (isRunning && progress < 1f) {
                    progress += 0.01f
                    delay(16)
                }
                if (isRunning) {
                    // Reset progress ke 0 agar animasi berulang
                    progress = 0f
                } else {
                    // Hapus elektron hanya jika animasi berhenti
                    electrons.removeAt(index)
                }
            }

            val (x, y) = when {
                progress < 0.3f -> {
                    val segmentProgress = progress / 0.3f
                    val yPos = startY - segmentProgress * (startY - topY)
                    Pair(startX, yPos)
                }
                progress < 0.7f -> {
                    val segmentProgress = (progress - 0.3f) / 0.4f
                    val xPos = startX + segmentProgress * (endX - startX)
                    Pair(xPos, topY)
                }
                else -> {
                    val segmentProgress = (progress - 0.7f) / 0.3f
                    val yPos = topY + segmentProgress * (endY - topY)
                    Pair(endX, yPos)
                }
            }

            Image(
                painter = painterResource(id = R.drawable.ic_e),
                contentDescription = "Electron",
                modifier = Modifier
                    .offset(x = with(density) { x.toDp() }, y = with(density) { y.toDp() })
                    .size(16.dp)
            )
        }
    }
}

@Composable
fun ZoomedDiagram(navController: NavController, anode: Metal?, cathode: Metal?, cellE: Float?) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val screenW = maxWidth
        val screenH = maxHeight
        val cellW = screenW * 0.8f
        val cellH = cellW * 0.7f
        val pad = screenW * 0.04f

        Image(
            painter = painterResource(id = R.drawable.ic_cell_diagram),
            contentDescription = null,
            modifier = Modifier
                .width(cellW)
                .height(cellH)
                .align(Alignment.Center),
            contentScale = androidx.compose.ui.layout.ContentScale.Fit
        )

        if (cellE != null && cellE > 0f) {
            ElectronAnimation(
                startX = with(LocalDensity.current) { (cellW * 0.254f + (cellW * 0.1f) / 2).toPx() },
                startY = with(LocalDensity.current) { (cellH * 0.4f).toPx() },
                endX = with(LocalDensity.current) { (cellW * 0.67f + (cellW * 0.1f) / 2).toPx() },
                endY = with(LocalDensity.current) { (cellH * 0.4f).toPx() },
                topY = with(LocalDensity.current) { (cellH * 0.1f).toPx() },
                isRunning = true
            )

            AnodeIonAnimation(
                position = Offset(
                    x = with(LocalDensity.current) { (cellW * 0.254f + (cellW * 0.1f) / 2).toPx() },
                    y = with(LocalDensity.current) { (cellH * 0.4f + cellH * 0.4f).toPx() }
                ),
                widthPx = with(LocalDensity.current) { (cellW * 0.1f).toPx() },
                heightPx = with(LocalDensity.current) { (cellH * 0.4f).toPx() },
                metal = anode
            )

            CathodeIonAnimation(
                position = Offset(
                    x = with(LocalDensity.current) { (cellW * 0.67f + (cellW * 0.1f) / 2).toPx() },
                    y = with(LocalDensity.current) { (cellH * 0.4f + cellH * 0.4f).toPx() }
                ),
                widthPx = with(LocalDensity.current) { (cellW * 0.1f).toPx() },
                heightPx = with(LocalDensity.current) { (cellH * 0.4f).toPx() },
                metal = cathode
            )
        }

        IconButton(
            onClick = { navController.navigateUp() },
            modifier = Modifier
                .padding(pad)
                .size(32.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
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
fun CathodeAnodeLandscapePreviews() {
    val navController = rememberNavController()
    CathodeAnode(navController)
}