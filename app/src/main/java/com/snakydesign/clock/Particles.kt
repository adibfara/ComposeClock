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
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.sin

/**
 * @author Adib Faramarzi (adibfara@gmail.com)
 */

@Composable
fun ParticleHeartBeat(
    clockConfig: ClockConfig,
    count: Int,
    type: ParticleObject.Type
) {


    val paint = Paint()
    val random = Random()
    val particles = mutableListOf<ParticleObject>()/*1.rangeTo(count).map {
                ParticleObject(type, clockConfig).also {
                    it.randomize(random, PxSize(Px(1000f), Px(1000f)))
                    it.animate(0f, PxSize(Px(1000f), Px(1000f)))
                }
            }*/

    Transition(definition = ParticleAnimations, initState = 0, toState = 1) { state ->
        Draw(onPaint = { canvas, size ->
            val progress = 1 - state[ParticleProgress]
            if (particles.isEmpty()) {
                particles.addAll(1.rangeTo(count).map {
                    ParticleObject(type, clockConfig).also {
                        it.randomize(random, size)
                        it.animate(progress, size)
                        it.drawOnCanvas(paint, canvas)
                    }
                })
            } else {
                particles.forEach { particleObject ->
                    particleObject.animate(
                        progress,
                        size
                    )
                    particleObject.drawOnCanvas(paint, canvas)
                }
            }
        })
    }
}


fun ParticleObject.animate(
    progress: Float,
    size: PxSize
) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val radius = min(centerX, centerY).value
    val random = Random()
    val modifier = max(0.2f, progress) * randomFloat(0.1f, 8f, random)
    //animationParams.currentAngle -= progress * 0.01f
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

    val distanceFromCenterPercent = currentLengthByRadius - type.minLengthModifier
    if (distanceFromCenterPercent <= 0f) {
        animationParams.alpha = 0f
    } else if (animationParams.alpha == 0f) {
        animationParams.alpha = random.nextFloat()

//        animationParams.alpha = (animationParams.alpha + modifier * 0.01f).coerceAtMost(1f)
    } else {
        val fadeOutRange = this.type.maxLengthModifier
        animationParams.alpha =
            if (currentLengthByRadius < fadeOutRange) animationParams.alpha else ((1f - currentLengthByRadius) / 1f - fadeOutRange)
    }


}

fun ParticleObject.drawOnCanvas(paint: Paint, canvas: Canvas) {
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