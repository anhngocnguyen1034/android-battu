package com.anhnn.battu.presentation.screens.chart.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.anhnn.battu.domain.models.BaziChart
import com.anhnn.battu.presentation.theme.WuxingColors
import com.anhnn.battu.presentation.util.BaziVi
import com.anhnn.battu.presentation.util.ThanSat

/**
 * "Hồ sơ Thần sát" của một trụ — mở khi người dùng bấm vào [BaziColumn].
 *
 * Stateless: lấy danh sách Thần sát theo [pillar] từ [chart], phân loại Cát/Hung
 * qua [ThanSat]; mọi điều khiển đóng do caller quản lý qua [onDismiss] (UDF).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThanSatProfileSheet(
    pillar: PillarType,
    chart: BaziChart,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val items = chart.shenshaDetail[pillar.dataIndex].orEmpty().filter { it.isNotBlank() }

    val pillarLabel = stringResource(
        when (pillar) {
            PillarType.YEAR -> R.string.pillar_year
            PillarType.MONTH -> R.string.pillar_month
            PillarType.DAY -> R.string.pillar_day
            PillarType.HOUR -> R.string.pillar_hour
        }
    )

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.thansat_profile_title),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(R.string.pillar_label, pillarLabel),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = WuxingColors.Gold,
            )

            if (items.isEmpty()) {
                Text(
                    text = stringResource(R.string.thansat_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                items.forEach { name ->
                    ThanSatRow(name = name, isGood = ThanSat.isGood(name))
                }
            }
        }
    }
}

@Composable
private fun ThanSatRow(name: String, isGood: Boolean) {
    val dotColor = if (isGood) ThanSatGoodColor else ThanSatHungColor
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Text(
            text = BaziVi.term(name),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = stringResource(
                if (isGood) R.string.thansat_good else R.string.thansat_hung
            ),
            style = MaterialTheme.typography.labelMedium,
            color = dotColor,
        )
    }
}
