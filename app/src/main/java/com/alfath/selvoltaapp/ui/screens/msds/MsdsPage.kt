package com.alfath.selvoltaapp.ui.screens.msds

import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alfath.selvoltaapp.R

private data class MsdsEntry(
    val no: Int,
    val nama: String,
    val sifatFisika: String,
    val sifatKimia: String,
    val bahaya: String,
    val penanggulangan: String
)

@Composable
fun MsdsPage(
    navController: NavController,
    @DrawableRes bgRes: Int = R.drawable.content_background
) {
    val entries = listOf(
        MsdsEntry(
            1,
            "Mg (Magnesium)",
            "berat molekul = 24,31 g/mol\nwarna = putih perak\ntitik leleh = 650 °C\ndensitas = 1,738 g/cm³",
            "mudah terbakar\nstabil di suhu kamar",
            "sensasi terbakar\nbatuk-batuk\niritasi mata",
            "pakai pakaian pelindung tahan nyala\nmenggunakan masker\nmemakai kacamata laboratorium"
        ),
        MsdsEntry(
            2,
            "Zn (Seng)",
            "warna = metalik abu-abu\ntitik lebur = 411 °C\ntitik didih = 908 °C\ndensitas = 7,140 g/cm³",
            "tidak mudah menyala",
            "korosi\ndapat menyebabkan iritasi kulit\ndapat menyebabkan luka bakar",
            "menggunakan pakaian laboratorium\ncuci dengan air mengalir dan sabun ke daerah luka\nbasuh dengan air mengalir sebanyak 15 menit"
        ),
        MsdsEntry(
            3,
            "Ba (Barium)",
            "warna = putih\ntitik lebur = 78 °C\ndensitas = 2,180 g/cm³",
            "larut dalam air\nph = 12,5 pada 50 g/l pada 20 °C",
            "iritasi kulit\niritasi mata\ntertelan",
            "tanggalkan pakaian terkontaminasi dan basuh dengan air\nbasuh dengan air banyak, periksa ke dokter\nberi air minum korban (maks. 2 gelas)"
        ),
        MsdsEntry(
            4,
            "Mn (Mangan)",
            "warna = abu-abu tua\ntitik didih = -\ntitik lebur = -\nsuhu penguraian = 535 °C\ndensitas = 5,21 g/cm³",
            "dapat mengoksidasi",
            "iritasi kulit\niritasi mata\ntertelan",
            "tanggalkan semua pakaian yang terkontaminasi dan basuh dengan air\nbasuhlah dengan air yang banyak dan periksalah ke dokter\nberi air minum kepada korban (paling banyak 2 gelas)"
        ),
        MsdsEntry(
            5,
            "Li (Litium)",
            "wujud = padat\nwarna = metalik\ntitik lebur = 180 °C\ntitik didih = 1342 °C\ndensitas = 0,534 g/cm³",
            "kelarutan dalam air = risiko reaksi hebat",
            "iritasi kulit\niritasi mata\ntertelan",
            "tanggalkan semua pakaian yang terkontaminasi dan basuh dengan air\nbasuhlah dengan air yang banyak dan periksalah ke dokter\nberi air minum kepada korban (paling banyak 2 gelas)"
        ),
        MsdsEntry(
            6,
            "Pb (Timbal)",
            "wujud = padat\nwarna = keputih-putihan\nbau = asam asetat lemah\ntitik didih = 75 °C\ndensitas = 2,55 g/cm³",
            "pH = 2-3 pada 50 g/l pada 20 °C",
            "iritasi kulit\niritasi mata\ntertelan",
            "tanggalkan semua pakaian yang terkontaminasi dan basuh dengan air\nbasuhlah dengan air yang banyak dan periksalah ke dokter\nberi air minum kepada korban (paling banyak 2 gelas)"
        ),
        MsdsEntry(
            7,
            "Cr (Kromium)",
            "bentuk = padat\nwarna = biru\nbau = asam nitrit\ntitik lebur = 36-37 °C",
            "sebagai pengoksidasi dengan kategori 2",
            "iritasi kulit\niritasi mata\ntertelan",
            "tanggalkan semua pakaian yang terkontaminasi dan basuh dengan air\nbasuhlah dengan air yang banyak dan periksalah ke dokter\nberi air minum kepada korban (paling banyak 2 gelas)"
        ),
        MsdsEntry(
            8,
            "Cu (Tembaga)",
            "wujud = padat\nwarna = biru\ntitik didih = -\ntitik lebur = -\ndensitas = 2,05 g/cm³ pada 20 °C",
            "",
            "iritasi kulit\niritasi mata\ntertelan",
            "tanggalkan semua pakaian yang terkontaminasi dan basuh dengan air\nbasuhlah dengan air yang banyak dan periksalah ke dokter\nberi air minum kepada korban (paling banyak 2 gelas)"
        ),
        MsdsEntry(
            9,
            "Na2SO4 (Natrium Sulfat)",
            "rumus molekul = Na2SO4\nberat molekul = 142,04 g/mol\nbentuk = kristalin\nwarna = putih\nbau = tidak berbau\ntitik didih = 108,9 °C\ndensitas = 2,70 g/cm³",
            "pH = 5,2-8,0 pada 50 g/l pada 20 °C\ntidak mudah menyala\nkelarutan dalam air = 445,5 g/l pada 20 °C\nsuhu penguraian = >890 °C",
            "iritasi kulit\niritasi mata\ntertelan",
            "tanggalkan semua pakaian yang terkontaminasi dan basuh dengan air\nbasuhlah dengan air yang banyak dan periksalah ke dokter\nberi air minum kepada korban (paling banyak 2 gelas)"
        )
    )

    val showExit = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as? Activity

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenW = maxWidth
        val screenH = maxHeight
        val density = LocalDensity.current

        val horizontalPadding = screenW * 0.05f
        val topPadding = screenH * 0.25f
        val bottomPadding = screenH * 0.1f
        val fontSize = with(density) { (screenW * 0.02f).toSp() }

        Image(
            painter = painterResource(id = bgRes),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )

        IconButton(
            onClick = { navController.navigateUp() },
            modifier = Modifier
                .padding(horizontal = horizontalPadding, vertical = topPadding * 0.1f)
                .size(screenW * 0.1f)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Kembali",
                tint = Color.Blue
            )
        }

        Text(
            text = "MSDS",
            fontSize = with(density) { (screenW * 0.05f).toSp() },
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = topPadding * 0.30f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = horizontalPadding,
                    end = horizontalPadding,
                    top = topPadding,
                    bottom = bottomPadding
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(vertical = screenH * 0.01f),
                horizontalArrangement = Arrangement.spacedBy(screenW * 0.01f)
            ) {
                Text("No", Modifier.weight(0.5f), color = Color.White, textAlign = TextAlign.Center)
                Text(
                    "Nama Bahan",
                    Modifier.weight(2f),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    "Fisika",
                    Modifier.weight(3f),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    "Kimia",
                    Modifier.weight(2f),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    "Bahaya",
                    Modifier.weight(2f),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    "Penang.",
                    Modifier.weight(3f),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(entries) { e ->
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF1F1F1))
                                .padding(vertical = screenH * 0.01f),
                            horizontalArrangement = Arrangement.spacedBy(screenW * 0.01f)
                        ) {
                            Text(
                                e.no.toString(),
                                Modifier.weight(0.5f),
                                fontSize = fontSize,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                e.nama,
                                Modifier.weight(2f),
                                fontSize = fontSize,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                e.sifatFisika,
                                Modifier.weight(3f),
                                fontSize = fontSize,
                                textAlign = TextAlign.Start
                            )
                            Text(
                                e.sifatKimia,
                                Modifier.weight(2f),
                                fontSize = fontSize,
                                textAlign = TextAlign.Start
                            )
                            Text(
                                e.bahaya,
                                Modifier.weight(2f),
                                fontSize = fontSize,
                                textAlign = TextAlign.Start
                            )
                            Text(
                                e.penanggulangan,
                                Modifier.weight(3f),
                                fontSize = fontSize,
                                textAlign = TextAlign.Start
                            )
                        }
                        HorizontalDivider(thickness = 1.dp, color = Color.Gray)
                    }
                }
            }

            if (showExit.value) {
                AlertDialog(
                    onDismissRequest = { showExit.value = false },
                    title = { Text("Konfirmasi Keluar") },
                    text = { Text("Apakah Anda yakin ingin keluar aplikasi?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showExit.value = false
                            activity?.finishAffinity()
                        }) { Text("Ya") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showExit.value = false }) { Text("Batal") }
                    }
                )
            }
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
fun MsdsPagePreview() {
    val navController = rememberNavController()
    MsdsPage(navController)
}