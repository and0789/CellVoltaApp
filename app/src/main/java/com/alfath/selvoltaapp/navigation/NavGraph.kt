// file: navigation/NavGraph.kt
package com.alfath.selvoltaapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alfath.selvoltaapp.ui.screens.cover.CoverPage
import com.alfath.selvoltaapp.ui.screens.exercise.Exercise
import com.alfath.selvoltaapp.ui.screens.menu.MainMenu
import com.alfath.selvoltaapp.ui.screens.msds.MsdsPage
import com.alfath.selvoltaapp.ui.screens.profile.AuthorProfile
import com.alfath.selvoltaapp.ui.screens.simulation.CathodeAnode
import com.alfath.selvoltaapp.ui.screens.simulation.Electroplating
import com.alfath.selvoltaapp.ui.screens.simulation.LearningObjective
import com.alfath.selvoltaapp.ui.screens.simulation.SpontaneousReaction
import com.alfath.selvoltaapp.ui.screens.simulation.VoltaCell
import com.alfath.selvoltaapp.ui.screens.table.TableVolta

@Composable
fun SelVoltaApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "cover") {
        composable("cover") { CoverPage(navController) }
        composable("menu") { MainMenu(navController) }
        composable("profil") { AuthorProfile(navController) }
        composable("cathode") { CathodeAnode(navController) }
        composable("spontaneous") { SpontaneousReaction(navController) }
        composable("electroplating") { Electroplating(navController) }
        composable("voltaic") { VoltaCell(navController) }
        composable("volta") { TableVolta(navController) }
        composable("exercise") { Exercise(navController) }
        composable("msds") { MsdsPage(navController) }
        composable("objective") { LearningObjective(navController) }
    }
}