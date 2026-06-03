package com.anhnn.battu.presentation.screens.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anhnn.battu.R
import com.anhnn.battu.domain.models.BaziChart
import com.anhnn.battu.domain.models.ChartResult
import com.anhnn.battu.domain.models.Gender
import com.anhnn.battu.presentation.theme.WuxingColors
import com.anhnn.battu.presentation.util.BaziVi
import com.anhnn.battu.presentation.viewmodels.ChartState
import com.anhnn.battu.presentation.viewmodels.ChartUiState
import com.anhnn.battu.presentation.viewmodels.ChartViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun ChartScreen(
    onBack: () -> Unit,
    viewModel: ChartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ChartContent(
        uiState = uiState,
        onBack = onBack,
        onDateSelected = viewModel::onDateSelected,
        onTimeSelected = viewModel::onTimeSelected,
        onGenderSelected = viewModel::onGenderSelected,
        onCreateChart = viewModel::onCreateChart
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChartContent(
    uiState: ChartUiState,
    onBack: () -> Unit,
    onDateSelected: (Long?) -> Unit,
    onTimeSelected: (Int, Int) -> Unit,
    onGenderSelected: (Gender) -> Unit,
    onCreateChart: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.chart_title)) },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text(stringResource(R.string.back)) }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item(key = "form") {
                ChartForm(
                    uiState = uiState,
                    onShowDatePicker = { showDatePicker = true },
                    onShowTimePicker = { showTimePicker = true },
                    onGenderSelected = onGenderSelected,
                    onCreateChart = onCreateChart
                )
            }

            when (val chartState = uiState.chartState) {
                ChartState.Idle -> Unit

                ChartState.Loading -> item(key = "loading") {
                    Box(Modifier.fillMaxWidth().padding(24.dp), Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is ChartState.Error -> item(key = "error") {
                    Text(
                        text = chartState.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                is ChartState.Success -> chartResultItems(chartState.result)
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.birthDateMillis
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    showDatePicker = false
                }) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = uiState.hour,
            initialMinute = uiState.minute,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    onTimeSelected(timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            text = { TimePicker(state = timePickerState) }
        )
    }
}

// ── Input form ────────────────────────────────────────────────────

@Composable
private fun ChartForm(
    uiState: ChartUiState,
    onShowDatePicker: () -> Unit,
    onShowTimePicker: () -> Unit,
    onGenderSelected: (Gender) -> Unit,
    onCreateChart: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onShowDatePicker, modifier = Modifier.weight(1f)) {
                Text(
                    uiState.birthDateMillis?.let { formatDate(it) }
                        ?: stringResource(R.string.chart_birth_date)
                )
            }
            OutlinedButton(onClick = onShowTimePicker, modifier = Modifier.weight(1f)) {
                Text("%02d:%02d".format(Locale.US, uiState.hour, uiState.minute))
            }
        }

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            Gender.entries.forEachIndexed { index, gender ->
                SegmentedButton(
                    selected = uiState.gender == gender,
                    onClick = { onGenderSelected(gender) },
                    shape = SegmentedButtonDefaults.itemShape(index, Gender.entries.size)
                ) {
                    Text(
                        stringResource(
                            if (gender == Gender.MALE) R.string.gender_male
                            else R.string.gender_female
                        )
                    )
                }
            }
        }

        Button(
            onClick = onCreateChart,
            enabled = uiState.canSubmit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.chart_create))
        }
    }
}

// ── Result sections ───────────────────────────────────────────────

private fun LazyListScope.chartResultItems(result: ChartResult) {
    val chart = result.chart

    item(key = "header") { DayMasterHeader(chart) }
    item(key = "pillars") { FourPillars(chart) }
    item(key = "palaces") { PalaceRow(chart) }
    item(key = "wuxing") { WuxingSection(result) }

    result.geju?.let { geju ->
        item(key = "geju") { GejuSection(geju) }
    }

    if (chart.dayun.isNotEmpty()) {
        item(key = "dayun") { DayunSection(chart) }
    }

    if (chart.shensha.any { it.isNotBlank() }) {
        item(key = "shensha") { ShenshaSection(chart) }
    }

    chart.xingchong?.let { item(key = "xingchong") { XingchongSection(chart) } }
}

