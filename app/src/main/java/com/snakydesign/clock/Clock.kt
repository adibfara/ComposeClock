package com.snakydesign.clock

import androidx.compose.Composable
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.ui.animation.Transition
import androidx.ui.core.Draw
import androidx.ui.geometry.Offset
import androidx.ui.graphics.Canvas
import androidx.ui.graphics.Paint
import androidx.ui.graphics.PaintingStyle
import androidx.ui.layout.LayoutSize
import androidx.ui.layout.Stack
import androidx.ui.unit.PxSize
import androidx.ui.unit.min
import androidx.ui.unit.toRect
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * @author Adib Faramarzi (adibfara@gmail.com)
 */

@Composable
fun ComposeClock() {
    Stack(modifier = LayoutSize.Fill) {


        val clockConfig = ClockConfig(
            Random()
        )
        ClockBackground(clockConfig)

        /**
         * Background particles
         */

        /**
         * Background particles
         */
        repeat(1000) {
            ParticleHeartBeat(
                clockConfig,
                ParticleObject.Type.Background
            )
        }

        /**
         * Hour handle
         */

        /**
         * Hour handle
         */
        repeat(100) {
            ParticleHeartBeat(
                clockConfig,
                ParticleObject.Type.Hour
            )
        }

        /**
         * Minute handle
         */

        /**
         * Minute handle
         */
        repeat(100) {
            ParticleHeartBeat(
                clockConfig,
                ParticleObject.Type.Minute
            )
        }

        ClockBackgroundBorder(clockConfig)
        ClockMinuteCircles(clockConfig)
        ClockSecondHand(clockConfig)
    }
}

@Composable
private fun ClockBackground(clockConfig: ClockConfig) {
    val backgroundPaint = Paint().apply {
        color = clockConfig.colorPalette.backgroundColor
        style = PaintingStyle.fill
    }
    Draw(onPaint = { canvas: Canvas, parentSize: PxSize ->
        canvas.drawRect(parentSize.toRect(), backgroundPaint)
    })
}

@Composable
private fun ClockSecondHand(clockConfig: ClockConfig) {
    val interpolator = FastOutSlowInInterpolator()

    Transition(definition = SecondHandAnimations, initState = 0, toState = 1) { state ->
        Draw(onPaint = { canvas, parentSize ->
            val clockRadius =
                0.9f * min((parentSize.width / 2), (parentSize.height / 2)).value
            val paint = Paint().apply {
                style = PaintingStyle.fill
                color = clockConfig.colorPalette.handleColor
            }
            val centerX = (parentSize.width / 2).value
            val centerY = (parentSize.height / 2).value
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

            paint.style = PaintingStyle.fill
            val radius = 8f
            canvas.drawCircle(
                (Offset(x.toFloat(), y.toFloat())),
                radius,
                paint
            )
        })
    }
}

@Composable
private fun ClockMinuteCircles(clockConfig: ClockConfig) {
    Draw(onPaint = { canvas: Canvas, parentSize: PxSize ->
        val clockRadius = 0.95f * min((parentSize.width / 2), (parentSize.height / 2)).value
        val paint = Paint().apply {
            style = PaintingStyle.fill
            color = clockConfig.colorPalette.handleColor
        }
        val centerX = (parentSize.width / 2).value
        val centerY = (parentSize.height / 2).value
        val oneMinuteRadians = Math.PI / 30
        0.rangeTo(59).forEach { minute ->
            val isHour = minute % 5 == 0
            val degree = -Math.PI / 2 + (minute * oneMinuteRadians)
            val x = centerX + cos(degree) * clockRadius
            val y = centerY + sin(degree) * clockRadius

            val radius: Float
            if (isHour) {
                paint.style = PaintingStyle.fill
                radius = 12f
            } else {
                paint.style = PaintingStyle.stroke
                radius = 6f
            }
            canvas.drawCircle(
                (Offset(x.toFloat(), y.toFloat())),
                radius,
                paint
            )
        }

    })
}

@Composable
private fun ClockBackgroundBorder(clockConfig: ClockConfig) {
    Draw(onPaint = { canvas: Canvas, parentSize: PxSize ->
        val radius = min((parentSize.width / 2), (parentSize.height / 2)).value * 0.9f
        val paint = Paint().apply {
            style = PaintingStyle.stroke
            strokeWidth = 10f
            color = clockConfig.colorPalette.borderColor
        }
        canvas.drawCircle(
            (Offset((parentSize.width / 2).value, (parentSize.height / 2).value)),
            radius,
            paint
        )
    })
}