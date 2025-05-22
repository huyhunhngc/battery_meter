package io.github.ifa.glancewidget.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectableChips(
    modifier: Modifier = Modifier,
    options: List<String>,
    onSelect: (Int) -> Unit,
    selected: Int = 0,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
            options.forEachIndexed { index, option ->
                FilterChip(
                    selected = selected == index,
                    onClick = { onSelect(index) },
                    label = { Text(option) },
                    shape = RoundedCornerShape(50)
                )
            }
        }
    }
}

@Preview
@Composable
fun SelectableChipPreview() {
    val options = listOf("Option 1", "Option 2", "Option 3", "Option 4")
    SelectableChips(
        modifier = Modifier.width(300.dp),
        options = options,
        onSelect = {}
    )
}