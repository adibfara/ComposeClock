package com.snakydesign.clock

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            ComposeClock()
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {
                Text(
                    modifier = Modifier,
                    text = "Compose Clock",
                    style = TextStyle(Color.White)
                )
                Text(
                    modifier = Modifier,
                    text = "github.com/adibfara/ComposeClock",
                    style = TextStyle(Color.White, fontSize = 12.sp)
                )

            }
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {

            }
        }
    }
}
