package com.snakydesign.clock

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
    val animation = rememberInfiniteTransition()
    val particles = remember { mutableStateOf(listOf<ParticleObject>()) }
    val animated by animation.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = CubicBezierEasing(0.98f, 0.2f, 1.0f, 1.0f)),
            repeatMode = RepeatMode.Reverse
        )
    )

    androidx.compose.foundation.Canvas(modifier = Modifier
        .fillMaxSize()
        .onSizeChanged { size ->
            particles.value = 1
                .rangeTo(100)
                .map {
                    ParticleObject(type, clockConfig)
                }
        }) {
        particles.value.forEach { particle ->
            particle.animate(
                this@Canvas,
                animated,
                size
            )
            drawParticle(particle)
        }


    }

}


private fun ParticleObject.animate(
    drawScope: DrawScope,
    progress: Float,
    size: Size
) {

    val centerX = size.width / 2
    val centerY = size.height / 2
    val radius = min(centerX, centerY)
    val random = Random()
    val modifier =
        with(drawScope) { progress * animationParams.progressModifier.dp.toPx() }//* randomFloat(1f, 4f, random) }
    val xUpdate = modifier * cos(animationParams.currentAngle)
    val yUpdate = modifier * sin(animationParams.currentAngle)
    val newX = animationParams.locationX.value + xUpdate
    val newY = animationParams.locationY.value + yUpdate

    val positionInsideCircle =
        hypot(newY - centerY, newX - centerX)
    val currentPositionIsInsideCircle = positionInsideCircle < radius * type.maxLengthModifier
    val currentLengthByRadius = positionInsideCircle / (radius * type.maxLengthModifier)
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
                (if (currentLengthByRadius < fadeOutRange) animationParams.alpha else ((1f - currentLengthByRadius) / (1f - fadeOutRange))).coerceIn(
                    0f,
                    1f
                )
        }
    }
    if (!currentPositionIsInsideCircle) {
        randomize(random, size)
        animationParams.alpha = 0f
    } else {
        animationParams.locationX = Dp(newX)
        animationParams.locationY = Dp(newY)
    }


}

private fun DrawScope.drawParticle(particleObject: ParticleObject) {

    val centerW = particleObject.animationParams.locationX.value
    val centerH = particleObject.animationParams.locationY.value
    drawCircle(
        SolidColor(particleObject.animationParams.currentColor),
        particleObject.animationParams.particleSize.value / 2f,
        Offset(centerW, centerH),
        style = if (particleObject.animationParams.isFilled) Fill else Stroke(1.dp.toPx()),
        alpha = particleObject.animationParams.alpha
    )
}

private fun ParticleObject.drawOnCanvas(paint: Paint, canvas: DrawScope) {
    canvas.apply {
        paint.color = animationParams.currentColor
        paint.alpha = animationParams.alpha
        val centerW = animationParams.locationX.value
        val centerH = animationParams.locationY.value
        if (animationParams.isFilled) {
            paint.style = PaintingStyle.Fill
        } else {
            paint.style = PaintingStyle.Stroke

        }
        drawCircle(
            animationParams.currentColor,
            animationParams.particleSize.value / 2f,
            Offset(centerW, centerH),
        )
    }
}


private fun ParticleObject.randomize(
    random: Random,
    pxSize: Size
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
    val centerX = (pxSize.width / 2) + randomFloat(-10f, 10f, random)
    val centerY = (pxSize.height / 2) + randomFloat(-10f, 10f, random)
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
        progressModifier = randomFloat(1f, 8f, random),
        currentColor = color
    )
}