package com.snakydesign.clock

import androidx.compose.Composable
import androidx.ui.animation.Transition
import androidx.ui.core.Draw
import androidx.ui.geometry.Offset
import androidx.ui.graphics.Canvas
import androidx.ui.graphics.Paint
import androidx.ui.graphics.PaintingStyle
import androidx.ui.unit.Dp
import androidx.ui.unit.PxSize
import androidx.ui.unit.min
import java.util.*
import kotlin.math.*

/**
 * @author Adib Faramarzi (adibfara@gmail.com)
 */

@Composable
fun ParticleHeartBeat(
    clockConfig: ClockConfig,
    type: ParticleObject.Type
) {
    val paint = Paint()
    val random = Random()
    var particle: ParticleObject? = null
    Transition(definition = ParticleAnimations, initState = 0, toState = 1) { state ->
        Draw(onPaint = { canvas, size ->
            val progress = 1 - state[ParticleProgress]
            if (particle == null) {
                particle = ParticleObject(type, clockConfig).also {
                    it.randomize(random, size)
                }
            }
            particle!!.apply {
                animate(
                    progress,
                    size
                )
                drawOnCanvas(paint, canvas)
            }

        })
    }
}


private fun ParticleObject.animate(
    progress: Float,
    size: PxSize
) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val radius = min(centerX, centerY).value
    val random = Random()
    val modifier = max(0.2f, progress) * randomFloat(0.1f, 8f, random)
    val xUpdate = modifier * cos(animationParams.currentAngle)
    val yUpdate = modifier * sin(animationParams.currentAngle)
    val progressionX = animationParams.locationX.value + xUpdate
    val progressionY = animationParams.locationY.value + yUpdate

    val positionInsideCircle =
        hypot(progressionY - centerY.value, progressionX - centerX.value)
    val currentPositionIsInsideCircle = positionInsideCircle < radius * type.maxLengthModifier
    val currentLengthByRadius = positionInsideCircle / (radius * type.maxLengthModifier)

    if (!currentPositionIsInsideCircle) {
        randomize(random, size)
        animationParams.alpha = 0f
    } else {
        animationParams.locationX = Dp(progressionX)
        animationParams.locationY = Dp(progressionY)
    }

    when {
        currentLengthByRadius - type.minLengthModifier <= 0f -> {
            animationParams.alpha = 0f
        }
        animationParams.alpha == 0f -> {
            animationParams.alpha = random.nextFloat()

        }
        else -> {
            val fadeOutRange = this.type.maxLengthModifier
            animationParams.alpha =
                if (currentLengthByRadius < fadeOutRange) animationParams.alpha else ((1f - currentLengthByRadius) / 1f - fadeOutRange)
        }
    }


}

private fun ParticleObject.drawOnCanvas(paint: Paint, canvas: Canvas) {
    canvas.apply {
        paint.color = animationParams.currentColor
        paint.alpha = animationParams.alpha
        val centerW = animationParams.locationX.value
        val centerH = animationParams.locationY.value
        if (animationParams.isFilled) {
            paint.style = PaintingStyle.fill
        } else {
            paint.style = PaintingStyle.stroke

        }
        drawCircle(
            Offset(centerW, centerH),
            animationParams.particleSize.value / 2f,
            paint
        )
    }
}


private fun ParticleObject.randomize(
    random: Random,
    pxSize: PxSize
) {
    val calendar = Calendar.getInstance()
    val currentMinuteCount = calendar.get(Calendar.MINUTE)
    val currentHour = (calendar.get(Calendar.HOUR_OF_DAY) % 24).toDouble() / 12.0
    val currentMinute = (currentMinuteCount).toDouble() / 60.0
    val currentMinuteRadians = (Math.PI / -2.0) + currentMinute * 2.0 * Math.PI
    val oneHourRadian = (currentMinute * 2.0 * Math.PI) / 12.0
    val currentHourRadians =
        (Math.PI / -2.0) + (currentHour) * 2.0 * Math.PI
    val currentHourMinuteRadians = (oneHourRadian * currentMinute) + currentHourRadians
    val randomAngleOffset =
        randomFloat(type.startAngleOffsetRadians, type.endAngleOffsetRadians, random)
    val randomizedAngle = when (type) {
        ParticleObject.Type.Hour -> currentHourMinuteRadians.toFloat()
        ParticleObject.Type.Minute -> currentMinuteRadians.toFloat()
        ParticleObject.Type.Background -> (currentHourMinuteRadians + randomAngleOffset).toFloat()
    }
    val centerX = (pxSize.width / 2).value + randomFloat(-10f, 10f, random)
    val centerY = (pxSize.height / 2).value + randomFloat(-10f, 10f, random)
    val radius = kotlin.math.min(centerX, centerY)
    val randomLength =
        randomFloat(type.minLengthModifier * radius, this.type.maxLengthModifier * radius, random)
    val x = randomLength * cos(randomizedAngle)
    val y = randomLength * sin(randomizedAngle)
    val color = when (type) {
        ParticleObject.Type.Background -> clockConfig.colorPalette.mainColors.random()
        ParticleObject.Type.Hour -> clockConfig.colorPalette.handleColor
        ParticleObject.Type.Minute -> clockConfig.colorPalette.handleColor
    }
    animationParams = ParticleObject.AnimationParams(
        isFilled = clockConfig.random.nextFloat() < 0.7f,
        alpha = (random.nextFloat()).coerceAtLeast(0f),
        locationX = Dp(centerX + x),
        locationY = Dp(centerY + y),
        particleSize = Dp(randomFloat(type.minSize.value, type.maxSize.value, random)),
        currentAngle = randomizedAngle.toFloat(),
        progressModifier = randomFloat(1f, 2f, random),
        currentColor = color
    )
}