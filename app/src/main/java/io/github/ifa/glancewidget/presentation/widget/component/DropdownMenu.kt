package io.github.ifa.glancewidget.presentation.widget.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
internal fun DropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onAddWidgetClick: () -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        DropdownMenuItem(
            onClick = onAddWidgetClick,
            leadingIcon = {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = null
                )
            },
            text = {
                Text(
                    "Create widget on home screen",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        )
    }
}