package com.snakydesign.clock

import android.graphics.Point
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.ui.animation.Transition
import androidx.ui.core.Text
import androidx.ui.core.setContent
import androidx.ui.foundation.Canvas
import androidx.ui.foundation.CanvasScope
import androidx.ui.geometry.Offset
import androidx.ui.graphics.Color
import androidx.ui.graphics.Paint
import androidx.ui.graphics.PaintingStyle
import androidx.ui.layout.LayoutSize
import androidx.ui.material.MaterialTheme
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.Dp
import java.util.*
import kotlin.math.*

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                DefaultPreview()
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}


fun createClockConfig(): ClockConfig {
    val startX = Dp(0f)
    val endX = Dp(400f)
    val startY = Dp(0f)
    val endY = Dp(400f)
    val random = Random()
    return ClockConfig(
        startX.value.toInt(),
        startY.value.toInt(),
        endX.value.toInt(),
        endY.value.toInt(),
        random
    )
}


fun updateParticleWithCanvas(particleObject: ParticleObject, paint: Paint, canvas: CanvasScope) {
    canvas.apply {
        paint.color = particleObject.animationParams.colorPalette.baseColor
        paint.alpha = particleObject.animationParams.alpha
        val centerW = particleObject.animationParams.locationX.toPx().value
        val centerH = particleObject.animationParams.locationY.toPx().value
        if (particleObject.animationParams.isFilled) {
            paint.style = PaintingStyle.fill
        } else {
            paint.style = PaintingStyle.stroke

        }
        drawCircle(
            Offset(centerW, centerH),
            particleObject.animationParams.particleSize.value / 2f,
            paint
        )
    }
}

@Preview
@Composable
fun DefaultPreview() {

    val clockConfig = createClockConfig()
    MaterialTheme {

        ParticleHeartBeat(
            clockConfig,
            1000,
            ParticleObject.Type.Background
        )
        ParticleHeartBeat(
            clockConfig,
            50,
            ParticleObject.Type.Hour
        )
        /*      MinuteParticles(clockConfig.copy(clockConfig.maxSize * 2f, color = Color(224,85, 38,150)))
              HourParticles(clockConfig.copy(clockConfig.maxSize * 3f, color = Color(93, 188, 210,125)))*/
    }
}