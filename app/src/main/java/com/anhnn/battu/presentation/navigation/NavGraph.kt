package com.anhnn.battu.presentation.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.anhnn.battu.presentation.screens.chart.ChartScreen
import com.anhnn.battu.presentation.screens.home.HomeScreen
import com.anhnn.language.LanguageScreen
import kotlinx.serialization.Serializable

// Type-safe routes
@Serializable
data object HomeRoute

@Serializable
data object ChartRoute

@Serializable
data object LanguageRoute

@Composable
fun BatTuNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HomeRoute
    ) {
        composable<HomeRoute> {
            HomeScreen(
                onOpenChart = { navController.navigate(ChartRoute) },
                onOpenLanguage = { navController.navigate(LanguageRoute) }
            )
        }

        composable<ChartRoute> {
            ChartScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<LanguageRoute> {
            val context = LocalContext.current
            LanguageScreen(
                onBack = { navController.popBackStack() },
                onLanguageSaved = {
                    // Recreate so the app reloads with the selected language
                    (context as? Activity)?.recreate()
                }
            )
        }
    }
}
