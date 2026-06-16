package com.anhnn.battu.presentation.screens.calendar

import android.Manifest
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.anhnn.battu.R
import com.anhnn.battu.presentation.theme.WuxingColors
import java.util.Calendar

private val Gold = WuxingColors.Gold

/**
 * Bottom-sheet để thêm sự kiện vào một ngày.
 *
 * @param onSave (tieuDe, ghiChu, alarmEpoch) → lưu
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventSheet(
    ngay: Int,
    thang: Int,
    nam: Int,
    thuText: String,
    onDismiss: () -> Unit,
    onSave: (tieuDe: String, ghiChu: String, alarmEpoch: Long) -> Unit,
) {
    val context = LocalContext.current

    var tieuDe by remember { mutableStateOf("") }
    var ghiChu by remember { mutableStateOf("") }
    var nhacNho by remember { mutableStateOf(false) }
    var alarmHour by remember { mutableIntStateOf(8) }
    var alarmMinute by remember { mutableIntStateOf(0) }

    val hasNotifPerm = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
        } else true
    }
    var notifGranted by remember { mutableStateOf(hasNotifPerm) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                notifGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                            PackageManager.PERMISSION_GRANTED
                } else true
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val notifPermPrefs = remember {
        context.getSharedPreferences("notif_perm_prefs", Context.MODE_PRIVATE)
    }

    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        notifGranted = granted
        nhacNho = granted
        notifPermPrefs.edit().putBoolean("asked", true).apply()
        if (!granted) {
            val act = context as? Activity
            val rationale = act != null && act.shouldShowRequestPermissionRationale(
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (!rationale) showSettingsDialog = true
        }
    }

    fun requestNotifPerm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !notifGranted) {
            val act = context as? Activity
            val askedBefore = notifPermPrefs.getBoolean("asked", false)
            val rationale = act != null && act.shouldShowRequestPermissionRationale(
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (askedBefore && !rationale) {
                showSettingsDialog = true
                return
            }
            permLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            return
        }
        nhacNho = true
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                stringResource(R.string.add_event_sheet_title, ngay, thang, nam, thuText),
                color = Gold, fontWeight = FontWeight.Bold, fontSize = 16.sp,
            )

            OutlinedTextField(
                value = tieuDe,
                onValueChange = { tieuDe = it },
                label = { Text(stringResource(R.string.add_event_label_title)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors(),
            )

            OutlinedTextField(
                value = ghiChu,
                onValueChange = { ghiChu = it },
                label = { Text(stringResource(R.string.add_event_label_note)) },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors(),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .clickable { if (!nhacNho) requestNotifPerm() else nhacNho = false }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(
                        Icons.Filled.Notifications,
                        contentDescription = null,
                        tint = if (nhacNho) Gold else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        stringResource(R.string.add_event_reminder),
                        color = if (nhacNho) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                    )
                }
                Switch(
                    checked = nhacNho,
                    onCheckedChange = { on -> if (on) requestNotifPerm() else nhacNho = false },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        checkedTrackColor = Gold,
                    ),
                )
            }

            if (nhacNho) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .clickable {
                            TimePickerDialog(
                                context,
                                { _, h, m -> alarmHour = h; alarmMinute = m },
                                alarmHour, alarmMinute, true,
                            ).show()
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        stringResource(R.string.add_event_alarm_time),
                        color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp,
                    )
                    Text(
                        "%02d:%02d".format(alarmHour, alarmMinute),
                        color = Gold, fontWeight = FontWeight.SemiBold, fontSize = 16.sp,
                    )
                }
            }

            if (showSettingsDialog) {
                AlertDialog(
                    onDismissRequest = { showSettingsDialog = false },
                    title = { Text(stringResource(R.string.notif_perm_blocked_title), fontWeight = FontWeight.Bold) },
                    text = { Text(stringResource(R.string.notif_perm_blocked_message), fontSize = 14.sp) },
                    confirmButton = {
                        TextButton(onClick = {
                            showSettingsDialog = false
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        }) {
                            Text(stringResource(R.string.notif_perm_open_settings), fontWeight = FontWeight.SemiBold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showSettingsDialog = false }) {
                            Text(stringResource(R.string.cancel))
                        }
                    },
                )
            }

            Button(
                onClick = {
                    if (tieuDe.isBlank()) return@Button
                    val epoch = if (nhacNho && notifGranted) {
                        Calendar.getInstance().apply {
                            set(nam, thang - 1, ngay, alarmHour, alarmMinute, 0)
                            set(Calendar.MILLISECOND, 0)
                        }.timeInMillis
                    } else 0L
                    onSave(tieuDe, ghiChu, epoch)
                    onDismiss()
                },
                enabled = tieuDe.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    contentColor = androidx.compose.ui.graphics.Color.Black,
                ),
            ) {
                Text(stringResource(R.string.add_event_btn_save), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Gold,
    cursorColor = Gold,
)
