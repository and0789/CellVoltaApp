package com.alfath.selvoltaapp.ui.screens.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlin.random.Random

data class Question(
    val text: String,
    val options: List<String>,
    val correctAnswer: Int // Index jawaban benar (0 untuk A, 1 untuk B, dst.)
)

@Composable
fun Exercise(navController: NavController) {
    val questionBank = remember { generateQuestionBank() }
    val selectedQuestions = remember { mutableStateListOf<Question>() }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(30) } // 30 detik per soal
    val userAnswers = remember { mutableStateListOf<Int?>() } // Simpan jawaban pengguna
    var showResult by remember { mutableStateOf(false) }

    // Memilih 10 soal acak di awal sesi
    LaunchedEffect(Unit) {
        val shuffled = questionBank.shuffled(Random).take(10)
        selectedQuestions.addAll(shuffled)
        userAnswers.addAll(List(10) { null }) // Inisialisasi jawaban kosong
    }

    // Logika timer
    LaunchedEffect(currentQuestionIndex, timeLeft) {
        if (currentQuestionIndex < 10 && !showResult) {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            // Pindah ke soal berikutnya jika waktu habis
            if (currentQuestionIndex < 9) {
                currentQuestionIndex++
                timeLeft = 30
            } else {
                showResult = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        if (showResult) {
            ResultScreen(navController, selectedQuestions, userAnswers)
        } else if (selectedQuestions.isNotEmpty()) {
            QuestionScreen(
                question = selectedQuestions[currentQuestionIndex],
                questionNumber = currentQuestionIndex + 1,
                timeLeft = timeLeft,
                onAnswerSelected = { answerIndex ->
                    userAnswers[currentQuestionIndex] = answerIndex
                    if (currentQuestionIndex < 9) {
                        currentQuestionIndex++
                        timeLeft = 30
                    } else {
                        showResult = true
                    }
                }
            )
        } else {
            StartScreen(navController)
        }
    }
}

@Composable
fun StartScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Latihan Soal",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Siap untuk menguji pengetahuanmu? Klik 'Mulai' untuk memulai latihan!",
            fontSize = 16.sp,
            color = Color(0xFF616161),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { navController.navigate("exercise") }, // Reload untuk memulai
            modifier = Modifier
                .width(200.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Mulai", color = Color.White, fontSize = 18.sp)
        }
    }
}

