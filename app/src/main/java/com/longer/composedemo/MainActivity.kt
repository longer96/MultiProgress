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
import com.longer.composedemo.ui.theme.ComposeDemoTheme
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
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
    }
}

@Composable
fun Greeting(name: String) {

    //进度条进度
    val progress = remember { mutableStateOf(0.1f) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        //圆形进度条--无限循环
        CircularProgressIndicator()
        Spacer(modifier = Modifier.requiredHeight(10.dp))
        //圆形进度条--按进度变化
        CircularProgressIndicator(progress = progress.value)
        Spacer(modifier = Modifier.requiredHeight(10.dp))
        TextButton(
            onClick = { if (progress.value < 1.0f) progress.value = progress.value + 0.1f },
            modifier = Modifier.background(Color.LightGray)
        ) {
            Text(text = "增加进度")
        }
    }

}


@Preview()
@Composable
fun DefaultPreview2() {
    ComposeDemoTheme {
        Greeting("Android")
    }
}