package com.anhnn.battu.presentation.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.anhnn.battu.presentation.screens.calendar.CalendarScreen
import com.anhnn.battu.presentation.screens.chart.ChartScreen
import com.anhnn.battu.presentation.screens.chart.SavedChartDetailScreen
import com.anhnn.battu.presentation.screens.home.HomeScreen
import com.anhnn.battu.presentation.screens.saved.SavedChartsScreen
import com.anhnn.battu.presentation.screens.settings.PrivacyPolicyScreen
import com.anhnn.battu.presentation.screens.settings.SettingsScreen
import com.anhnn.language.LanguageScreen
import kotlinx.serialization.Serializable

// Type-safe routes
@Serializable
data object HomeRoute

@Serializable
data object ChartRoute

@Serializable
data object LanguageRoute

@Serializable
data object SettingsRoute

@Serializable
data object PrivacyPolicyRoute

@Serializable
data object SavedChartsRoute

@Serializable
data object CalendarRoute

@Serializable
data class SavedChartDetailRoute(val id: String)

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
                onOpenCalendar = { navController.navigate(CalendarRoute) },
                onOpenSaved = { navController.navigate(SavedChartsRoute) },
                onOpenSettings = { navController.navigate(SettingsRoute) }
            )
        }

        composable<SettingsRoute> {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onOpenLanguage = { navController.navigate(LanguageRoute) },
                onOpenPrivacy = { navController.navigate(PrivacyPolicyRoute) },
                onOpenSaved = { navController.navigate(SavedChartsRoute) }
            )
        }

        composable<SavedChartsRoute> {
            SavedChartsScreen(
                onBack = { navController.popBackStack() },
                onOpenChart = { id -> navController.navigate(SavedChartDetailRoute(id)) }
            )
        }

        composable<SavedChartDetailRoute> {
            SavedChartDetailScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<PrivacyPolicyRoute> {
            PrivacyPolicyScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<ChartRoute> {
            ChartScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<CalendarRoute> {
            CalendarScreen(
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
