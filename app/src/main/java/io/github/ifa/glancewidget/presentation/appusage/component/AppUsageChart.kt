package io.github.ifa.glancewidget.presentation.appusage.component

import android.graphics.Typeface
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.cartesianLayerPadding
import com.patrykandpatrick.vico.compose.cartesian.layer.grouped
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.component.shapeComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.rememberHorizontalLegend
import com.patrykandpatrick.vico.compose.common.rememberVerticalLegend
import com.patrykandpatrick.vico.compose.common.vicoTheme
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.axis.Axis
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.LegendItem
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.domain.UsageStatsWrapper
import io.github.ifa.glancewidget.utils.timeMillisToHours
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun AppUsageChart(
    modifier: Modifier = Modifier,
    appUsageStats: List<UsageStatsWrapper>,
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val totalTimeVisible = remember(appUsageStats) {
        appUsageStats.map { it.usageStats?.totalTimeVisible?.timeMillisToHours() ?: 0 }
    }
    val totalTimeForeground = remember(appUsageStats) {
        appUsageStats.map { it.usageStats?.totalTimeInForeground?.timeMillisToHours() ?: 0 }
    }

    LaunchedEffect(appUsageStats) {
        withContext(Dispatchers.Default) {
            modelProducer.runTransaction {
                columnSeries {
                    series(totalTimeVisible)
                    series(totalTimeForeground)
                }
            }
        }
    }
    val timeVisibleColumn =
        rememberLineComponent(
            color = MaterialTheme.colorScheme.primary,
            thickness = 16.dp,
            shape = CorneredShape.rounded(topLeftDp = 4f, topRightDp = 4f)
        )
    val timeForegroundColumn =
        rememberLineComponent(
            color = MaterialTheme.colorScheme.inversePrimary,
            thickness = 16.dp,
            shape = CorneredShape.rounded(topLeftDp = 4f, topRightDp = 4f)
        )
    val typeface = LocalContext.current.resources.getFont(R.font.googlesansregular)
    val label = rememberTextComponent(
        typeface = Typeface.create(typeface, Typeface.BOLD),
        color = MaterialTheme.colorScheme.primary,
        minWidth = TextComponent.MinWidth.fixed(16f),
    )
    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(
                columnProvider = object : ColumnCartesianLayer.ColumnProvider {
                    override fun getColumn(
                        entry: ColumnCartesianLayerModel.Entry,
                        seriesIndex: Int,
                        extraStore: ExtraStore,
                    ) = if (seriesIndex == 0) timeVisibleColumn else timeForegroundColumn

                    override fun getWidestSeriesColumn(seriesIndex: Int, extraStore: ExtraStore) =
                        timeVisibleColumn
                },
                mergeMode = {
                    ColumnCartesianLayer.MergeMode.grouped(
                        columnSpacing = 1.dp
                    )
                },
            ),
            startAxis = VerticalAxis.rememberStart(
                label = label,
                guideline = null,
                valueFormatter = object : CartesianValueFormatter {
                    override fun format(
                        context: CartesianMeasuringContext,
                        value: Double,
                        verticalAxisPosition: Axis.Position.Vertical?,
                    ): String {
                        return if (value >= totalTimeVisible.max()) "${value.toInt()} h" else value.toInt()
                            .toString()
                    }
                },
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = object : CartesianValueFormatter {
                    override fun format(
                        context: CartesianMeasuringContext,
                        value: Double,
                        verticalAxisPosition: Axis.Position.Vertical?,
                    ): String {
                        val appName = appUsageStats.getOrNull(value.toInt())?.appName ?: "Unknown"
                        return appName.split(" ").firstOrNull()?.take(10).orEmpty()
                    }
                },
                guideline = null,
                label = label
            ),
            layerPadding = cartesianLayerPadding(
                scalableStartPadding = 8.dp,
                scalableEndPadding = 8.dp
            ),
        ),
        modelProducer = modelProducer,
        modifier = modifier,
        zoomState = rememberVicoZoomState(zoomEnabled = false),

    )
}
