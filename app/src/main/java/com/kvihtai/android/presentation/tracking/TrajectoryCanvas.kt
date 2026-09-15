package com.kvihtai.android.presentation.tracking

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import com.kvihtai.android.domain.model.BarCenter

@Composable
fun TrajectoryCanvas(
    trajectoryPath: List<BarCenter>,
    modifier: Modifier = Modifier,
    pathColor: Color = Color(0xFF00E676), // High contrast neon green for gym environment
    strokeWidth: Float = 12f
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        if (trajectoryPath.isEmpty()) return@Canvas

        // Find bounding box of trajectory in mm to scale appropriately
        val minX = trajectoryPath.minOf { it.xMm }
        val maxX = trajectoryPath.maxOf { it.xMm }
        val minY = trajectoryPath.minOf { it.yMm }
        val maxY = trajectoryPath.maxOf { it.yMm }

        val rangeX = (maxX - minX).coerceAtLeast(100.0) // avoid division by zero
        val rangeY = (maxY - minY).coerceAtLeast(100.0)

        val padding = 80f
        val availableWidth = (size.width - padding * 2).coerceAtLeast(100f)
        val availableHeight = (size.height - padding * 2).coerceAtLeast(100f)

        val scaleX = availableWidth / rangeX.toFloat()
        val scaleY = availableHeight / rangeY.toFloat()

        val path = Path()

        trajectoryPath.forEachIndexed { index, point ->
            // Map X: normalized offset from minX, scaled
            val xPx = padding + (point.xMm - minX).toFloat() * scaleX

            // Map Y: backend Y=0 is floor (bottom), Android Canvas Y=0 is top.
            // Invert Y axis: canvasHeight - (padding + (point.yMm - minY) * scaleY)
            val normalizedY = (point.yMm - minY).toFloat() * scaleY
            val yPx = size.height - (padding + normalizedY)

            if (index == 0) {
                path.moveTo(xPx, yPx)
            } else {
                path.lineTo(xPx, yPx)
            }
        }

        drawPath(
            path = path,
            color = pathColor,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}
