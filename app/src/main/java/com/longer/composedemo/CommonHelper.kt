package com.longer.composedemo

import android.graphics.Region
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp


@Composable
internal fun Dp.toPx(): Float {
    return this.value * LocalDensity.current.density
}


/**
 * 传入一个触摸点的坐标，以及圆心的坐标，返回一个角度
 * 可以设置起始角度  todo 可以设置角度
 */
/**
 * 算出该点与水平的角度的值，用移动点角度减去起始点角度就是旋转角度。
 */
fun getRotate(x1: Double, y1: Double, x2: Double, y2: Double, centerX: Double, centerY: Double): Double {
//    Log.d("getRotateAngle", "x1=$x1,y1=$y1,x2=$x2,y2=$y2,centerX=$centerX,centerY=$centerY")
    val abx: Double = centerX - x1
    val aby: Double = centerY - y1
    val acx: Double = centerX - x2
    val acy: Double = centerY - y2
    val bcx = x2 - x1
    val bcy = y2 - y1
    val c = Math.hypot(abx, aby)
    val b = Math.hypot(acx, acy)
    val a = Math.hypot(bcx, bcy)
    var cos1 = (c * c + b * b - a * a) / (2 * b * c)
//    Log.i("TAG", "c == $c")
//    Log.i("TAG", "b == $b")
//    Log.i("TAG", "a == $a")
//    Log.i("TAG", "cos == $cos1")
    if (cos1 >= 1) {
        cos1 = 1.0
    }
    val radian = Math.acos(cos1)
    return Math.toDegrees(radian)
}


/**
 * 获取验证区域
 */
@Composable
fun getVertyRegion(
    circleSize: Dp,
    regionCircleList: List<Region>
) {
    // 默认 外圈放大倍数
    val scale = 3f
    // 外圈区域
    val verityRectOut = Rect(
        center = Offset(circleSize.toPx() / 2, circleSize.toPx() / 2),
        radius = circleSize.toPx() / 2 * scale,
    )
    val verityCirclePathOut = Path().apply {
        addArc(
            verityRectOut,
            0f,
            360f,
        )
    }
    // 内区域
    val verityRectIn = Rect(
        center = Offset(circleSize.toPx() / 2, circleSize.toPx() / 2),
        radius = circleSize.toPx() / 2 * 0.4f,
    )
    val verityCirclePathIn = Path().apply {
        addArc(
            verityRectIn,
            0f,
            360f,
        )
    }
    val verityRegionRect = Region(
        -100 * scale.toInt(),
        -100 * scale.toInt(),
        (circleSize.toPx() * scale).toInt(),
        (circleSize.toPx() * scale).toInt(),
    )
    regionCircleList[0].setPath(verityCirclePathOut.asAndroidPath(), Region(verityRegionRect))
    regionCircleList[1].setPath(verityCirclePathIn.asAndroidPath(), Region(verityRegionRect))
}


/**
 * 验证是不是在圆形区域内
 */
fun verityCircle(regionCircleList: List<Region>, x: Float, y: Float): Boolean {
    val verity: Boolean
    if (regionCircleList[0].contains(x.toInt(), y.toInt())) {
        Log.d("verityCircle", "外圈区域 >> 通过")
        if (regionCircleList[1].contains(x.toInt(), y.toInt())) {
            Log.d("verityCircle", "内圈区域 >> 不通过")
            verity = false
        } else {
            Log.d("verityCircle", "内圈区域 >> 通过")
            verity = true
        }
    } else {
        Log.d("verityCircle", "外圈区域 >> 未通过")
        verity = false
    }
    return verity

}
