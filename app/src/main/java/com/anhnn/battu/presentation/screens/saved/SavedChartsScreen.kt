package com.anhnn.battu.presentation.screens.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anhnn.battu.R
import com.anhnn.battu.domain.models.Gender
import com.anhnn.battu.domain.models.SavedChart
import com.anhnn.battu.presentation.theme.AnhnnTheme
import com.anhnn.battu.presentation.viewmodels.SavedChartsViewModel

@Composable
fun SavedChartsScreen(
    onBack: () -> Unit,
    onOpenChart: (id: String) -> Unit,
    viewModel: SavedChartsViewModel = hiltViewModel()
) {
    val savedCharts by viewModel.savedCharts.collectAsStateWithLifecycle()

    SavedChartsContent(
        savedCharts = savedCharts,
        onBack = onBack,
        onOpenChart = onOpenChart,
        onDeleteChart = viewModel::onDeleteChart
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SavedChartsContent(
    savedCharts: List<SavedChart>,
    onBack: () -> Unit,
    onOpenChart: (id: String) -> Unit,
    onDeleteChart: (id: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var pendingDeleteId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.saved_charts_title),
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (savedCharts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.saved_charts_empty),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
            ) {
                items(savedCharts, key = { it.id }) { chart ->
                    SavedChartRow(
                        chart = chart,
                        onClick = { onOpenChart(chart.id) },
                        onDelete = { pendingDeleteId = chart.id }
                    )
                }
            }
        }
    }

    val deleteId = pendingDeleteId
    if (deleteId != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteId = null },
            title = { Text(stringResource(R.string.saved_charts_delete_title)) },
            text = { Text(stringResource(R.string.saved_charts_delete_message)) },
            confirmButton = {
                TextButton(onClick = {
                    pendingDeleteId = null
                    onDeleteChart(deleteId)
                }) {
                    Text(
                        stringResource(R.string.saved_charts_delete_confirm),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteId = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun SavedChartRow(
    chart: SavedChart,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.9f),
                        MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.85f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.radialGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            MaterialTheme.colorScheme.surfaceContainer
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_saved),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.size(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = chart.datetimeStr,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(
                    if (chart.gender == Gender.MALE) R.string.gender_male
                    else R.string.gender_female
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }
        IconButton(onClick = onDelete) {
            Icon(
                painter = painterResource(R.drawable.ic_trash),
                contentDescription = stringResource(R.string.saved_charts_delete_confirm),
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "Saved charts - Light")
@Composable
private fun SavedChartsLightPreview() {
    AnhnnTheme(darkTheme = false) {
        SavedChartsContent(
            savedCharts = listOf(
                SavedChart("1", "1990-05-15 14:30", Gender.MALE, 0L),
                SavedChart("2", "1995-11-02 08:00", Gender.FEMALE, 0L)
            ),
            onBack = {},
            onOpenChart = {},
            onDeleteChart = {}
        )
    }
}

@Preview(showBackground = true, name = "Saved charts - Dark", backgroundColor = 0xFF1A1A1A)
@Composable
private fun SavedChartsDarkPreview() {
    AnhnnTheme(darkTheme = true) {
        SavedChartsContent(
            savedCharts = emptyList(),
            onBack = {},
            onOpenChart = {},
            onDeleteChart = {}
        )
    }
}
