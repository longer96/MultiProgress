package com.longer.composedemo

import android.graphics.Path
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alg.roundprogress.TouchCircleProgress
import com.longer.composedemo.ui.theme.ComposeDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Greeting()
        }
    }
}

@Composable
fun Greeting() {

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        val size = with(LocalDensity.current) { 500.toDp() }
        TouchCircleProgress(
            circleSize = size,
            startProcess = 0f,
            progressWidth = 20.dp,
            touchImgSize = 50.dp,
            startDirection = Path.Direction.CW,
            processColor = Color(0xFF7CC7E2),
            circleBgColor = Color(0xFFEEF4F5),
            onProcessFinish = {
            }
        )
        Text(text = stringResource(R.string.app_name), fontSize = 17.sp)
    }


}


@Preview()
@Composable
fun DefaultPreview2() {
    ComposeDemoTheme {
    }
}