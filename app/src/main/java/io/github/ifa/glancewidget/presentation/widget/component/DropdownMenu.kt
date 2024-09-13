package io.github.ifa.glancewidget.presentation.widget.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import io.github.ifa.glancewidget.R

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
                    painter = painterResource(id = R.drawable.ic_widgets),
                    contentDescription = null
                )
            },
            trailingIcon = {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = null
                )
            },
            text = {
                Text(
                    stringResource(id = R.string.add_pinned_widget),
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            colors = MenuDefaults.itemColors().copy(
                textColor = MaterialTheme.colorScheme.primary,
                leadingIconColor = MaterialTheme.colorScheme.primary,
                trailingIconColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}