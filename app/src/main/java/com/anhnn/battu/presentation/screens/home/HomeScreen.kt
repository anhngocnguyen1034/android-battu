package com.anhnn.battu.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anhnn.battu.R
import com.anhnn.battu.presentation.theme.AnhnnTheme
import com.anhnn.battu.presentation.viewmodels.HomeUiState
import com.anhnn.battu.presentation.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    onOpenChart: () -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeContent(
        uiState = uiState,
        onOpenChart = onOpenChart,
        onOpenSettings = onOpenSettings
    )
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onOpenChart: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = uiState.title,
                style = MaterialTheme.typography.headlineMedium
            )
            Button(onClick = onOpenChart) {
                Text(text = stringResource(R.string.home_open_chart))
            }
            OutlinedButton(onClick = onOpenSettings) {
                Text(text = stringResource(R.string.home_open_settings))
            }
        }
    }
}

@Preview(showBackground = true, name = "Home - Light")
@Composable
private fun HomeContentLightPreview() {
    AnhnnTheme(darkTheme = false) {
        HomeContent(uiState = HomeUiState(), onOpenChart = {}, onOpenSettings = {})
    }
}

@Preview(showBackground = true, name = "Home - Dark")
@Composable
private fun HomeContentDarkPreview() {
    AnhnnTheme(darkTheme = true) {
        HomeContent(uiState = HomeUiState(), onOpenChart = {}, onOpenSettings = {})
    }
}
