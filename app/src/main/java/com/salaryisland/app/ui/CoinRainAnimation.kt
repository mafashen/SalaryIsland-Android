package com.salaryisland.app.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp

@Composable
fun CoinRainAnimation(
    modifier: Modifier = Modifier,
    coinCount: Int = 6,
    primaryColor: Color = Color(0xFFFFC107),
    secondaryColor: Color = Color(0xFFFF9800)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "coin_rain")

    Canvas(modifier = modifier.fillMaxWidth().height(60.dp)) {
        val coinRadius = (6.5).toPx()
        val spacing = size.width / (coinCount + 1)

        for (i in 0 until coinCount) {
            val delay = i * 120
            val anim by infiniteTransition.animateFloat(
                initialValue = -coinRadius * 2,
                targetValue = size.height - 4.dp.toPx(),
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 1000,
                        delayMillis = delay,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                ),
                label = "coin_$i"
            )

            val startX = spacing * (i + 1)
            val alpha = if (anim > size.height * 0.7) 0.6f else 1.0f

            drawCoin(
                center = Offset(startX, anim),
                radius = coinRadius,
                alpha = alpha,
                primaryColor = primaryColor,
                secondaryColor = secondaryColor
            )
        }

        // Draw the bowl at the bottom
        drawBowl(
            canvasWidth = size.width,
            bottomY = size.height,
            bowlColor = secondaryColor
        )
    }
}

private fun DrawScope.drawCoin(
    center: Offset,
    radius: Float,
    alpha: Float,
    primaryColor: Color,
    secondaryColor: Color
) {
    // Coin body
    drawCircle(
        color = primaryColor.copy(alpha = alpha),
        radius = radius,
        center = center
    )
    // Coin border
    drawCircle(
        color = secondaryColor.copy(alpha = alpha),
        radius = radius,
        center = center,
        style = Stroke(width = 1.5f)
    )
    // Coin symbol (¥)
    drawContext.canvas.nativeCanvas.drawText(
        "¥",
        center.x,
        center.y + radius * 0.35f,
        android.graphics.Paint().apply {
            this.color = android.graphics.Color.parseColor("#FF9800")
            this.textSize = radius * 1.1f
            this.textAlign = android.graphics.Paint.Align.CENTER
            this.alpha = (alpha * 255).toInt()
        }
    )
}

private fun DrawScope.drawBowl(
    canvasWidth: Float,
    bottomY: Float,
    bowlColor: Color
) {
    val bowlWidth = 56.dp.toPx()
    val bowlHeight = 20.dp.toPx()
    val midX = canvasWidth / 2
    val topY = bottomY - bowlHeight

    val path = Path().apply {
        moveTo(midX - bowlWidth / 2, topY)
        quadraticBezierTo(midX, bottomY + 10.dp.toPx(), midX + bowlWidth / 2, topY)
    }

    drawPath(
        path = path,
        color = bowlColor,
        style = Stroke(width = 2.dp.toPx())
    )

    drawPath(
        path = path,
        color = bowlColor.copy(alpha = 0.3f),
        style = Stroke(width = 6.dp.toPx())
    )
}
