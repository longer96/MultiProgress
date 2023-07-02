package com.alg.roundprogress

import android.graphics.Point
import android.graphics.Region
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.alg.roundprogress.utils.LogUtils


@Composable
internal fun Dp.toPx(): Float {
    return this.value * LocalDensity.current.density
}

/**
 * 方法接收三个 Point 类型的参数，代表三个点的坐标。方法内部首先使用 atan2 函数计算出第一个点和第二个点之间的线段与水平方向的夹角，
 * 再计算出第一个点和第三个点之间的线段与水平方向的夹角。两个角度的差即为第三个点与线段之间的夹角。
 * 最后需要注意，如果夹角大于180度，则需要减去360度，如果夹角小于等于-180度，
 * 则需要加上360度，以确保夹角落在-180度到180度之间，同时也支持钝角的计算。
 * @param p1 线段起点
 * @param p2 线段终点
 * @param p3 需要计算的点
 * @return -180 ~ 180
 */
fun calculateAngle(p1: Point, p2: Point, p3: Point): Double {
    val angle1 = Math.atan2((p2.y - p1.y).toDouble(), (p2.x - p1.x).toDouble())
    val angle2 = Math.atan2((p3.y - p1.y).toDouble(), (p3.x - p1.x).toDouble())
    var angle = angle2 - angle1
    if (angle > Math.PI) {
        angle -= 2 * Math.PI
    } else if (angle <= -Math.PI) {
        angle += 2 * Math.PI
    }
    // Convert to degrees
    return angle * 180 / Math.PI
}

fun getRotate(angle: Double): Double {
    var mAngle = angle
    if (mAngle > 180) {
        mAngle = 360 - mAngle
    } else if (mAngle < 0) {
        mAngle += 360
    }
    return mAngle
}


/**
 * 获取验证区域
 * @param circleSize 圆的大小
 * @param regionCircleList 内圈、外圈验证区域
 * 外圈：放大倍数为半径3倍
 * 内圈：半径为外圈半径的0.4倍
 */
@Composable
fun setVerityRegion(
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
    return if (regionCircleList[0].contains(x.toInt(), y.toInt())) {
        LogUtils.d("外圈区域 >> 通过")
        if (regionCircleList[1].contains(x.toInt(), y.toInt())) {
            LogUtils.d("内圈区域 >> 不通过")
            false
        } else {
            LogUtils.d("内圈区域 >> 通过")
            true
        }
    } else {
        LogUtils.d("外圈区域 >> 未通过")
        false
    }

}


/**
 * 给定一个圆和一个角度，计算出圆上的点
 * @param p1: Point 圆心
 * @param radius: Double 半径
 * @param angle 角度
 * @return 圆上的点
 */
fun getPoint(p1: Point, radius: Float, angle: Double): Point {
    val centerX = p1.x // 圆心X坐标
    val centerY = p1.y // 圆心Y坐标

    // 计算起始角度，即12点钟方向的角度
    val startAngle = -Math.PI / 2

    // 将角度转换为弧度
    val radians = Math.toRadians(angle)

    // 计算点的坐标
    val x = centerX + radius * Math.cos(startAngle + radians)
    val y = centerY + radius * Math.sin(startAngle + radians)

    return Point(x.toInt(), y.toInt())
}