@Composable
private fun DayMasterHeader(chart: BaziChart) {
    val fallback = MaterialTheme.colorScheme.onSurface
    val dm = chart.dayMaster.firstOrNull()
    Column {
        Text(
            text = stringResource(R.string.chart_header_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = WuxingColors.Gold
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.section_day_master) + ": ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = chart.dayMaster,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = dm?.let { WuxingColors.charColor(it, fallback) } ?: fallback
            )
            Text(
                text = " ${BaziVi.char(dm)} · ${BaziVi.stemElement(dm)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "   " + BaziVi.gender(chart.gender),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * A single pillar slot, mapping a display position to its backend data index.
 *
 * Backend [BaziChart] arrays are indexed [Year=0, Month=1, Day=2, Hour=3].
 * Traditional Bazi is read Hour → Day → Month → Year, which on a modern
 * left-to-right screen means rendering in that exact order.
 */
private data class PillarSlot(
    val dataIndex: Int,
    val labelRes: Int,
    val subRes: Int,
    val isDayMaster: Boolean,
)

private val PILLAR_SLOTS = listOf(
    PillarSlot(3, R.string.pillar_hour, R.string.pillar_sub_hour, isDayMaster = false),   // 0 · Hour
    PillarSlot(2, R.string.pillar_day, R.string.pillar_sub_day, isDayMaster = true),      // 1 · Day (Nhật Chủ)
    PillarSlot(1, R.string.pillar_month, R.string.pillar_sub_month, isDayMaster = false), // 2 · Month
    PillarSlot(0, R.string.pillar_year, R.string.pillar_sub_year, isDayMaster = false),   // 3 · Year
)

@Composable
private fun FourPillars(chart: BaziChart) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        PILLAR_SLOTS.forEach { slot ->
            val i = slot.dataIndex
            val pillar = chart.pillars.getOrNull(i).orEmpty()
            PillarCard(
                modifier = Modifier.weight(1f),
                label = stringResource(slot.labelRes),
                sublabel = stringResource(slot.subRes),
                isDayMasterPillar = slot.isDayMaster,
                stem = pillar.getOrNull(0),
                branch = pillar.getOrNull(1),
                tenGodGan = chart.tgGan.getOrNull(i).orEmpty(),
                tenGodZhi = chart.tgZhi.getOrNull(i).orEmpty(),
                nayin = chart.nayin.getOrNull(i).orEmpty(),
                dishi = chart.dishi.getOrNull(i).orEmpty(),
                shensha = chart.shenshaDetail[i].orEmpty()
            )
        }
    }
}

@Composable
private fun PillarCard(
    label: String,
    sublabel: String,
    isDayMasterPillar: Boolean,
    stem: Char?,
    branch: Char?,
    tenGodGan: String,
    tenGodZhi: String,
    nayin: String,
    dishi: String,
    shensha: List<String>,
    modifier: Modifier = Modifier
) {
    val fallback = MaterialTheme.colorScheme.onSurface
    // Highlight the Day Pillar — it is the core (Nhật Chủ) of the whole chart.
    val containerColor =
        if (isDayMasterPillar) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        else MaterialTheme.colorScheme.surfaceVariant

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            // 1 · Pillar title
            Text(label, style = MaterialTheme.typography.labelMedium, color = WuxingColors.Gold)
            Text(
                sublabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 2 · Ten God of the Stem — Day Pillar is forced to "Nhật Chủ" (bold).
            Text(
                text = if (isDayMasterPillar) stringResource(R.string.section_day_master)
                else BaziVi.term(tenGodGan).ifBlank { " " },
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isDayMasterPillar) FontWeight.Bold else FontWeight.Normal,
                color = WuxingColors.Gold,
                textAlign = TextAlign.Center
            )

            // 3 · Heavenly Stem (Thiên Can) glyph + Hán-Việt
            GlyphWithVi(stem, fallback)

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 2.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // 4 · Earthly Branch (Địa Chi) glyph + Hán-Việt
            GlyphWithVi(branch, fallback)

            // 5 · Hidden Stems' Ten Gods (Tàng Can) — rendered BELOW the branch.
            Text(
                BaziVi.tokens(tenGodZhi).ifBlank { " " },
                style = MaterialTheme.typography.labelSmall,
                color = WuxingColors.Gold.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            // 6 · Nayin (Nạp âm) & Dishi (Địa thế)
            if (nayin.isNotBlank()) {
                Text(
                    BaziVi.term(nayin),
                    style = MaterialTheme.typography.labelSmall,
                    color = WuxingColors.Gold,
                    textAlign = TextAlign.Center
                )
            }
            if (dishi.isNotBlank()) {
                Text(
                    BaziVi.term(dishi),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 7 · Shensha (Thần sát)
            shensha.filter { it.isNotBlank() }.take(3).forEach { tag ->
                ElementChip(text = BaziVi.term(tag), color = WuxingColors.Wood)
            }
        }
    }
}

/** A large Chinese glyph (wuxing-colored) with its Hán-Việt reading below. */
@Composable
private fun GlyphWithVi(char: Char?, fallback: Color) {
    val color = char?.let { WuxingColors.charColor(it, fallback) } ?: fallback
    Text(
        text = char?.toString() ?: "—",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = color
    )
    Text(
        text = BaziVi.char(char),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Medium,
        color = color
    )
}

@Composable
private fun PalaceRow(chart: BaziChart) {
    val palaces = listOf(
        stringResource(R.string.palace_ming) to chart.minggong,
        stringResource(R.string.palace_taiyuan) to chart.taiyuan,
        stringResource(R.string.palace_shen) to chart.shengong,
        stringResource(R.string.palace_taixi) to chart.taixi,
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        palaces.forEach { (label, value) ->
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        value.ifBlank { "—" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = WuxingColors.Gold
                    )
                    if (value.isNotBlank()) {
                        Text(
                            BaziVi.translate(value),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WuxingSection(result: ChartResult) {
    // wuxing_power.power uses pure keys (金/木/…); chart.wuxing uses "金(Metal)" —
    // normalize to the leading element char so element colors resolve in both cases.
    val power = result.wuxingPower?.power?.takeIf { it.isNotEmpty() }
        ?: result.chart.wuxing.entries.associate { (k, v) -> k.take(1) to v.toDouble() }
    val order = listOf("金", "木", "水", "火", "土")
    val total = power.values.sum().takeIf { it > 0.0 } ?: 1.0
    val fallback = MaterialTheme.colorScheme.onSurface

    SectionCard(titleRes = R.string.section_wuxing) {
        order.forEach { element ->
            val value = power[element] ?: 0.0
            val fraction = (value / total).toFloat().coerceIn(0f, 1f)
            val color = WuxingColors.elementColor(element, fallback)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = Modifier.width(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        element,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                    Text(
                        BaziVi.term(element),
                        style = MaterialTheme.typography.labelSmall,
                        color = color
                    )
                }
                Box(
                    Modifier
                        .weight(1f)
                        .height(10.dp)
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(5.dp))
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(fraction)
                            .height(10.dp)
                            .background(color, RoundedCornerShape(5.dp))
                    )
                }
                Text(
                    "%.0f%%".format(fraction * 100),
                    modifier = Modifier.width(44.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        result.wuxingPower?.let { wp ->
            if (wp.strong.isNotEmpty() || wp.weak.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (wp.strong.isNotEmpty()) {
                        Text(
                            stringResource(R.string.wuxing_strong) + ":",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        wp.strong.forEach { ElementChip(BaziVi.term(it), WuxingColors.Wood) }
                    }
                    if (wp.weak.isNotEmpty()) {
                        Text(
                            stringResource(R.string.wuxing_weak) + ":",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        wp.weak.forEach { ElementChip(BaziVi.term(it), WuxingColors.Fire) }
                    }
                }
            }
        }
    }
}

@Composable
private fun GejuSection(geju: com.anhnn.battu.domain.models.Geju) {
    SectionCard(titleRes = R.string.section_geju) {
        InfoRow(stringResource(R.string.geju_name), BaziVi.translate(geju.name))
        InfoRow(stringResource(R.string.geju_type), BaziVi.translate(geju.type))
        InfoRow(stringResource(R.string.geju_strength), BaziVi.translate(geju.strength))
    }
}

@Composable
private fun DayunSection(chart: BaziChart) {
    val fallback = MaterialTheme.colorScheme.onSurface
    SectionCard(titleRes = R.string.section_dayun) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(chart.dayun, key = { it.startYear }) { dayun ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row {
                            dayun.ganzhi.forEach { ch ->
                                Text(
                                    ch.toString(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = WuxingColors.charColor(ch, fallback)
                                )
                            }
                        }
                        Text(
                            BaziVi.translate(dayun.ganzhi),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "${dayun.startYear}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            stringResource(R.string.dayun_age, dayun.startAge),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShenshaSection(chart: BaziChart) {
    SectionCard(titleRes = R.string.section_shensha) {
        FlowChips(
            chart.shensha.filter { it.isNotBlank() }.distinct().map { BaziVi.term(it) },
            WuxingColors.Wood
        )
    }
}

@Composable
private fun XingchongSection(chart: BaziChart) {
    val xc = chart.xingchong ?: return
    val groups = buildList {
        addGroup("冲", xc.chong)
        addGroup("合", xc.he)
        addGroup("刑", xc.xing)
        addGroup("害", xc.hai)
        addGroup("破", xc.po)
        addGroup("三合", xc.sanHe)
        addGroup("三会", xc.sanHui)
        addGroup("半三合", xc.banSanHe)
    }
    if (groups.isEmpty()) return
    SectionCard(titleRes = R.string.section_xingchong) {
        groups.forEach { (label, values) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    BaziVi.term(label),
                    modifier = Modifier.width(72.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = WuxingColors.Gold
                )
                Text(
                    values.joinToString("  ") { BaziVi.translate(it) },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private fun MutableList<Pair<String, List<String>>>.addGroup(label: String, values: List<String>) {
    if (values.isNotEmpty()) add(label to values)
}

// ── Shared building blocks ────────────────────────────────────────

@Composable
private fun SectionCard(titleRes: Int, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                stringResource(titleRes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = WuxingColors.Gold
            )
            content()
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(
            value.ifBlank { "—" },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ElementChip(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.15f),
        contentColor = color,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun FlowChips(items: List<String>, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row.forEach { ElementChip(it, color) }
            }
        }
    }
}

private fun formatDate(millis: Long): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.US)
        .apply { timeZone = TimeZone.getTimeZone("UTC") }
        .format(Date(millis))
