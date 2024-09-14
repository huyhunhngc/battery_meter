package io.github.ifa.glancewidget.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import io.github.ifa.glancewidget.R

@Composable
fun AppAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    positiveButtonText: String = stringResource(id = R.string.confirm),
    negativeButtonText: String = stringResource(id = R.string.dismiss),
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(positiveButtonText)
            }
        },
        dismissButton = if (negativeButtonText.isNotBlank()) {
            {
                TextButton(
                    onClick = {
                        onDismissRequest()
                    }
                ) {
                    Text(negativeButtonText)
                }
            }
        } else null

    )
}