package com.anhnn.battu.presentation.screens.chart.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anhnn.battu.R
import com.anhnn.battu.domain.models.BaziChart
import com.anhnn.battu.presentation.theme.AnhnnGradient
import com.anhnn.battu.presentation.theme.AnhnnTheme
import com.anhnn.battu.presentation.theme.WuxingColors
import com.anhnn.battu.presentation.util.BaziVi
import com.anhnn.battu.presentation.util.ThanSat

/**
 * Bảng Tứ Trụ (Four Pillars Grid).
 *
 * Hiển thị 4 cột theo bố cục truyền thống đọc từ PHẢI qua TRÁI
 * ([Năm] → [Tháng] → [Ngày] → [Giờ]). Trên màn hình hiện đại đọc trái→phải,
 * điều đó tương đương render theo thứ tự Giờ → Ngày → Tháng → Năm.
 *
 * Stateless theo mô hình UDF: mọi tương tác bấm vào ô Can/Chi được đẩy lên
 * trên qua [onCellClick]. Component không giữ state nội bộ.
 */

/** Loại ô được bấm trong một trụ. */
enum class CellType { CAN, CHI }

/** Bốn trụ, kèm index dữ liệu backend [Năm=0, Tháng=1, Ngày=2, Giờ=3]. */
enum class PillarType(val dataIndex: Int) {
    YEAR(0), MONTH(1), DAY(2), HOUR(3);

    companion object {
        fun fromIndex(index: Int): PillarType = entries.firstOrNull { it.dataIndex == index } ?: HOUR
    }
}

/** Một Thần sát của trụ, kèm phân loại Cát (isGood) / Hung. */
@Immutable
data class ThanSatState(
    /** Tên Thần sát (chữ Hán thô, dịch khi hiển thị qua BaziVi). */
    val name: String,
    /** true = Cát tinh, false = Hung tinh. */
    val isGood: Boolean,
)

/** Màu chấm chỉ báo Thần sát — dịu để không lấn át Can Chi. */
internal val ThanSatGoodColor = Color(0xFF66BB6A) // xanh lá dịu (Cát)
internal val ThanSatHungColor = Color(0xFFE57373) // đỏ dịu (Hung)

/** Khóa nhận diện ô Can/Chi được bấm, đẩy lên trên để xem giải nghĩa. */
@Immutable
data class ElementKey(
    /** Index trụ theo backend [Năm=0, Tháng=1, Ngày=2, Giờ=3]. */
    val pillarIndex: Int,
    val type: CellType,
    /** Glyph Hán đã bấm (vd '庚'), null nếu thiếu dữ liệu. */
    val glyph: Char?,
)

/** Dữ liệu hiển thị cho một cột (trụ) — Thiên Can, Địa Chi, Thập Thần, Tàng Can. */
@Immutable
data class BaziColumnState(
    /** Index trụ theo backend [Năm=0, Tháng=1, Ngày=2, Giờ=3]. */
    val dataIndex: Int,
    /** Tiêu đề trụ (Năm/Tháng/Ngày/Giờ), đã resolve sang ngôn ngữ hiện tại. */
    val title: String,
    /** Phụ đề trụ (Tổ tiên/Cha mẹ/Bản thân/Con cái). */
    val subtitle: String,
    /** Thiên Can (glyph Hán). */
    val stem: Char?,
    /** Địa Chi (glyph Hán). */
    val branch: Char?,
    /** Thập Thần thiên can (chuỗi Hán thô, dịch khi render). */
    val tenGodGan: String,
    /** Thập Thần tàng can địa chi (các token Hán cách nhau bởi khoảng trắng). */
    val hiddenStems: String,
    /** Nạp âm (chuỗi Hán thô, vd 海中金 → "Hải Trung Kim"). */
    val nayin: String,
    /** Địa thế / Thập nhị trường sinh (vd 长生 → "Trường Sinh"). */
    val dishi: String,
    /** Thần sát của trụ, đã phân loại Cát/Hung — render thành chấm chỉ báo. */
    val thanSatList: List<ThanSatState>,
    /** Trụ Ngày (Nhật Chủ) — trung tâm lá số, được làm nổi bật bằng viền gradient. */
    val isDayMaster: Boolean,
) {
    companion object {
        /**
         * Map [BaziChart] sang danh sách cột theo thứ tự hiển thị
         * (Giờ → Ngày → Tháng → Năm).
         */
        fun listFrom(
            chart: BaziChart,
            title: (dataIndex: Int) -> String,
            subtitle: (dataIndex: Int) -> String,
        ): List<BaziColumnState> = DISPLAY_ORDER.map { i ->
            val pillar = chart.pillars.getOrNull(i).orEmpty()
            BaziColumnState(
                dataIndex = i,
                title = title(i),
                subtitle = subtitle(i),
                stem = pillar.getOrNull(0),
                branch = pillar.getOrNull(1),
                tenGodGan = chart.tgGan.getOrNull(i).orEmpty(),
                hiddenStems = chart.tgZhi.getOrNull(i).orEmpty(),
                nayin = chart.nayin.getOrNull(i).orEmpty(),
                dishi = chart.dishi.getOrNull(i).orEmpty(),
                thanSatList = chart.shenshaDetail[i].orEmpty()
                    .filter { it.isNotBlank() }
                    .map { ThanSatState(name = it, isGood = ThanSat.isGood(it)) },
                isDayMaster = i == DAY_PILLAR_INDEX,
            )
        }

        /** Backend index của trụ Ngày (Nhật Chủ). */
        private const val DAY_PILLAR_INDEX = 2

        /** Thứ tự render trái→phải = đọc phải→trái: Giờ, Ngày, Tháng, Năm. */
        private val DISPLAY_ORDER = listOf(3, 2, 1, 0)
    }
}

