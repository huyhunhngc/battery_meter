package io.github.ifa.glancewidget.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.ifa.glancewidget.R

@Composable
fun CancellableFloatingActionButton(
    onApply: (Boolean) -> Unit,
) {
    Row {
        FloatingActionButton(onClick = { onApply(false) }) {
            Icon(Icons.Filled.Close, "Decline setting")
        }
        Spacer(modifier = Modifier.width(8.dp))
        ExtendedFloatingActionButton(
            onClick = { onApply(true) },
            icon = { Icon(Icons.Filled.Check, "Apply setting") },
            text = { Text(text = stringResource(id = R.string.apply)) },
        )
    }
}