package com.anhnn.battu

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.anhnn.battu.presentation.navigation.BatTuNavGraph
import com.anhnn.battu.presentation.theme.AnhnnTheme
import com.anhnn.language.LanguageDataSource
import com.anhnn.language.LanguageManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        // Apply saved language before the Activity is created (anhnn-language)
        val code = runBlocking { LanguageDataSource(newBase).languageCode.first() }
        super.attachBaseContext(LanguageManager.setLanguage(newBase, code))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnhnnTheme {
                BatTuNavGraph()
            }
        }
    }
}