/** Overload tiện dụng: dựng grid trực tiếp từ [BaziChart]. */
@Composable
fun BaziGrid(
    chart: BaziChart,
    onCellClick: (ElementKey) -> Unit,
    onShenshaClick: (PillarType) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val titles = mapOf(
        0 to stringResource(R.string.pillar_year),
        1 to stringResource(R.string.pillar_month),
        2 to stringResource(R.string.pillar_day),
        3 to stringResource(R.string.pillar_hour),
    )
    val subtitles = mapOf(
        0 to stringResource(R.string.pillar_sub_year),
        1 to stringResource(R.string.pillar_sub_month),
        2 to stringResource(R.string.pillar_sub_day),
        3 to stringResource(R.string.pillar_sub_hour),
    )
    val dayMasterLabel = stringResource(R.string.section_day_master)
    val columns = BaziColumnState.listFrom(
        chart = chart,
        title = { titles[it].orEmpty() },
        subtitle = { subtitles[it].orEmpty() },
    )
    BaziGrid(
        columns = columns,
        dayMasterLabel = dayMasterLabel,
        onCellClick = onCellClick,
        onShenshaClick = onShenshaClick,
        modifier = modifier,
    )
}

/**
 * Bảng Tứ Trụ — stateless.
 *
 * @param columns 4 cột theo đúng thứ tự hiển thị (trái→phải).
 * @param dayMasterLabel nhãn "Nhật Chủ" hiển thị thay Thập Thần cho trụ Ngày.
 */
@Composable
fun BaziGrid(
    columns: List<BaziColumnState>,
    dayMasterLabel: String,
    onCellClick: (ElementKey) -> Unit,
    onShenshaClick: (PillarType) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.Top,
    ) {
        columns.forEach { state ->
            BaziColumn(
                state = state,
                dayMasterLabel = dayMasterLabel,
                onCellClick = onCellClick,
                onShenshaClick = onShenshaClick,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/** Một trụ (cột): tiêu đề → ô Thiên Can → ô Địa Chi → Thập Thần → Nạp âm → Thần sát. */
@Composable
private fun BaziColumn(
    state: BaziColumnState,
    dayMasterLabel: String,
    onCellClick: (ElementKey) -> Unit,
    onShenshaClick: (PillarType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(14.dp)
    // Trụ Ngày (Nhật Chủ) là trung tâm lá số → bao quanh bằng viền gradient Anhnn.
    // Bản thân trụ KHÔNG bắt click: chỉ ô Can/Chi và "thẻ Thần sát" ở chân trụ
    // mới là vùng chạm, tránh xung đột thao tác.
    val containerModifier = modifier
        .clip(shape)
        .then(
            if (state.isDayMaster) Modifier.border(width = 2.dp, brush = AnhnnGradient, shape = shape)
            else Modifier
        )
        .background(MaterialTheme.colorScheme.surface)
        .padding(horizontal = 4.dp, vertical = 12.dp)

    Column(
        modifier = containerModifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        // 1 · Tiêu đề trụ
        Text(
            text = state.title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = WuxingColors.Gold,
        )
        Text(
            text = state.subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        // 2 · Thập Thần của Thiên Can — trụ Ngày hiển thị "Nhật Chủ" (đậm).
        Text(
            text = if (state.isDayMaster) dayMasterLabel
            else BaziVi.term(state.tenGodGan).ifBlank { " " },
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (state.isDayMaster) FontWeight.Bold else FontWeight.Normal,
            color = if (state.isDayMaster) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        // 3 · Ô Thiên Can (Can) — trên
        CanChiCell(
            glyph = state.stem,
            onClick = { onCellClick(ElementKey(state.dataIndex, CellType.CAN, state.stem)) },
        )

        // 4 · Ô Địa Chi (Chi) — dưới
        CanChiCell(
            glyph = state.branch,
            onClick = { onCellClick(ElementKey(state.dataIndex, CellType.CHI, state.branch)) },
        )

        // 5 · Tàng Can (Thập Thần của địa chi) — danh sách nhỏ xếp dọc dưới cùng.
        val hidden = BaziVi.tokens(state.hiddenStems)
        if (hidden.isNotBlank()) {
            Text(
                text = hidden,
                style = MaterialTheme.typography.labelSmall,
                color = WuxingColors.Gold.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
            )
        }

        // 6 · Nạp âm & Địa thế — micro-text xám mờ, lùi xuống làm nền phụ
        // để Can Chi tỏa sáng (không tô màu Ngũ Hành rực ở đây).
        val muted = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
        if (state.nayin.isNotBlank()) {
            MicroText(text = BaziVi.term(state.nayin), color = muted)
        }
        if (state.dishi.isNotBlank()) {
            MicroText(text = BaziVi.term(state.dishi), color = muted)
        }

        // 7 · "Thẻ Thần sát" — vùng chạm riêng ở chân trụ, mở Hồ sơ Thần sát.
        ThanSatBar(
            items = state.thanSatList,
            onClick = { onShenshaClick(PillarType.fromIndex(state.dataIndex)) },
        )
    }
}

/** Chiều cao cố định của thẻ Thần sát — giữ 4 trụ thẳng hàng dù rỗng hay không. */
private val ThanSatBarMinHeight = 32.dp

/**
 * Thẻ chạm ở chân trụ bọc cụm chấm Indicator Dots (Cát xanh / Hung đỏ).
 *
 * - Có Thần sát: nền mờ nhẹ + bấm được để mở Hồ sơ Thần sát của trụ.
 * - Rỗng: giữ một [Spacer] cùng chiều cao (không nền, không bấm) để các trụ cân nhau.
 *
 * Modifier theo thứ tự chuẩn: size → clip → background → clickable → padding.
 */
@Composable
private fun ThanSatBar(
    items: List<ThanSatState>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (items.isEmpty()) {
        Spacer(modifier.height(ThanSatBarMinHeight))
        return
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = ThanSatBarMinHeight)
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f))
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEach { ts ->
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (ts.isGood) ThanSatGoodColor else ThanSatHungColor)
            )
        }
    }
}

/** Chữ siêu nhỏ, màu mờ — dùng cho thông tin phụ (Nạp âm, Địa thế). */
@Composable
private fun MicroText(text: String, color: Color, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        textAlign = TextAlign.Center,
    )
}

