package com.snakydesign.clock


import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import java.util.*
import kotlin.math.cos
import kotlin.math.min
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

        Box(
            Modifier
                .fillMaxSize()
                .padding(Dp(16f))
        ) {
            ParticleHeartBeat(
                clockConfig,
                ParticleObject.Type.Background
            )
            ParticleHeartBeat(
                clockConfig,
                ParticleObject.Type.Hour
            )
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

@Composable
private fun ClockSecondHand(clockConfig: ClockConfig) {
    val interpolator = FastOutSlowInInterpolator()

    val animation = rememberInfiniteTransition()
    val second by animation.animateFloat(
        initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    var startSecond by remember {
        mutableStateOf(Calendar.getInstance().get(Calendar.SECOND).toFloat())
    }
    Canvas(Modifier.fillMaxSize()) {
        val parentSize = size
        val clockRadius =
            0.9f * min((parentSize.width / 2), (parentSize.height / 2))
        val centerX = (parentSize.width / 2)
        val centerY = (parentSize.height / 2)
        val oneMinuteRadians = Math.PI / 30

        val currentSecondInMillisecond = System.currentTimeMillis() % 1000
        val progression = second//(currentSecondInMillisecond / 1000.0)
        /*if(progression == 0f){
            startSecond = (startSecond + 1) % 60
        }*/
        val interpolatedProgression = progression/*
            interpolator.getInterpolation(progression.toFloat())*/
        startSecond =
                /*Calendar.getInstance().get(Calendar.SECOND)*/
            startSecond + interpolatedProgression

        val degree = -Math.PI / 2 + (startSecond * oneMinuteRadians)
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

@Composable
private fun ClockMinuteCircles(clockConfig: ClockConfig) {
    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
        val parentSize = size
        val clockRadius = 0.95f * kotlin.math.min((parentSize.width / 2), (parentSize.height / 2))
        val paint = Paint().apply {
            style = PaintingStyle.Fill
            color = clockConfig.colorPalette.handleColor
        }
        val centerX = (parentSize.width / 2)
        val centerY = (parentSize.height / 2)
        val oneMinuteRadians = Math.PI / 30
        0.rangeTo(59).forEach { minute ->
            val isHour = minute % 5 == 0
            val degree = -Math.PI / 2 + (minute * oneMinuteRadians)
            val x = centerX + cos(degree) * clockRadius
            val y = centerY + sin(degree) * clockRadius

            val radius: Float
            if (isHour) {
                paint.style = PaintingStyle.Fill
                radius = 12f
            } else {
                paint.style = PaintingStyle.Stroke
                radius = 6f
            }
            drawCircle(
                clockConfig.colorPalette.handleColor,
                radius,
                center = (Offset(x.toFloat(), y.toFloat())),
            )
        }

    }
}

@Composable
private fun ClockBackgroundBorder(clockConfig: ClockConfig) {
    androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
        val parentSize = size
        val radius = kotlin.math.min((parentSize.width / 2), (parentSize.height / 2)) * 0.9f
        drawCircle(
            clockConfig.colorPalette.borderColor,
            radius,
            (Offset((parentSize.width / 2), (parentSize.height / 2))),
            style = Stroke(10.dp.toPx())
        )
    }
}