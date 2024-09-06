package io.github.ifa.glancewidget.presentation.widget.component

import androidx.compose.material.icons.Icons
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
    onEditTaskListClick: () -> Unit,
    onDeleteTaskListClick: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        DropdownMenuItem(
            onClick = onEditTaskListClick,
            leadingIcon = {
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = null
                )
            },
            text = {
                Text(
                    "Edit",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        )
        DropdownMenuItem(
            onClick = onDeleteTaskListClick,
            leadingIcon = {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = null
                )
            },
            text = {
                Text(
                    "Delete",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        )
    }
}