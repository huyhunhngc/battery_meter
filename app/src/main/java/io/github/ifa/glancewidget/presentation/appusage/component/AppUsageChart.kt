package io.github.ifa.glancewidget.presentation.appusage.component

import android.graphics.Typeface
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.cartesianLayerPadding
import com.patrykandpatrick.vico.compose.cartesian.layer.grouped
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.axis.Axis
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
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
    val context = LocalContext.current
    val columnColor = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.inversePrimary
    )

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
    val column = rememberLineComponent(
        color = MaterialTheme.colorScheme.primary,
        thickness = 16.dp,
        shape = CorneredShape.rounded(topLeftDp = 4f, topRightDp = 4f)
    )

    val typeface = LocalContext.current.resources.getFont(R.font.googlesansregular)
    val label = rememberTextComponent(
        typeface = Typeface.create(typeface, Typeface.BOLD),
        color = MaterialTheme.colorScheme.primary,
        minWidth = TextComponent.MinWidth.fixed(20f),
    )
    val legendColumnKeys = listOf(
        context.getString(R.string.cpu_time),
        context.getString(R.string.cpu_foreground)
    )
    Column(modifier = modifier) {
        Text(
            text = context.getString(R.string.usage_time),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberColumnCartesianLayer(
                    columnProvider = object : ColumnCartesianLayer.ColumnProvider {
                        override fun getColumn(
                            entry: ColumnCartesianLayerModel.Entry,
                            seriesIndex: Int,
                            extraStore: ExtraStore,
                        ) = column.copy(color = columnColor[seriesIndex % columnColor.size].toArgb())

                        override fun getWidestSeriesColumn(seriesIndex: Int, extraStore: ExtraStore) = column
                    },
                    mergeMode = {
                        ColumnCartesianLayer.MergeMode.grouped(
                            columnSpacing = 1.dp
                        )
                    },
                ),
                startAxis = VerticalAxis.rememberStart(
                    label = label,
                    guideline = rememberAxisGuidelineComponent(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    )
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
                    }, guideline = null, label = label
                ),
                layerPadding = cartesianLayerPadding(
                    scalableStartPadding = 8.dp, scalableEndPadding = 8.dp
                ),
            ),
            modelProducer = modelProducer,
            modifier = Modifier.weight(1f),
            zoomState = rememberVicoZoomState(zoomEnabled = false)
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            legendColumnKeys.forEachIndexed { index, key ->
                LegendItem(
                    label = key,
                    color = columnColor[index % columnColor.size]
                )
            }
        }
    }
}

@Composable
private fun LegendItem(
    label: String,
    color: Color
) {
    Row(
        modifier = Modifier.padding(4.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, shape = RoundedCornerShape(4.dp))
        )
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}
