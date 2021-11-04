package com.snakydesign.clock

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * @author Adib Faramarzi (adibfara@gmail.com)
 */

@Composable
fun ComposeClock() {
    Box(modifier = Modifier.fillMaxSize()) {

        val clockConfig = ClockConfig(
            Random()
        )

        ClockBackground(clockConfig)

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {

            /**
             * Background particles
             */
            ParticleHeartBeat(
                clockConfig,
                ParticleObject.Type.Background
            )

            /**
             * Hour handle
             */
            ParticleHeartBeat(
                clockConfig,
                ParticleObject.Type.Hour
            )

            /**
             * Minute handle
             */
            ParticleHeartBeat(
                clockConfig,
                ParticleObject.Type.Minute
            )

            ClockBackgroundBorder(clockConfig)
            ClockMinuteCircles(clockConfig)
            ClockSecondHand(clockConfig)
        }
    }
}

@Composable
private fun ClockBackground(clockConfig: ClockConfig) {
    Spacer(
        modifier = Modifier
            .fillMaxSize()
            .background(clockConfig.colorPalette.backgroundColor)
    )
}

@Preview
@Composable
fun ClockBackgroundPreview() {
    MaterialTheme {
        ClockBackground(clockConfig = ClockConfig())
    }
}

@Composable
private fun ClockSecondHand(clockConfig: ClockConfig) {

    Canvas(Modifier.fillMaxSize()) {
        val interpolator = FastOutSlowInInterpolator()
        val clockRadius = min(size.width.toDp(), size.height.toDp()).value * 0.9f
        val centerX = (size.width / 2)
        val centerY = (size.height / 2)

        val oneMinuteRadians = Math.PI / 30

        val currentSecondInMillisecond = System.currentTimeMillis() % 1000
        val progression = (currentSecondInMillisecond / 1000.0)
        val interpolatedProgression =
            interpolator.getInterpolation(progression.toFloat())
        val animatedSecond =
            Calendar.getInstance().get(Calendar.SECOND) + interpolatedProgression

        val degree = -Math.PI / 2 + (animatedSecond * oneMinuteRadians)

        val x = centerX + cos(degree) * clockRadius
        val y = centerY + sin(degree) * clockRadius

        val radius = 8f
        drawCircle(
            clockConfig.colorPalette.handleColor,
            radius,
            (Offset(x.toFloat(), y.toFloat())),
            style = Fill
        )
    }
}

@Preview
@Composable
private fun ClockSecondHandPreview() {
    MaterialTheme {
        ClockSecondHand(clockConfig = ClockConfig())
    }
}

@Composable
private fun ClockMinuteCircles(clockConfig: ClockConfig) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val clockRadius = min(size.width.toDp(), size.height.toDp()).value * 0.95f
        val centerX = (size.width / 2)
        val centerY = (size.height / 2)
        val oneMinuteRadians = Math.PI / 30
        0.rangeTo(59).forEach { minute ->
            val isHour = minute % 5 == 0
            val degree = -Math.PI / 2 + (minute * oneMinuteRadians)
            val x = centerX + cos(degree) * clockRadius
            val y = centerY + sin(degree) * clockRadius
            drawCircle(
                radius = if (isHour) 12f else 6f,
                color = if (isHour) clockConfig.colorPalette.handleColor else clockConfig.colorPalette.borderColor,
                center = Offset(x.toFloat(), y.toFloat()),
                style = if (isHour) Fill else Stroke(2f),
            )
        }
    }
}

@Preview
@Composable
private fun ClockMinutePreview() {
    MaterialTheme {
        ClockMinuteCircles(clockConfig = ClockConfig())
    }
}

@Composable
private fun ClockBackgroundBorder(clockConfig: ClockConfig) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            radius = min(size.width.toDp(), size.height.toDp()).value * 0.9f,
            color = clockConfig.colorPalette.borderColor,
            center = Offset((size.width / 2), (size.height / 2)),
            style = Stroke(width = 10f),
        )
    }
}

@Preview
@Composable
private fun ClockBackgroundBorderPreview() {
    MaterialTheme {
        ClockBackgroundBorder(clockConfig = ClockConfig())
    }
}