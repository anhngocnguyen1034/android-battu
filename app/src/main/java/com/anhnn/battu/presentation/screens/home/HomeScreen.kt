package com.anhnn.battu.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anhnn.battu.R
import com.anhnn.battu.presentation.theme.AnhnnGradient
import com.anhnn.battu.presentation.theme.AnhnnTheme
import com.anhnn.battu.presentation.theme.WuxingColors
import com.anhnn.battu.presentation.viewmodels.HomeUiState
import com.anhnn.battu.presentation.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    onOpenChart: () -> Unit,
    onOpenCalendar: () -> Unit,
    onOpenSaved: () -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeContent(
        uiState = uiState,
        onOpenChart = onOpenChart,
        onOpenCalendar = onOpenCalendar,
        onOpenSaved = onOpenSaved,
        onOpenSettings = onOpenSettings
    )
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onOpenChart: () -> Unit,
    onOpenCalendar: () -> Unit,
    onOpenSaved: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            HeroSection(title = stringResource(R.string.home_title))

            PrimaryActionCard(
                title = stringResource(R.string.home_open_chart),
                desc = stringResource(R.string.home_cta_desc),
                icon = Icons.Filled.AutoAwesome,
                onClick = onOpenChart
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SecondaryActionCard(
                    title = stringResource(R.string.home_calendar_title),
                    desc = stringResource(R.string.home_calendar_desc),
                    icon = Icons.Filled.CalendarMonth,
                    onClick = onOpenCalendar
                )
                SecondaryActionCard(
                    title = stringResource(R.string.settings_saved_title),
                    desc = stringResource(R.string.settings_saved_desc),
                    icon = Icons.Filled.Bookmark,
                    onClick = onOpenSaved
                )
                SecondaryActionCard(
                    title = stringResource(R.string.home_open_settings),
                    desc = stringResource(R.string.home_settings_desc),
                    icon = Icons.Filled.Settings,
                    onClick = onOpenSettings
                )
            }

            Text(
                text = stringResource(R.string.home_footer),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ── Hero ──────────────────────────────────────────────────────────

/** Five Elements (Ngũ Hành) shown as a brand accent under the app name. */
private val WUXING_DOTS = listOf(
    "木" to WuxingColors.Wood,
    "火" to WuxingColors.Fire,
    "土" to WuxingColors.Earth,
    "金" to WuxingColors.Metal,
    "水" to WuxingColors.Water,
)

@Composable
private fun HeroSection(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(AnhnnGradient)
            .height(248.dp)
    ) {
        // Faint oversized glyph as a decorative watermark.
        Text(
            text = "命",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 8.dp),
            color = Color.White.copy(alpha = 0.10f),
            fontSize = 160.sp,
            fontWeight = FontWeight.Bold
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.18f))
                    .border(1.dp, Color.White.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "八字",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = title,
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.home_tagline),
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
                letterSpacing = 0.5.sp
            )

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                WUXING_DOTS.forEach { (_, color) ->
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                    )
                }
            }
        }
    }
}

// ── Action cards ──────────────────────────────────────────────────

@Composable
private fun PrimaryActionCard(
    title: String,
    desc: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(AnhnnGradient)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.20f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = desc,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 12.sp
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun SecondaryActionCard(
    title: String,
    desc: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val surfaceHigh = MaterialTheme.colorScheme.surfaceContainerHigh
    val surface = MaterialTheme.colorScheme.surfaceContainer
    val primary = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    listOf(surfaceHigh.copy(alpha = 0.9f), surface.copy(alpha = 0.85f))
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(primary.copy(alpha = 0.35f), primary.copy(alpha = 0.15f))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.radialGradient(
                        listOf(primary.copy(alpha = 0.15f), surface)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = primary,
                modifier = Modifier.size(22.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = desc,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = primary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview(showBackground = true, name = "Home - Light")
@Composable
private fun HomeContentLightPreview() {
    AnhnnTheme(darkTheme = false) {
        HomeContent(
            uiState = HomeUiState(),
            onOpenChart = {},
            onOpenCalendar = {},
            onOpenSaved = {},
            onOpenSettings = {}
        )
    }
}

@Preview(showBackground = true, name = "Home - Dark", backgroundColor = 0xFF1A1A1A)
@Composable
private fun HomeContentDarkPreview() {
    AnhnnTheme(darkTheme = true) {
        HomeContent(
            uiState = HomeUiState(),
            onOpenChart = {},
            onOpenCalendar = {},
            onOpenSaved = {},
            onOpenSettings = {}
        )
    }
}
