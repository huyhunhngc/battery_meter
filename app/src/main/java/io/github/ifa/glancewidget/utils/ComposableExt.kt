package io.github.ifa.glancewidget.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection

@Composable
fun combinePadding(
    topSource: PaddingValues, restSource: PaddingValues
): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current

    return PaddingValues(
        top = topSource.calculateTopPadding(),
        bottom = restSource.calculateBottomPadding(),
        start = restSource.calculateStartPadding(layoutDirection),
        end = restSource.calculateEndPadding(layoutDirection)
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun cookieShape(): Shape {
    return MaterialShapes.Cookie9Sided.toShape()
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
val singleItemListItemShapes: ListItemShapes
    @Composable get() = ListItemShapes(
        shapes.large, shapes.large, shapes.large, shapes.large, shapes.large, shapes.large
    )

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
val listItemColor
    @Composable get() = ListItemDefaults.segmentedColors(
        containerColor = MaterialTheme.colorScheme.background
    )