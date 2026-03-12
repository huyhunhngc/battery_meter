package io.github.ifa.glancewidget.ui.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import io.github.ifa.glancewidget.ui.theme.topBarColors
import androidx.compose.ui.unit.lerp as lerpUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedTextTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = topBarColors,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    val initialTextStyle = MaterialTheme.typography.headlineLarge.copy(
        fontWeight = FontWeight.Bold
    )
    val scrolledTextStyle = MaterialTheme.typography.titleLarge

    val fraction = scrollBehavior?.state?.collapsedFraction ?: 0f

    val textStyle = TextStyle(
        fontSize = lerpUnit(initialTextStyle.fontSize, scrolledTextStyle.fontSize, fraction),
        fontWeight = FontWeight.SemiBold,
        fontFamily = initialTextStyle.fontFamily
    )

    MediumTopAppBar(
        title = {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                style = textStyle,
            )
        },
        modifier = modifier,
        navigationIcon = navigationIcon,
        actions = actions,
        windowInsets = windowInsets,
        colors = colors,
        scrollBehavior = scrollBehavior,
    )
}