package io.github.ifa.glancewidget.features.battery.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.ifa.glancewidget.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun DropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onForceReloadClick: () -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        shape = MaterialTheme.shapes.largeIncreased,
        containerColor = MaterialTheme.colorScheme.background,
        shadowElevation = 16.dp,
        tonalElevation = 10.dp
    ) {
        DropdownMenuItem(
            onClick = onForceReloadClick,
            leadingIcon = {
                Icon(
                    Icons.Rounded.Refresh, contentDescription = null
                )
            },
            text = {
                Text(
                    stringResource(id = R.string.force_reload),
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            colors = MenuDefaults.itemColors().copy(
                textColor = MaterialTheme.colorScheme.primary,
                leadingIconColor = MaterialTheme.colorScheme.primary,
                trailingIconColor = MaterialTheme.colorScheme.primary
            ),
        )
        DropdownMenuItem(
            onClick = {},
            leadingIcon = {
                Icon(
                    Icons.Rounded.BugReport, contentDescription = null
                )
            },
            text = {
                Text(
                    stringResource(id = R.string.bug_report),
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            colors = MenuDefaults.itemColors().copy(
                textColor = MaterialTheme.colorScheme.primary,
                leadingIconColor = MaterialTheme.colorScheme.primary,
                trailingIconColor = MaterialTheme.colorScheme.primary
            ),
        )
    }
}