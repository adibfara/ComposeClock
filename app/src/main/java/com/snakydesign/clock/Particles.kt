package com.snakydesign.clock

import androidx.compose.Composable
import androidx.ui.animation.Transition
import androidx.ui.foundation.Canvas
import androidx.ui.graphics.Paint
import androidx.ui.layout.LayoutSize
import androidx.ui.unit.Dp
import java.util.*
import kotlin.math.*

/**
 * @author Adib Faramarzi (adibfara@gmail.com)
 */

@Composable
fun RandomParticles(
    clockConfig: ClockConfig,
    type: ParticleObject.Type
) {
    val paint = Paint()
    paint.strokeWidth = 1f
    paint.isAntiAlias = false
    val particles = 1.rangeTo(clockConfig.maxCount).map {
        ParticleObject(type, clockConfig)
    }

    Transition(definition = ParticleAnimations, initState = 0, toState = 1) { state ->
        Canvas(modifier = LayoutSize(Dp(400f)), onCanvas = {
            val progress = 1 - state[Progress]
            particles.forEach { particleObject ->
                particleObject.animate(
                    progress,
                    clockConfig
                )
                updateParticleWithCanvas(particleObject, paint, this)
            }
        })
    }
}

@Composable
fun ParticleHeartBeat(
    clockConfig: ClockConfig,
    count: Int,
    type: ParticleObject.Type
) {
    val paint = Paint()
    val random = Random()
    val particles = 1.rangeTo(count).map {
        ParticleObject(type, clockConfig).also {
            it.randomize(random, clockConfig)
            it.animate(0f, clockConfig)
        }
    }

    Transition(definition = ParticleAnimations, initState = 0, toState = 1) { state ->
        Canvas(modifier = LayoutSize(Dp(400f)), onCanvas = {

            val progress = 1 - state[Progress]
            particles.forEach { particleObject ->
                particleObject.animate(
                    progress,
                    clockConfig
                )
                updateParticleWithCanvas(particleObject, paint, this)
            }
        })
    }
}


fun ParticleObject.animate(
    progress: Float,
    clockConfig: ClockConfig
) {
    val random = Random()
    val modifier = max(0.2f, progress * randomFloat(0.1f, 1f, random))
    //animationParams.currentAngle -= progress * 0.01f
    val xUpdate = modifier * cos(animationParams.currentAngle)
    val yUpdate = modifier * sin(animationParams.currentAngle)
    val progressionX = animationParams.locationX.value + xUpdate
    val progressionY = animationParams.locationY.value + yUpdate

    val positionInsideCircle =
        hypot(progressionY - clockConfig.centerY, progressionX - clockConfig.centerX)
    val currentPositionIsInsideCircle = positionInsideCircle < clockConfig.radius
    val currentLengthByRadius = positionInsideCircle / clockConfig.radius.toFloat()

    if (!currentPositionIsInsideCircle) {
        randomize(random, clockConfig)
        animationParams.alpha = 0f
    } else {
        animationParams.locationX = Dp(progressionX)
        animationParams.locationY = Dp(progressionY)
    }

    val distanceFromCenterPercent = currentLengthByRadius - 0.2f
    if (type.fadesIn && distanceFromCenterPercent <= 0f) {
        animationParams.alpha = 0f
    } else {
        animationParams.alpha = (animationParams.alpha + progress * 0.01f).coerceAtMost(1f)
    }
    animationParams.alpha =
        (animationParams.alpha - ((currentLengthByRadius - 0.9f).coerceAtLeast(0f) / 0.1f)).coerceAtLeast(
            0f
        )

}
