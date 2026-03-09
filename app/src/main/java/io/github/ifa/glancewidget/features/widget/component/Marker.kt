package io.github.ifa.glancewidget.features.widget.component

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.Layout
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.dimensions
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.HorizontalDimensions
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.LayeredComponent
import com.patrykandpatrick.vico.core.common.component.Shadow
import com.patrykandpatrick.vico.core.common.component.ShapeComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.copyColor
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import io.github.ifa.glancewidget.R

@Composable
internal fun rememberMarker(): CartesianMarker {
    val typeface = LocalContext.current.resources.getFont(R.font.googlesansregular)
    val label = rememberTextComponent(
        typeface = Typeface.create(typeface, Typeface.BOLD),
        color = MaterialTheme.colorScheme.primary,
        textAlignment = Layout.Alignment.ALIGN_CENTER,
        padding = dimensions(8.dp, 4.dp),
        minWidth = TextComponent.MinWidth.fixed(40f),
    )
    val indicatorFrontComponent =
        rememberShapeComponent(MaterialTheme.colorScheme.tertiary, CorneredShape.Pill)
    return remember(label) {
        Marker(
            label = label,
            indicatorFrontComponent = indicatorFrontComponent,
        )
    }
}

@SuppressLint("RestrictedApi")
class Marker(
    label: TextComponent,
    labelPosition: LabelPosition = LabelPosition.AbovePoint,
    indicatorFrontComponent: ShapeComponent,
) : DefaultCartesianMarker(
    label = label,
    labelPosition = labelPosition,
    indicator = { color ->
        LayeredComponent(
            rear = ShapeComponent(color.copyColor(alpha = 0.15f), CorneredShape.Pill),
            front =
            LayeredComponent(
                rear = ShapeComponent(
                    color = color,
                    shape = CorneredShape.Pill,
                    shadow = Shadow(radiusDp = 8f, color = color),
                ),
                front = indicatorFrontComponent,
                padding = dimensions(2.dp),
            ),
            padding = dimensions(5.dp),
        )
    },
    indicatorSizeDp = 16f,
) {
    override fun updateInsets(
        context: CartesianMeasuringContext,
        horizontalDimensions: HorizontalDimensions,
        model: CartesianChartModel,
        insets: Insets,
    ) {
        with(context) {
            val baseShadowInsetDp =
                CLIPPING_FREE_SHADOW_RADIUS_MULTIPLIER * LABEL_BACKGROUND_SHADOW_RADIUS_DP
            var topInset = (baseShadowInsetDp - LABEL_BACKGROUND_SHADOW_DY_DP).pixels
            var bottomInset = (baseShadowInsetDp + LABEL_BACKGROUND_SHADOW_DY_DP).pixels
            when (labelPosition) {
                LabelPosition.Top,
                LabelPosition.AbovePoint -> topInset += label.getHeight(context) + tickSizeDp.pixels

                LabelPosition.Bottom -> bottomInset += label.getHeight(context) + tickSizeDp.pixels
                LabelPosition.AroundPoint -> {}
            }
            insets.ensureValuesAtLeast(top = topInset, bottom = bottomInset)
        }
    }
}

private const val LABEL_BACKGROUND_SHADOW_RADIUS_DP = 4f
private const val LABEL_BACKGROUND_SHADOW_DY_DP = 2f
private const val CLIPPING_FREE_SHADOW_RADIUS_MULTIPLIER = 1.4f