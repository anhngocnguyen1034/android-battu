package com.anhnn.battu.presentation.screens.chart.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anhnn.battu.R
import com.anhnn.battu.presentation.theme.WuxingColors
import com.anhnn.battu.presentation.util.BaziMeaning
import com.anhnn.battu.presentation.util.BaziVi

/**
 * Bottom sheet giải nghĩa một ô Can/Chi vừa được bấm trên [BaziGrid].
 *
 * Stateless: hiển thị nội dung tĩnh từ [BaziMeaning]; mọi điều khiển đóng/mở
 * do caller quản lý qua [onDismiss] theo mô hình UDF.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanChiInfoSheet(
    cell: ElementKey,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val meaning = BaziMeaning.of(cell.glyph)
    val fallback = MaterialTheme.colorScheme.onSurface
    val accent = cell.glyph?.let { WuxingColors.charColor(it, fallback) } ?: fallback

    val pillarLabel = stringResource(
        when (cell.pillarIndex) {
            0 -> R.string.pillar_year
            1 -> R.string.pillar_month
            2 -> R.string.pillar_day
            else -> R.string.pillar_hour
        }
    )
    val typeLabel = stringResource(
        if (cell.type == CellType.CAN) R.string.cell_heavenly_stem
        else R.string.cell_earthly_branch
    )

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Vị trí: "Thiên Can · Trụ Ngày"
            Text(
                text = "$typeLabel · ${stringResource(R.string.pillar_label, pillarLabel)}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            // Header: glyph lớn tô màu Ngũ Hành + tên Hán-Việt
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(accent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = (cell.glyph?.toString()).orEmpty().ifBlank { "—" },
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = accent,
                    )
                }
                Text(
                    text = meaning?.viName ?: BaziVi.char(cell.glyph).ifBlank { "—" },
                    modifier = Modifier.padding(start = 16.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
            }

            if (meaning != null) {
                // Chips: Ngũ Hành · Âm/Dương · Con giáp/Giờ
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoChip(
                        text = stringResource(R.string.cell_element, meaning.element),
                        color = accent,
                    )
                    InfoChip(text = meaning.yinYang, color = accent)
                }
                Text(
                    text = meaning.image,
                    style = MaterialTheme.typography.titleSmall,
                    color = WuxingColors.Gold,
                )

                Text(
                    text = meaning.detail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            } else {
                Text(
                    text = stringResource(R.string.cell_no_info),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InfoChip(text: String, color: androidx.compose.ui.graphics.Color) {
    AssistChip(
        onClick = {},
        enabled = false,
        label = { Text(text) },
        colors = AssistChipDefaults.assistChipColors(
            disabledLabelColor = color,
            disabledContainerColor = color.copy(alpha = 0.12f),
        ),
        border = null,
    )
}