/**
 * Ô hiển thị một Can hoặc Chi: nền fill màu Ngũ Hành tương ứng với alpha 0.15,
 * glyph dùng màu đậm của hành đó. Bấm vào ô đẩy sự kiện lên trên.
 *
 * Modifier theo thứ tự chuẩn dự án: size → clip → background → clickable → padding.
 */
@Composable
private fun CanChiCell(
    glyph: Char?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val fallback = MaterialTheme.colorScheme.onSurface
    val elementColor = glyph?.let { WuxingColors.charColor(it, fallback) } ?: fallback
    val shape = RoundedCornerShape(10.dp)

    Text(
        text = BaziVi.char(glyph).ifBlank { "—" },
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(elementColor.copy(alpha = 0.15f))
            .clickable(enabled = glyph != null, onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 2.dp),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = elementColor,
        textAlign = TextAlign.Center,
    )
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

private val PREVIEW_COLUMNS = listOf(
    // Giờ
    BaziColumnState(3, "Giờ", "Con cái", '庚', '辰', "偏财", "戊 乙 癸",
        nayin = "白蜡金", dishi = "养",
        thanSatList = listOf(ThanSatState("天乙贵人", true), ThanSatState("劫煞", false)),
        isDayMaster = false),
    // Ngày (Nhật Chủ)
    BaziColumnState(2, "Ngày", "Bản thân", '丙', '午', "日主", "丁 己",
        nayin = "天河水", dishi = "帝旺",
        thanSatList = listOf(ThanSatState("羊刃", false)),
        isDayMaster = true),
    // Tháng
    BaziColumnState(1, "Tháng", "Cha mẹ", '壬', '子', "七杀", "癸",
        nayin = "桑柘木", dishi = "胎",
        thanSatList = emptyList(),
        isDayMaster = false),
    // Năm
    BaziColumnState(0, "Năm", "Tổ tiên", '甲', '寅', "偏印", "甲 丙 戊",
        nayin = "大溪水", dishi = "长生",
        thanSatList = listOf(ThanSatState("文昌", true), ThanSatState("将星", true), ThanSatState("桃花", false)),
        isDayMaster = false),
)

@Preview(name = "BaziGrid · Light", showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
private fun BaziGridLightPreview() {
    AnhnnTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            BaziGrid(
                columns = PREVIEW_COLUMNS,
                dayMasterLabel = "Nhật Chủ",
                onCellClick = {},
                modifier = Modifier.padding(12.dp),
            )
        }
    }
}

@Preview(name = "BaziGrid · Dark", showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun BaziGridDarkPreview() {
    AnhnnTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            BaziGrid(
                columns = PREVIEW_COLUMNS,
                dayMasterLabel = "Nhật Chủ",
                onCellClick = {},
                modifier = Modifier.padding(12.dp),
            )
        }
    }
}
