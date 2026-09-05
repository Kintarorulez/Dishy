package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.ui.theme.AppThemeMode
import kotlin.math.sin

/**
 * Atmospheric background theme inspired by clean dishwasher hydro-dynamics,
 * brushed stainless steel tub textures, and delicate water spray currents.
 * Designed to be simplistic, elegant, and non-distracting to keep the UI spacious.
 */
@Composable
fun DishwasherBackground(
    themeMode: AppThemeMode,
    isPoweredOn: Boolean,
    isRunning: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // Subtle wave animation when wash cycle is active
    val transition = rememberInfiniteTransition(label = "dishwasher_water_wave")
    val waveOffset by if (isRunning) {
        transition.animateFloat(
            initialValue = 0f,
            targetValue = (2 * Math.PI).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 9000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "wave_motion"
        )
    } else {
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 10000),
                repeatMode = RepeatMode.Restart
            ),
            label = "wave_static"
        )
    }

    // Base atmospheric background gradient based on theme
    val bgBrush = when (themeMode) {
        AppThemeMode.LIGHT -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF3F7FA), // Clean porcelain mist
                Color(0xFFEBF3F8), // Soft hydro aqua tint
                Color(0xFFE2EDF4)  // Stainless basin wash shade
            )
        )
        AppThemeMode.DIM -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFF161A22), // Deep brushed graphite
                Color(0xFF131F2A), // Dark aquatic chamber
                Color(0xFF18222D)  // Tub basin
            )
        )
        AppThemeMode.DARK -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0D1016), // Dark appliance steel
                Color(0xFF0F1822), // Subdued hydro twilight
                Color(0xFF0B131D)  // Deep chamber
            )
        )
    }

    val waterWaveColorPrimary = when (themeMode) {
        AppThemeMode.LIGHT -> Color(0xFF0284C7).copy(alpha = if (isRunning) 0.055f else 0.035f)
        AppThemeMode.DIM -> Color(0xFF38BDF8).copy(alpha = if (isRunning) 0.07f else 0.045f)
        AppThemeMode.DARK -> Color(0xFF38BDF8).copy(alpha = if (isRunning) 0.065f else 0.04f)
    }

    val waterWaveColorSecondary = when (themeMode) {
        AppThemeMode.LIGHT -> Color(0xFF0EA5E9).copy(alpha = if (isRunning) 0.045f else 0.025f)
        AppThemeMode.DIM -> Color(0xFF2DD4BF).copy(alpha = if (isRunning) 0.055f else 0.035f)
        AppThemeMode.DARK -> Color(0xFF2DD4BF).copy(alpha = if (isRunning) 0.05f else 0.03f)
    }

    val bubbleColor = when (themeMode) {
        AppThemeMode.LIGHT -> Color(0xFF0284C7).copy(alpha = 0.06f)
        AppThemeMode.DIM -> Color(0xFF38BDF8).copy(alpha = 0.08f)
        AppThemeMode.DARK -> Color(0xFF38BDF8).copy(alpha = 0.07f)
    }

    val stainlessAccentColor = when (themeMode) {
        AppThemeMode.LIGHT -> Color(0xFFCBD5E1).copy(alpha = 0.4f)
        AppThemeMode.DIM -> Color(0xFF334155).copy(alpha = 0.35f)
        AppThemeMode.DARK -> Color(0xFF1E293B).copy(alpha = 0.35f)
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Dishwasher Hydro & Stainless Canvas (Rendered behind all UI components)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Draw thematic background gradient
            drawRect(brush = bgBrush)

            // 2. Subtle brushed stainless steel top trim line
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        stainlessAccentColor.copy(alpha = 0.05f),
                        stainlessAccentColor,
                        stainlessAccentColor.copy(alpha = 0.05f)
                    )
                ),
                start = Offset(0f, 0f),
                end = Offset(width, 0f),
                strokeWidth = 3f
            )

            // 3. Lower Hydro Wash Current (Wave 1)
            val path1 = Path()
            val baseHeight1 = height * 0.72f
            path1.moveTo(0f, height)
            path1.lineTo(0f, baseHeight1)
            val steps = 24
            for (i in 0..steps) {
                val x = (width / steps) * i
                val progress = (i.toFloat() / steps) * 2 * Math.PI.toFloat()
                val y = baseHeight1 + (sin(progress + waveOffset) * 24f)
                path1.lineTo(x, y)
            }
            path1.lineTo(width, height)
            path1.close()
            drawPath(path = path1, color = waterWaveColorPrimary)

            // 4. Secondary Wash Wave (Wave 2)
            val path2 = Path()
            val baseHeight2 = height * 0.85f
            path2.moveTo(0f, height)
            path2.lineTo(0f, baseHeight2)
            for (i in 0..steps) {
                val x = (width / steps) * i
                val progress = (i.toFloat() / steps) * 2.5f * Math.PI.toFloat()
                val y = baseHeight2 + (sin(progress - waveOffset * 0.8f) * 18f)
                path2.lineTo(x, y)
            }
            path2.lineTo(width, height)
            path2.close()
            drawPath(path = path2, color = waterWaveColorSecondary)

            // 5. Delicate Hydro Droplets / Sparkling Clean Bubbles (Subtle decorative accents along perimeter)
            val bubbles = listOf(
                Triple(width * 0.12f, height * 0.15f, 10f),
                Triple(width * 0.88f, height * 0.22f, 14f),
                Triple(width * 0.92f, height * 0.45f, 8f),
                Triple(width * 0.08f, height * 0.58f, 12f),
                Triple(width * 0.18f, height * 0.82f, 9f),
                Triple(width * 0.82f, height * 0.76f, 11f)
            )

            for ((bx, by, radius) in bubbles) {
                val animatedY = if (isRunning) by + (sin(waveOffset + bx) * 4f) else by
                // Droplet ring
                drawCircle(
                    color = bubbleColor,
                    radius = radius,
                    center = Offset(bx, animatedY),
                    style = Stroke(width = 1.5f)
                )
                // Droplet inner glint
                drawCircle(
                    color = bubbleColor.copy(alpha = bubbleColor.alpha * 0.6f),
                    radius = radius * 0.35f,
                    center = Offset(bx - radius * 0.3f, animatedY - radius * 0.3f)
                )
            }

            // 6. Subtle Dishwasher Tub Rack Wire Grid Accent (Simplistic & Minimal)
            val rackAlpha = if (themeMode == AppThemeMode.LIGHT) 0.035f else 0.025f
            val rackColor = stainlessAccentColor.copy(alpha = rackAlpha)
            for (step in 1..4) {
                val rackY = height * (0.28f + step * 0.12f)
                drawLine(
                    color = rackColor,
                    start = Offset(width * 0.06f, rackY),
                    end = Offset(width * 0.94f, rackY),
                    strokeWidth = 1f
                )
            }

            // 7. Rotating Spray Arm Geometry Hint in upper right background
            val sprayCenterX = width * 0.85f
            val sprayCenterY = height * 0.12f
            val armRotation = if (isRunning) waveOffset else 0f
            drawCircle(
                color = waterWaveColorPrimary.copy(alpha = 0.04f),
                radius = 70f,
                center = Offset(sprayCenterX, sprayCenterY),
                style = Stroke(width = 1.5f)
            )
            drawCircle(
                color = waterWaveColorPrimary.copy(alpha = 0.025f),
                radius = 110f,
                center = Offset(sprayCenterX, sprayCenterY),
                style = Stroke(width = 1.2f)
            )
            // Spray arm dual fins
            val armLength = 55f
            val cosRot = kotlin.math.cos(armRotation)
            val sinRot = kotlin.math.sin(armRotation)
            drawLine(
                color = waterWaveColorPrimary.copy(alpha = if (isRunning) 0.08f else 0.04f),
                start = Offset(sprayCenterX - armLength * cosRot, sprayCenterY - armLength * sinRot),
                end = Offset(sprayCenterX + armLength * cosRot, sprayCenterY + armLength * sinRot),
                strokeWidth = 2.5f
            )
        }

        // Render application content on top
        content()
    }
}
