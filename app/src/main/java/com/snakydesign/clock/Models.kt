package com.snakydesign.clock

import androidx.ui.graphics.Color
import androidx.ui.unit.Dp
import androidx.ui.unit.PxSize
import androidx.ui.unit.min
import com.snakydesign.clock.ParticleObject.Type.*
import java.util.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * @author Adib Faramarzi (adibfara@gmail.com)
 */
data class ClockConfig(
    val random: Random,
    val colorPalette: ColorPalette = ColorPalette.Adrift,
    val maxCount: Int = 100,
    val hourCount: Int = 50,
    val minuteCount: Int = 100
)

enum class ColorPalette(
    val mainColors: Array<Color>,
    val handleColor: Color,
    val dividerColor: Color,
    val borderColor: Color,
    val backgroundColor: Color

) {
    Adrift(
        arrayOf(
            Color(0xFF99B898),
            Color(0xFFFECEA8),
            Color(0xFFFF847C)
        ),
        Color(0xFFE84A5F),
        Color(0x39E84A5F),
        Color(0x41E84A5F),
        Color(0xff2A363B)
    ),
}

data class ParticleObject(
    val type: Type,
    val clockConfig: ClockConfig,
    var animationParams: AnimationParams = AnimationParams()
) {
    data class AnimationParams(
        var locationX: Dp = Dp(-1f),
        var locationY: Dp = Dp(-1f),
        var alpha: Float = -1f,
        var isFilled: Boolean = false,
        var currentColor: Color = Color(0),
        var particleSize: Dp = Dp(0f),
        var currentAngle: Float = 1f,
        var progressModifier: Float = 1f
    )

    enum class Type(
        val startAngleOffsetRadians: Float,
        val endAngleOffsetRadians: Float,
        val maxLengthModifier: Float,
        val minLengthModifier: Float,
        val minSize: Dp,
        val maxSize: Dp
    ) {
        Background(
            Math.PI.toFloat() * (0.5f / 12f),
            2 * Math.PI.toFloat() - (Math.PI.toFloat() * (0.5f / 12f)),
            0.8f,
            0.2f,
            Dp(4f),
            Dp(12f)
        ),
        Hour(
            0f,
            0f,
            0.6f,
            0.05f,
            Dp(8f),
            Dp(32f)
        ),
        Minute(
            0f,
            0f,
            0.75f,
            0.05f,
            Dp(8f),
            Dp(32f)
        )
    }
}

fun ParticleObject.randomize(
    random: Random,
    pxSize: PxSize
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
        Hour -> currentHourMinuteRadians.toFloat()
        Minute -> currentMinuteRadians.toFloat()
        Background -> (currentHourMinuteRadians + randomAngleOffset).toFloat()
    }
    val centerX = (pxSize.width / 2).value + randomFloat(-10f, 10f, random)
    val centerY = (pxSize.height / 2).value + randomFloat(-10f, 10f, random)
    val radius = min(centerX, centerY)
    val randomLength =
        randomFloat(type.minLengthModifier * radius, this.type.maxLengthModifier * radius, random)
    val x = randomLength * cos(randomizedAngle)
    val y = randomLength * sin(randomizedAngle)
    val color = when (type) {
        Background -> clockConfig.colorPalette.mainColors.random()
        Hour -> clockConfig.colorPalette.handleColor
        Minute -> clockConfig.colorPalette.handleColor
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