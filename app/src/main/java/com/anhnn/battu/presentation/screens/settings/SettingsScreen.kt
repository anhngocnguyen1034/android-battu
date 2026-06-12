package com.anhnn.battu.presentation.screens.settings

import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anhnn.battu.R
import com.anhnn.battu.domain.models.ThemeMode
import com.anhnn.battu.presentation.components.AnhnnThemeSwitch
import com.anhnn.battu.presentation.components.FeedbackDialog
import com.anhnn.battu.presentation.theme.AnhnnTheme
import com.anhnn.battu.presentation.viewmodels.SettingsViewModel
import com.anhnn.rate.RateDialog
import com.anhnn.rate.requestInAppReview

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenLanguage: () -> Unit,
    onOpenPrivacy: () -> Unit,
    onOpenSaved: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    SettingsContent(
        themeMode = themeMode,
        onToggleTheme = viewModel::onToggleTheme,
        onBack = onBack,
        onOpenLanguage = onOpenLanguage,
        onOpenPrivacy = onOpenPrivacy,
        onOpenSaved = onOpenSaved
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsContent(
    themeMode: ThemeMode,
    onToggleTheme: (isCurrentlyDark: Boolean) -> Unit,
    onBack: () -> Unit,
    onOpenLanguage: () -> Unit,
    onOpenPrivacy: () -> Unit,
    onOpenSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scroll = rememberScrollState()
    var showRateDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }

    val isDarkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.settings_title),
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scroll)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SectionHeader(stringResource(R.string.settings_theme_section))
            ThemeSwitchRow(
                isDark = isDarkTheme,
                onToggle = { onToggleTheme(isDarkTheme) }
            )

            Spacer(Modifier.height(8.dp))

            SectionHeader(stringResource(R.string.settings_language))
            SettingsActionRow(
                iconRes = R.drawable.ic_change_language,
                title = stringResource(R.string.settings_language),
                desc = stringResource(R.string.settings_language_desc),
                onClick = onOpenLanguage
            )

            SectionHeader(stringResource(R.string.settings_data_section))
            SettingsActionRow(
                iconRes = R.drawable.ic_saved,
                title = stringResource(R.string.settings_saved_title),
                desc = stringResource(R.string.settings_saved_desc),
                onClick = onOpenSaved
            )

            SectionHeader(stringResource(R.string.settings_about_section))
            SettingsActionRow(
                iconRes = R.drawable.ic_privacy_policy,
                title = stringResource(R.string.settings_privacy_title),
                desc = stringResource(R.string.settings_privacy_desc),
                onClick = onOpenPrivacy
            )
            SettingsActionRow(
                iconRes = R.drawable.ic_favorite,
                title = stringResource(R.string.settings_rate),
                desc = stringResource(R.string.settings_rate_desc),
                onClick = {
                    val activity = context as? Activity
                    if (activity != null) {
                        // In-app review; falls back to a Store dialog when unavailable
                        requestInAppReview(activity) { showRateDialog = true }
                    } else {
                        showRateDialog = true
                    }
                }
            )
            SettingsActionRow(
                iconRes = R.drawable.ic_feedback,
                title = stringResource(R.string.settings_feedback),
                desc = stringResource(R.string.settings_feedback_desc),
                onClick = { showFeedbackDialog = true }
            )
        }
    }

    if (showFeedbackDialog) {
        FeedbackDialog(
            onDismiss = { showFeedbackDialog = false },
            onSent = { rating ->
                // Happy users (4–5★) are invited to leave a Store review too
                if (rating != null && rating >= 4) {
                    val activity = context as? Activity
                    if (activity != null) {
                        requestInAppReview(activity) { showRateDialog = true }
                    }
                }
            }
        )
    }

    if (showRateDialog) {
        RateDialog(
            packageName = context.packageName,
            title = stringResource(R.string.rate_dialog_title),
            message = stringResource(R.string.rate_dialog_message),
            confirmText = stringResource(R.string.rate_dialog_confirm),
            dismissText = stringResource(R.string.rate_dialog_dismiss),
            onDismiss = { showRateDialog = false }
        )
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.8.sp
    )
}

@Composable
private fun settingsCardModifier(): Modifier {
    val surfaceHigh = MaterialTheme.colorScheme.surfaceContainerHigh
    val surface = MaterialTheme.colorScheme.surfaceContainer
    val primary = MaterialTheme.colorScheme.primary
    return Modifier
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
}

@Composable
private fun ThemeSwitchRow(isDark: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = settingsCardModifier()
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (isDark) stringResource(R.string.settings_theme_dark)
                else stringResource(R.string.settings_theme_light),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = if (isDark) stringResource(R.string.settings_dark_mode_desc)
                else stringResource(R.string.settings_light_mode_desc),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }
        AnhnnThemeSwitch(
            isDarkTheme = isDark,
            onToggle = onToggle
        )
    }
}

@Composable
private fun SettingsActionRow(
    @DrawableRes iconRes: Int,
    title: String,
    desc: String,
    onClick: () -> Unit
) {
    Row(
        modifier = settingsCardModifier()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
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
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.size(14.dp))
            Column {
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
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(18.dp)
                .rotate(180f)
        )
    }
}

@Preview(showBackground = true, name = "Settings - Light")
@Composable
private fun SettingsContentLightPreview() {
    AnhnnTheme(darkTheme = false) {
        SettingsContent(
            themeMode = ThemeMode.LIGHT,
            onToggleTheme = {},
            onBack = {},
            onOpenLanguage = {},
            onOpenPrivacy = {},
            onOpenSaved = {}
        )
    }
}

@Preview(showBackground = true, name = "Settings - Dark", backgroundColor = 0xFF1A1A1A)
@Composable
private fun SettingsContentDarkPreview() {
    AnhnnTheme(darkTheme = true) {
        SettingsContent(
            themeMode = ThemeMode.DARK,
            onToggleTheme = {},
            onBack = {},
            onOpenLanguage = {},
            onOpenPrivacy = {},
            onOpenSaved = {}
        )
    }
}