@Composable
fun QuestionScreen(
    question: Question,
    questionNumber: Int,
    timeLeft: Int,
    onAnswerSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Soal $questionNumber/10",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
            Text(
                text = "Waktu: $timeLeft detik",
                fontSize = 16.sp,
                color = if (timeLeft <= 10) Color.Red else Color(0xFF616161)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Teks Soal
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = question.text,
                fontSize = 18.sp,
                color = Color(0xFF212121),
                modifier = Modifier
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Opsi Jawaban
        question.options.forEachIndexed { index, option ->
            Button(
                onClick = { onAnswerSelected(index) },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(50.dp)
                    .padding(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "${('A' + index)}. $option",
                    color = Color.White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp)) // Tambahan ruang di akhir untuk estetika
    }
}

@Composable
fun ResultScreen(
    navController: NavController,
    questions: List<Question>,
    userAnswers: List<Int?>
) {
    val correctCount =
        questions.zip(userAnswers).count { (q, a) -> a != null && a == q.correctAnswer }
    val wrongCount =
        questions.zip(userAnswers).count { (q, a) -> a != null && a != q.correctAnswer }
    val unansweredCount = userAnswers.count { it == null }
    val percentage = (correctCount * 100) / 10

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Hasil Latihan",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Skor: $percentage%",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Benar: $correctCount soal",
                    fontSize = 16.sp,
                    color = Color(0xFF616161)
                )
                Text(
                    text = "Salah: $wrongCount soal",
                    fontSize = 16.sp,
                    color = Color(0xFF616161)
                )
                Text(
                    text = "Tidak Terjawab: $unansweredCount soal",
                    fontSize = 16.sp,
                    color = Color(0xFF616161)
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { navController.navigate("exercise") }, // Ulangi latihan
                modifier = Modifier
                    .width(150.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Ulangi", color = Color.White, fontSize = 16.sp)
            }
            Button(
                onClick = { navController.navigateUp() }, // Kembali ke beranda
                modifier = Modifier
                    .width(150.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Beranda", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

fun generateQuestionBank(): List<Question> {
    return listOf(
        Question(
            text = "Pernyataan berikut yang sesuai dengan Sel Volta adalah ...",
            options = listOf(
                "Salah satu sel elektrokimia yang mengubah energi listrik menjadi energi kimia",
                "Anoda pada sel volta merupakan tempat terjadinya reaksi reduksi",
                "Reaksi berjalan tidak spontan",
                "Katoda pada sel volta bertindak sebagai kutub positif",
                "Anoda pada sel volta bertindak sebagai kutub positif"
            ),
            correctAnswer = 3 // D
        ),
        Question(
            text = "Dari pernyataan berikut, yang tidak sesuai dengan kaidah sel volta adalah ...\ni. Pada katode terjadi reaksi reduksi\nii. Pada anode terjadi reaksi oksidasi\niii. Pada sel volta, katode termasuk elektrode negatif\niv. Logam yang memiliki potensial lebih tinggi berperan sebagai elektrode positif\nv. Logam yang memiliki potensial lebih rendah berperan sebagai elektrode negatif",
            options = listOf("i", "ii", "iii", "iv", "v"),
            correctAnswer = 2 // C
        ),
        Question(
            text = "Suatu sel volta memiliki elektroda kadmium (E0Cd+2/Cd = -0,40 volt) dan platina (E0Pt+2/Pt = +1,20 Volt). Pernyataan yang benar mengenai sel volta tersebut adalah ...",
            options = listOf(
                "Notasi sel Pt/ Pt+2// Cd+2/Cd",
                "Platina bertindak sebagai anoda",
                "Elektron mengalir dari platina ke cadmium",
                "Potensial selnya sebesar 1,60 volt",
                "Reaksi sel: Cd+2 + Pt → Pt+2 + Cd"
            ),
            correctAnswer = 3 // D
        ),
        Question(
            text = "Jika diketahui bahwa logam E0 Sn = -0,14 V dan E0 Ag = +0,80 V, maka pernyataan yang tepat adalah...",
            options = listOf(
                "Perak bertindak sebagai anoda",
                "Potensial selnya adalah +0,66 V",
                "Notasi selnya adalah Ag | Ag+ || Sn2+ | Sn",
                "Elektron mengalir dari Ag ke Sn",
                "Logam Sn akan larut sedangkan logam Ag akan mengendap di katoda"
            ),
            correctAnswer = 4 // E
        ),
        Question(
            text = "Logam-logam A, B, dan C masing-masing memiliki Eo = -0,5 V ; +0,8 V ; dan -1,2 V. Pernyataan yang benar adalah...",
            options = listOf(
                "A dapat mereduksi C, tetapi tidak dapat mereduksi B",
                "B dapat mereduksi C, tetapi tidak dapat mereduksi A",
                "C dapat mereduksi A dan B",
                "C dapat mereduksi A, tetapi tidak dapat mereduksi B",
                "A dapat mereduksi B dan C"
            ),
            correctAnswer = 2 // C
        ),
        Question(
            text = "Jika diketahui:\nAg+ + e– → Ag Eo = +0,80 volt\nCu2+ + 2e– → Cu Eo = +0,34 volt\nMg2+ + 2e– → Mg Eo = -2,34 volt\nZn2+ + 2e– → Zn Eo = -0,76 volt\nAl3+ + 3e– → Al Eo = -1,66 volt\n2H+ + 2e– → H2 Eo = 0 volt\nMaka reaksi yang tidak dapat berlangsung spontan adalah...",
            options = listOf(
                "Zn + 2H+ → Zn2+ + H2",
                "3Ag + Al3+ → 3Ag+ + Al",
                "Mg + Cu2+ → Mg2+ + Cu",
                "Mg + Zn2+ → Mg2+ + Zn",
                "2Al + 6H+ → 2Al3+ + 3H2"
            ),
            correctAnswer = 1 // B
        ),
        Question(
            text = "Diketahui:\nE0cu2+/Cu = +0,34 volt\nE0Zn2+/Zn = -0,76 volt\nPernyataan yang benar mengenai sel volta yang menggunakan elektroda tembaga (Cu) dan seng (Zn) adalah...",
            options = listOf(
                "Tembaga bertindak sebagai anoda dan seng bertindak sebagai katoda",
                "Potensial selnya adalah 1,10 V",
                "Notasi selnya adalah Zn | Zn²⁺ || Cu²⁺ | Cu",
                "Elektron mengalir dari tembaga ke seng",
                "Reaksi yang terjadi pada katoda adalah Zn²⁺ + 2e⁻ → Zn"
            ),
            correctAnswer = 1 // B
        ),
        Question(
            text = "Diketahui dua logam dengan potensial reduksi standar sebagai berikut:\nCu2+ + 2e- → Cu E° = 0,3 V\nFe2+ + 2e- → Fe E° = -0,44 V\nPernyataan yang benar adalah...",
            options = listOf(
                "Potensial sel adalah 0,78 V elektron mengalir dari besi ke tembaga",
                "Potensial sel adalah 0,78 V elektron mengalir dari tembaga ke besi",
                "Potensial sel adalah 0,78 V reaksi pada katoda adalah reduksi besi",
                "Potensial sel adalah 0,78 V reaksi pada katoda adalah reduksi tembaga",
                "Potensial sel adalah 0,34 V elektron mengalir dari besi ke tembaga"
            ),
            correctAnswer = 3 // D
        ),
        Question(
            text = "Diketahui dua logam dengan potensial reduksi standar sebagai berikut:\nNi2+ + 2e- → Ni E° = -0,23 V\nPb2+ + 2e- → Pb E° = -0,13 V\nNotasi sel yang paling tepat untuk sel volta tersebut adalah...",
            options = listOf(
                "Ni | Ni²⁺ || Pb²⁺ | Pb",
                "Pb | Pb²⁺ || Ni²⁺ | Ni",
                "Ni | Ni²⁺ || Pb | Pb²⁺",
                "Pb | Pb²⁺ || Ni | Ni²⁺",
                "Ni | Ni²⁺ || Pb | Pb²⁺"
            ),
            correctAnswer = 0 // A
        ),
        Question(
            text = "Jika diketahui potensial reduksi logam sebagai berikut:\nX2+ + 2e– → X Eo = +0,47 volt\nY2+ + 2e– → Y Eo = -0,76 volt\nZ2+ + 2e– → Z Eo = -0,83 volt\nSusunan logam X, Y, dan Z dalam deret Volta berdasarkan urutan sifat reduktor yang makin kuat adalah...",
            options = listOf(
                "X-Y-Z",
                "Z-Y-X",
                "Y-Z-X",
                "Y-X-Z",
                "X-Z-Y"
            ),
            correctAnswer = 1 // B
        )
    )
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
fun ExercisePreview() {
    val navController = rememberNavController()
    Exercise(navController)
}