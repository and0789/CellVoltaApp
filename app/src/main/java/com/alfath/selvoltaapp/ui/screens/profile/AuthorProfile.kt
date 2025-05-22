package com.alfath.selvoltaapp.ui.screens.profile

import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
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


private data class Author(
    val name: String,
    val role: String,
    @DrawableRes val photo: Int
)

@Composable
fun AuthorProfile(
    navController: NavController,
    @DrawableRes bgRes: Int = R.drawable.content_background
) {
    val authors = listOf(
        Author("Alfath Anshorullah", "Mahasiswa Pendidikan Kimia", R.drawable.photo_user),
        Author("Dr. Cucu Zenab Subarkah, M.Pd", "Dosen Pendidikan Kimia", R.drawable.photo_user),
        Author("Imelda Helsy, M.Pd", "Dosen Pendidikan Kimia", R.drawable.photo_user)
    )

    val showExit = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as? Activity

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenW = maxWidth
        val screenH = maxHeight
        val density = LocalDensity.current

        // Dynamic sizing
        val horizontalPadding = screenW * 0.05f
        val topPadding = screenH * 0.1f
        val buttonSize = screenW * 0.05f
        val buttonPadding = screenW * 0.03f
        val titleFont = with(density) { (screenW * 0.06f).toSp() }
        val desiredCardWidth = screenW * 0.28f
        val maxCardHeight = screenH * 0.4f
        val cardSize = if (desiredCardWidth <= maxCardHeight) desiredCardWidth else maxCardHeight
        val nameFont = with(density) { (screenW * 0.018f).toSp() }
        val roleFont = with(density) { (screenW * 0.013f).toSp() }
        val rowBottomPadding = screenH * 0.05f

        // Background
        Image(
            painter = painterResource(id = bgRes),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )

        // Back button
        IconButton(
            onClick = { navController.navigateUp() },
            modifier = Modifier
                .padding(all = buttonPadding)
                .size(buttonSize)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier.size(buttonSize * 0.8f)
            )
        }

        // Title
        Text(
            text = "PROFIL PENULIS",
            fontSize = titleFont,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = topPadding)
        )

        // Author cards row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding)
                .padding(bottom = rowBottomPadding)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            authors.forEach { author ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(cardSize)
                ) {
                    // Photo Card as square
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .size(cardSize)
                    ) {
                        Image(
                            painter = painterResource(id = author.photo),
                            contentDescription = author.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = author.name,
                        fontSize = nameFont,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = author.role,
                        fontSize = roleFont,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    )
                }
            }
        }

        // Exit confirmation dialog
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
fun AuthorProfilePreview() {
    val navController = rememberNavController()
    AuthorProfile(navController)
}
