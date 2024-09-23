package io.github.ifa.glancewidget.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PagerIndicator(
    pagerState: PagerState,
    itemSpacing: Dp = 4.dp,
    dotSize: Dp = 9.dp,
    activeLineWidth: Dp = 18.dp,
    contentColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    val pageCount = pagerState.pageCount
    val totalWidth =
        dotSize * pageCount + itemSpacing * (pageCount - 1) + (activeLineWidth - dotSize)
    Canvas(
        modifier = modifier
            .width(width = totalWidth)
            .height(dotSize)
    ) {
        val spacing = itemSpacing.toPx()
        val dotWidth = dotSize.toPx()
        val dotHeight = dotSize.toPx()

        val activeDotWidth = activeLineWidth.toPx()
        var x = 0f
        val y = center.y

        repeat(pageCount) { i ->
            val posOffset = pagerState.pageOffset
            val dotOffset = posOffset % 1
            val current = posOffset.toInt()

            val factor = (dotOffset * (activeDotWidth - dotWidth))

            val calculatedWidth = when {
                i == current -> activeDotWidth - factor
                i - 1 == current || (i == 0 && posOffset > pageCount - 1) -> dotWidth + factor
                else -> dotWidth
            }

            drawIndicator(
                x,
                y,
                calculatedWidth,
                dotHeight,
                CornerRadius(dotWidth / 2),
                contentColor
            )
            x += calculatedWidth + spacing
        }
    }
}

@Preview
@Composable
fun PagerIndicatorPreview() {
    PagerIndicator(
        pagerState = rememberPagerState(pageCount = { 3 }),
        itemSpacing = 4.dp,
        dotSize = 8.dp,
        activeLineWidth = 16.dp
    )
}

val PagerState.pageOffset: Float
    get() = this.currentPage + this.currentPageOffsetFraction

private fun DrawScope.drawIndicator(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    radius: CornerRadius,
    color: Color = Color.White
) {
    val rect = RoundRect(
        x,
        y - height / 2,
        x + width,
        y + height / 2,
        radius
    )
    val path = Path().apply { addRoundRect(rect) }
    drawPath(path = path, color = color)
}
