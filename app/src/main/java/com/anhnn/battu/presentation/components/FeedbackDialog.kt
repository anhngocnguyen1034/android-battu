package com.anhnn.battu.presentation.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anhnn.battu.R
import com.anhnn.battu.core.Constants
import com.anhnn.battu.presentation.viewmodels.FeedbackState
import com.anhnn.battu.presentation.viewmodels.FeedbackUiState
import com.anhnn.battu.presentation.viewmodels.FeedbackViewModel

/**
 * In-app feedback dialog: 1–5 star rating + message, sent to the backend
 * which forwards it to Discord.  Falls back to a mailto intent on error.
 *
 * @param onSent called once after a successful send (with the given rating)
 *               so the caller can chain e.g. an in-app review request.
 */
@Composable
fun FeedbackDialog(
    onDismiss: () -> Unit,
    onSent: (rating: Int?) -> Unit,
    viewModel: FeedbackViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Close the dialog as soon as the feedback is delivered
    val sendState = uiState.sendState
    LaunchedEffect(sendState) {
        if (sendState is FeedbackState.Sent) {
            onSent(sendState.rating)
            viewModel.onDismissed()
            onDismiss()
        }
    }

    FeedbackDialogContent(
        uiState = uiState,
        onRatingSelected = viewModel::onRatingSelected,
        onMessageChanged = viewModel::onMessageChanged,
        onSend = viewModel::onSend,
        onDismiss = {
            viewModel.onDismissed()
            onDismiss()
        }
    )
}

@Composable
private fun FeedbackDialogContent(
    uiState: FeedbackUiState,
    onRatingSelected: (Int) -> Unit,
    onMessageChanged: (String) -> Unit,
    onSend: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isSending = uiState.sendState == FeedbackState.Sending

    AlertDialog(
        onDismissRequest = { if (!isSending) onDismiss() },
        title = { Text(stringResource(R.string.feedback_dialog_title)) },
        text = {
            Column {
                StarRatingRow(
                    rating = uiState.rating,
                    onRatingSelected = onRatingSelected
                )
                OutlinedTextField(
                    value = uiState.message,
                    onValueChange = onMessageChanged,
                    label = { Text(stringResource(R.string.feedback_dialog_hint)) },
                    minLines = 3,
                    enabled = !isSending,
                    modifier = Modifier.fillMaxWidth()
                )
                if (uiState.sendState is FeedbackState.Error) {
                    Text(
                        text = stringResource(R.string.feedback_dialog_error),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    TextButton(
                        onClick = { sendViaEmail(context, uiState.message) }
                    ) {
                        Text(stringResource(R.string.feedback_dialog_email_fallback))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onSend, enabled = uiState.canSend) {
                if (isSending) {
                    CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.feedback_dialog_send))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSending) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun StarRatingRow(
    rating: Int?,
    onRatingSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        (1..5).forEach { star ->
            val filled = rating != null && star <= rating
            IconButton(onClick = { onRatingSelected(star) }) {
                Icon(
                    imageVector = if (filled) Icons.Filled.Star else Icons.Outlined.StarOutline,
                    contentDescription = stringResource(R.string.feedback_dialog_star, star),
                    tint = if (filled) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

/** Mailto fallback when the backend is unreachable. */
private fun sendViaEmail(context: Context, message: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(Constants.FEEDBACK_EMAIL))
        putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.feedback_subject))
        putExtra(Intent.EXTRA_TEXT, message)
    }
    runCatching { context.startActivity(Intent.createChooser(intent, null)) }
}
