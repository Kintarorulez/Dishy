package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SleekBlue

@Composable
fun CircularProgressDial(
    remainingSeconds: Int,
    progressPercent: Int,
    modifier: Modifier = Modifier,
    trackColor: Color = Color(0xFFE2E8F0),
    progressColor: Color = SleekBlue
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progressPercent / 100f,
        animationSpec = tween(durationMillis = 600),
        label = "progress"
    )

    val hours = remainingSeconds / 3600
    val minutes = (remainingSeconds % 3600) / 60
    val seconds = remainingSeconds % 60
    val timeFormatted = if (hours > 0) {
        String.format(java.util.Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(java.util.Locale.getDefault(), "%d:%02d", minutes, seconds)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(192.dp)
            .testTag("circular_progress_dial")
    ) {
        Canvas(modifier = Modifier.size(180.dp)) {
            val strokeWidth = 9.dp.toPx()

            // Background Track Circle
            drawCircle(
                color = trackColor,
                radius = (size.minDimension - strokeWidth) / 2f,
                style = Stroke(width = strokeWidth)
            )

            // Foreground Progress Arc (-90 deg start)
            val sweepAngle = animatedProgress * 360f
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        // Inner remaining time readout
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = timeFormatted,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.testTag("remaining_time_text")
            )
            Text(
                text = "REMAINING",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
