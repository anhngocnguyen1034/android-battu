package com.anhnn.battu.presentation.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anhnn.battu.R
import com.anhnn.battu.domain.models.CalendarDay
import com.anhnn.battu.domain.models.CalendarEvent
import com.anhnn.battu.domain.models.CalendarMonth
import com.anhnn.battu.presentation.theme.AnhnnTheme
import com.anhnn.battu.presentation.theme.WuxingColors
import com.anhnn.battu.presentation.viewmodels.CalendarState
import com.anhnn.battu.presentation.viewmodels.CalendarViewModel
import com.anhnn.battu.presentation.viewmodels.EventViewModel
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.flowOf
import java.util.Calendar

private val Gold = WuxingColors.Gold

@Composable
fun CalendarScreen(
    onBack: () -> Unit,
    calendarVm: CalendarViewModel = hiltViewModel(),
    eventVm: EventViewModel = hiltViewModel(),
) {
    val state by calendarVm.state.collectAsStateWithLifecycle()
    val selectedDay by calendarVm.selectedDay.collectAsStateWithLifecycle()
    val currentMonth by calendarVm.currentMonth.collectAsStateWithLifecycle()
    val currentYear by calendarVm.currentYear.collectAsStateWithLifecycle()

    // Đồng bộ tháng/năm để lấy sự kiện của tháng
    LaunchedEffect(currentMonth, currentYear) {
        eventVm.setThang(currentMonth, currentYear)
    }
    val eventsThisMonth by eventVm.eventsThisMonth.collectAsStateWithLifecycle()

    val eventsForDay by remember(selectedDay) {
        val d = selectedDay
        if (d != null) eventVm.eventsForDay(d.ngayDuong, d.thangDuong, d.namDuong)
        else flowOf(emptyList())
    }.collectAsState(emptyList())

    var showAddSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        // ── Header ──────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            Text(
                stringResource(R.string.calendar_screen_title),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.size(48.dp))
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)

        // ── Month navigator ─────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(onClick = { calendarVm.prevMonth() }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = Gold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    stringResource(R.string.calendar_month_year, currentMonth, currentYear),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                )
                (state as? CalendarState.Success)?.let {
                    Text(
                        "${it.data.canChiThang} – ${it.data.canChiNam}",
                        color = Gold,
                        fontSize = 12.sp,
                    )
                }
            }
            IconButton(onClick = { calendarVm.nextMonth() }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Gold)
            }
        }

        when (val s = state) {
            is CalendarState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator(color = Gold)
            }

            is CalendarState.Error -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text(s.message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
            }

            is CalendarState.Success -> {
                val daysWithEvent = eventsThisMonth.map { it.ngayDuong }.toSet()
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    CalendarGrid(
                        data = s.data,
                        selectedDay = selectedDay,
                        daysWithEvent = daysWithEvent,
                        onDayClick = { calendarVm.selectDay(it) },
                    )
                    selectedDay?.let { day ->
                        DayDetail(
                            day = day,
                            events = eventsForDay,
                            onAddClick = { showAddSheet = true },
                            onDelete = { eventVm.deleteEvent(it) },
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }

    if (showAddSheet && selectedDay != null) {
        val day = selectedDay!!
        AddEventSheet(
            ngay = day.ngayDuong,
            thang = day.thangDuong,
            nam = day.namDuong,
            thuText = day.thu,
            onDismiss = { showAddSheet = false },
            onSave = { tieuDe, ghiChu, epoch ->
                eventVm.addEvent(tieuDe, ghiChu, day.ngayDuong, day.thangDuong, day.namDuong, epoch)
            },
        )
    }
}

@Composable
private fun CalendarGrid(
    data: CalendarMonth,
    selectedDay: CalendarDay?,
    daysWithEvent: Set<Int>,
    onDayClick: (CalendarDay) -> Unit,
) {
    val today = Calendar.getInstance()
    val isCurrentMonth = data.thang == today.get(Calendar.MONTH) + 1 &&
            data.nam == today.get(Calendar.YEAR)
    val todayNum = today.get(Calendar.DAY_OF_MONTH)

    val daysOfWeek = listOf(
        stringResource(R.string.calendar_day_sun),
        stringResource(R.string.calendar_day_mon),
        stringResource(R.string.calendar_day_tue),
        stringResource(R.string.calendar_day_wed),
        stringResource(R.string.calendar_day_thu),
        stringResource(R.string.calendar_day_fri),
        stringResource(R.string.calendar_day_sat),
    )
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
        daysOfWeek.forEachIndexed { i, label ->
            Text(
                label,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (i == 0) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }

    val firstDayOfWeek = if (data.ngay.isNotEmpty()) thuToIndex(data.ngay.first().thu) else 0
    val cells = mutableListOf<CalendarDay?>()
    repeat(firstDayOfWeek) { cells.add(null) }
    cells.addAll(data.ngay)
    while (cells.size % 7 != 0) cells.add(null)

    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceDim = MaterialTheme.colorScheme.onSurfaceVariant
    val errorColor = MaterialTheme.colorScheme.error
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary

    cells.chunked(7).forEach { row ->
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp)) {
            row.forEachIndexed { colIdx, dayInfo ->
                Box(
                    modifier = Modifier.weight(1f).aspectRatio(1f).padding(2.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    if (dayInfo != null) {
                        val isToday = isCurrentMonth && dayInfo.ngayDuong == todayNum
                        val isSelected = selectedDay != null &&
                                selectedDay.ngayDuong == dayInfo.ngayDuong &&
                                selectedDay.thangDuong == dayInfo.thangDuong
                        val hasApiEvent = dayInfo.leDuongLich != null || dayInfo.leAmLich != null
                        val hasMyEvent = dayInfo.ngayDuong in daysWithEvent
                        val isSunday = colIdx == 0

                        val bgColor = when {
                            isSelected -> primary
                            isToday -> primary.copy(alpha = 0.12f)
                            else -> Color.Transparent
                        }
                        val solarColor = when {
                            isSelected -> onPrimary
                            isSunday -> errorColor
                            else -> onSurface
                        }
                        val lunarColor = when {
                            isSelected -> onPrimary
                            hasApiEvent -> Gold
                            else -> onSurfaceDim
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(6.dp))
                                .background(bgColor)
                                .then(
                                    if (isToday && !isSelected)
                                        Modifier.border(1.dp, Gold, RoundedCornerShape(6.dp))
                                    else Modifier
                                )
                                .clickable { onDayClick(dayInfo) },
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Text(
                                    "${dayInfo.ngayDuong}",
                                    color = solarColor,
                                    fontSize = 14.sp,
                                    fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                                )
                                Text("${dayInfo.ngayAm}", color = lunarColor, fontSize = 9.sp)
                            }
                            if (hasMyEvent) {
                                Box(
                                    Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(2.dp)
                                        .size(5.dp)
                                        .background(Gold, CircleShape)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DayDetail(
    day: CalendarDay,
    events: List<CalendarEvent>,
    onAddClick: () -> Unit,
    onDelete: (CalendarEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    "${day.thu}, ${day.ngayDuong}/${day.thangDuong}/${day.namDuong}",
                    color = Gold, fontWeight = FontWeight.Bold, fontSize = 15.sp,
                )
                Text(
                    stringResource(R.string.calendar_lunar_prefix, day.amLichText) +
                            if (day.thangNhuan) " ${stringResource(R.string.calendar_intercalary)}" else "",
                    color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp,
                )
            }
            IconButton(
                onClick = onAddClick,
                modifier = Modifier.size(36.dp).background(Gold, CircleShape),
            ) {
                Icon(
                    Icons.Filled.Add,
                    stringResource(R.string.calendar_cd_add_event),
                    tint = Color.Black,
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
        DetailRow(stringResource(R.string.calendar_label_day), day.canChiNgay)
        DetailRow(stringResource(R.string.calendar_label_lunar_month), day.canChiThang)
        DetailRow(stringResource(R.string.calendar_label_lunar_year), day.canChiNam)

        if (day.truc != null || day.lucNham != null || day.gioHoangDao.isNotEmpty()) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
            day.truc?.let { truc ->
                val trucColor = if (truc.tot) Gold else MaterialTheme.colorScheme.error
                val trucLabel = if (truc.tot) stringResource(R.string.calendar_truc_good)
                else stringResource(R.string.calendar_truc_bad)
                BadgeRow(stringResource(R.string.calendar_label_truc), truc.ten, trucLabel, trucColor)
            }
            day.lucNham?.let { ln ->
                val lnColor = if (ln.hoangDao) Gold else MaterialTheme.colorScheme.error
                val lnLabel = if (ln.hoangDao) stringResource(R.string.calendar_hoang_dao)
                else stringResource(R.string.calendar_hac_dao)
                BadgeRow(stringResource(R.string.calendar_luc_nham_label), ln.ten, lnLabel, lnColor)
            }
            if (day.gioHoangDao.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    stringResource(R.string.calendar_hoang_dao_hours_label),
                    color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp,
                )
                Spacer(Modifier.height(4.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    day.gioHoangDao.forEach { gio ->
                        Box(
                            modifier = Modifier
                                .background(Gold.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                        ) {
                            Text(gio, color = Gold, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        if (!day.leDuongLich.isNullOrBlank() || !day.leAmLich.isNullOrBlank()) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
            if (!day.leDuongLich.isNullOrBlank()) {
                EventChip(day.leDuongLich, MaterialTheme.colorScheme.error)
            }
            if (!day.leAmLich.isNullOrBlank()) {
                EventChip(day.leAmLich, Gold)
            }
        }

        if (events.isNotEmpty()) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
            Text(
                stringResource(R.string.calendar_your_events),
                color = Gold, fontSize = 12.sp,
            )
            events.forEach { sk ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            if (sk.alarmEpoch > 0) {
                                Icon(
                                    Icons.Filled.Notifications, null, tint = Gold,
                                    modifier = Modifier.size(14.dp),
                                )
                            }
                            Text(
                                sk.tieuDe,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold, fontSize = 13.sp,
                            )
                        }
                        if (sk.ghiChu.isNotBlank()) {
                            Text(
                                sk.ghiChu,
                                color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp,
                            )
                        }
                        if (sk.alarmEpoch > 0) {
                            val cal = Calendar.getInstance().apply { timeInMillis = sk.alarmEpoch }
                            Text(
                                stringResource(
                                    R.string.add_event_remind_at,
                                    "%02d".format(cal.get(Calendar.HOUR_OF_DAY)),
                                    "%02d".format(cal.get(Calendar.MINUTE)),
                                ),
                                color = Gold, fontSize = 11.sp,
                            )
                        }
                    }
                    IconButton(onClick = { onDelete(sk) }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Filled.Delete,
                            stringResource(R.string.saved_charts_delete_confirm),
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BadgeRow(label: String, value: String, badge: String, badgeColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                value,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Box(
                modifier = Modifier
                    .background(badgeColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Text(badge, color = badgeColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

private val leadingEmojiRe = Regex("^[^\\p{L}\\p{N}\\p{P}]+\\s*")

@Composable
private fun EventChip(text: String, dotColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(6.dp).background(dotColor, CircleShape))
        Spacer(Modifier.width(6.dp))
        Text(leadingEmojiRe.replace(text, ""), color = dotColor, fontSize = 13.sp)
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f),
        )
        Text(
            value,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f),
        )
    }
}

private fun thuToIndex(thu: String): Int = when (thu) {
    "Chủ nhật" -> 0; "Thứ hai" -> 1; "Thứ ba" -> 2; "Thứ tư" -> 3
    "Thứ năm" -> 4; "Thứ sáu" -> 5; "Thứ bảy" -> 6; else -> 0
}

// ── Previews ────────────────────────────────────────────────────────────────

private fun sampleMonth(): CalendarMonth {
    val days = (1..30).map { d ->
        CalendarDay(
            ngayDuong = d, thangDuong = 6, namDuong = 2024,
            thu = listOf("Thứ bảy", "Chủ nhật", "Thứ hai", "Thứ ba", "Thứ tư", "Thứ năm", "Thứ sáu")[d % 7],
            jd = 2460000 + d,
            ngayAm = d, thangAm = 5, namAm = 2024, thangNhuan = false,
            amLichText = "Mùng $d, tháng 5",
            canChiNgay = "Giáp Tý", canChiThang = "Canh Ngọ", canChiNam = "Giáp Thìn",
            leDuongLich = if (d == 1) "🌷 Ngày test" else null,
        )
    }
    return CalendarMonth(6, 2024, "Canh Ngọ", "Giáp Thìn", 30, days)
}

@Preview(showBackground = true, name = "Calendar - Light")
@Composable
private fun CalendarGridLightPreview() {
    AnhnnTheme(darkTheme = false) {
        Column(Modifier.background(MaterialTheme.colorScheme.background)) {
            CalendarGrid(sampleMonth(), sampleMonth().ngay[14], setOf(3, 8), {})
        }
    }
}

@Preview(showBackground = true, name = "Calendar - Dark", backgroundColor = 0xFF1A1A1A)
@Composable
private fun CalendarGridDarkPreview() {
    AnhnnTheme(darkTheme = true) {
        Column(Modifier.background(MaterialTheme.colorScheme.background)) {
            CalendarGrid(sampleMonth(), sampleMonth().ngay[14], setOf(3, 8), {})
        }
    }
}
