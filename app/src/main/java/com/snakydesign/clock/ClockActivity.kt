package com.snakydesign.clock

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.ui.animation.Transition
import androidx.ui.core.Alignment
import androidx.ui.core.Draw
import androidx.ui.core.Text
import androidx.ui.core.setContent
import androidx.ui.geometry.Offset
import androidx.ui.graphics.Canvas
import androidx.ui.graphics.Color
import androidx.ui.graphics.Paint
import androidx.ui.graphics.PaintingStyle
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.material.darkColorPalette
import androidx.ui.text.TextStyle
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.*
import java.util.*
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

class ClockActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                DefaultPreview()
            }
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {

    MaterialTheme() {
        Stack() {
            ComposeClock()

            Align(alignment = Alignment.BottomLeft) {
                Column {
                    Text(
                        modifier = LayoutPadding(Dp(16f)),
                        text = "Compose Clock",
                        style = TextStyle(Color.White)
                    )
                    Text(
                        modifier = LayoutPadding(Dp(16f)),
                        text = "github.com/adibfara/ComposeClock",
                        style = TextStyle(Color.White, TextUnit.Companion.Sp(12f))
                    )

                }
            }
        }
    }
}
