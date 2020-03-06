package com.snakydesign.clock

import androidx.ui.graphics.Color
import androidx.ui.unit.Dp
import java.util.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * @author Adib Faramarzi (adibfara@gmail.com)
 */
data class ClockConfig(
    val startX: Int,
    val startY: Int,
    val endX: Int,
    val endY: Int,
    val random: Random,
    val maxCount: Int = 100,
    val hourCount: Int = 50,
    val minuteCount: Int = 100
) {

    private val lengthX: Int = (endX - startX)
    private val lengthY: Int = (endY - startY)
    val centerX: Int = startX + (lengthX / 2)
    val centerY: Int = startY + (lengthY / 2)
    val radius: Int = min(lengthX, lengthY) / 2
}

enum class ColorPalette(val baseColor: Color) {
    Blue(Color.Blue),
    Green(Color.Green),
    Red(Color.Red),
}

data class ParticleObject(
    val type: Type,
    val clockConfig: ClockConfig,
    var animationParams: AnimationParams = AnimationParams(clockConfig)
) {
    data class AnimationParams(
        var locationX: Dp,
        var locationY: Dp,
        var alpha: Float,
        var isFilled: Boolean,
        var particleSize: Dp = Dp(0f),
        var colorPalette: ColorPalette = ColorPalette.Blue,
        var currentAngle: Float = 1f,
        var progressModifier: Float = 1f
    ) {
        constructor(clockConfig: ClockConfig) : this( //todo remove
            Dp(clockConfig.centerX.toFloat()),
            Dp(clockConfig.centerY.toFloat()),
            clockConfig.random.nextFloat(),
            clockConfig.random.nextFloat() < 0.7f
        )
    }

    enum class Type(
        val startAngleOffsetRadians: Float,
        val endAngleOffsetRadians: Float,
        val maxLengthModifier: Float,
        val minSize: Dp,
        val maxSize: Dp,
        val fadesIn: Boolean
    ) {
        Background(
            Math.PI.toFloat() * (0.5f / 12f),
            2 * Math.PI.toFloat() - (Math.PI.toFloat() * (0.5f / 12f)),
            0.9f,
            Dp(8f),
            Dp(12f),
            true
        ),
        Hour(
            0f,
            0f,
            0.5f,
            Dp(16f),
            Dp(24f),
            false
        ),
        Minute(
            0f,
            0f,
            0.7f,
            Dp(5f),
            Dp(8f),
            false
        )
    }
}

fun ParticleObject.randomize(
    random: Random,
    clockConfig: ClockConfig,
    colorPalette: ColorPalette = ColorPalette.Blue
) {
    val calendar = Calendar.getInstance()
    val currentMinuteCount = calendar.get(Calendar.MINUTE)
    val currentHourCount = calendar.get(Calendar.HOUR_OF_DAY)
    val currentMinute = (currentMinuteCount).toDouble() / 60.0
    val currentMinuteRadians = (Math.PI / -2.0) + currentMinute * 2.0 * Math.PI
    val oneHourRadian = (currentMinute * 2.0 * Math.PI) / 12.0
    val currentHourRadians =
        (Math.PI / -2.0) + ((currentHourCount % 12).toDouble() / 12.0) * 2.0 * Math.PI
    val currentHourMinuteRadians = (oneHourRadian * currentMinute) + currentHourRadians

    val randomAngleOffset =
        randomFloat(type.startAngleOffsetRadians, type.endAngleOffsetRadians, random)
    val randomizedAngle = when (type) {
        ParticleObject.Type.Hour -> currentHourMinuteRadians.toFloat()
        ParticleObject.Type.Minute -> currentMinuteRadians.toFloat()
        ParticleObject.Type.Background -> (currentHourMinuteRadians + randomAngleOffset).toFloat()
    }
    val randomLength = randomFloat(0f, this.type.maxLengthModifier * clockConfig.radius, random)
    val x = randomLength * cos(randomizedAngle)
    val y = randomLength * sin(randomizedAngle)
    animationParams = ParticleObject.AnimationParams(
        isFilled = clockConfig.random.nextFloat() < 0.7f,
        alpha = (random.nextFloat() - 0.1f).coerceAtLeast(0f),
        locationX = Dp(clockConfig.centerX.toFloat() + x),
        locationY = Dp(clockConfig.centerY.toFloat() + y),
        particleSize = Dp(randomFloat(type.minSize.value, type.maxSize.value, random)),
        colorPalette = colorPalette,
        currentAngle = randomizedAngle.toFloat(),
        progressModifier = randomFloat(1f, 2f, random)
    )
}