package com.alg.roundprogress

import android.graphics.Region
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import android.graphics.Path.Direction


/**
 * @Author: longer
 * @Date: 2023-01-11 20:58
 * @Description: 可触摸的圆形进度条
 * @todo:
 * 1. 获取布局约束
 * 2：减少 pathMeasure 计算
 * 3：可以设置角度和形状
 */
@Composable
fun TouchCircleProgress(
    // 圆形进度条的大小
    circleSize: Dp = Dp.Infinity,
    // 进度条的宽度
    progressWidth: Dp = 30.dp,
    // 进度条的背景颜色
    circleBgColor: Color = Color(0xFFE5E5E5),
    // 进度条的颜色
    processColor: Color = Color(0xFF00BFFF),
    // 进度条的进度
    startProcess: Float = 0f,
    // 开始角度 todo 暂时不支持，因为滑动的时候不好计算，目前都以12点方向为起始点
//    startAngle: Float = 0f,
    // 触摸点图片大小（为空，默认为线宽度的1.3倍）
    touchImgSize: Dp? = null,
    // 触摸点图片
    circleTouchImage: ImageBitmap = ImageBitmap.imageResource(R.drawable.ic_verify_anchor_default),
    // 时针方向
    startDirection: Direction = Direction.CW,
    // 背景颜色
    backgroundColor: Color = Color.Transparent,
    // 完成回调
    onProcessFinish: () -> Unit = {},
) {

    // 触摸点图片大小（touchImgSize 为空，默认为线宽度的1.3倍）
    var touchImgSizePx: Int

    // 当前进度 0f- 1f
    val process = remember { mutableStateOf(startProcess) }

    // 触摸点的位置信息
    var touchPosition: FloatArray
    // 触摸点的区域 触摸图标的位置 + 触摸图标的大小
    val regionTouch = remember { Region() }
    var regionTouchVerify = false

    // 可拖动区域 内外圈
    val regionCircleList = listOf(
        Region(),
        Region()
    )

    // 触摸事件num,当变动之后，就不在消费事件
    val dragNumber = remember { mutableStateOf(0) }

    // 滑动方向 默认顺时针
    val direction = remember { mutableStateOf(startDirection) }

    // 标记是否快结束
    val isNearlyFinish = remember { mutableStateOf(false) }

    getVertyRegion(circleSize, regionCircleList)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(circleSize)
            .background(backgroundColor)
            .pointerInput(dragNumber.value) {
                detectDragGestures(
                    onDragStart = { offset ->
                        LogUtils.d(">>  onDragStart  regionTouch >> $regionTouch")
                        if (!regionTouch.contains(offset.x.toInt(), offset.y.toInt())) {
                            LogUtils.d(">>  detectTapGestures: 不在区域内")
                            regionTouchVerify = false
                        } else {
                            LogUtils.d(">>  detectTapGestures: 在区域内")
                            regionTouchVerify = true
                        }
                    },
                    onDragEnd = {
                        LogUtils.d(">>  onDragEnd")
                        regionTouchVerify = false
                    },
                    onDragCancel = {
                        LogUtils.d(">>  onDragCancel")
                        regionTouchVerify = false
                    },
                    onDrag = { change, _ ->
                        val offset = change.position
                        if (!regionTouchVerify) {
                            LogUtils.d("拖动点 不在触摸区域内，不处理，不在消耗事件")
                            dragNumber.value++
                            return@detectDragGestures
                        }

                        // 验证是不是在圆形区域内, 不在就不处理
                        if (!verityCircle(regionCircleList, offset.x, offset.y)) {
                            LogUtils.d("不在触摸区域内，不处理，不在消耗事件")
                            dragNumber.value++
                            return@detectDragGestures
                        }

                        LogUtils.d(">>  detectTapGestures: $offset")

                        // 设置时针方向
                        if (process.value < 0.2f) {
                            if (offset.x >= circleSize.toPx() / 2) {
                                direction.value = Direction.CW
                            } else {
                                direction.value = Direction.CCW
                            }
                        }


                        var rotateAngle = getRotate(
                            x1 = offset.x.toDouble(),
                            y1 = offset.y.toDouble(),
                            x2 = (circleSize.toPx() / 2).toDouble(),
                            y2 = 0.0,
                            centerX = (circleSize.toPx() / 2).toDouble(),
                            centerY = (circleSize.toPx() / 2).toDouble()
                        )

                        LogUtils.d("得到的角度: $rotateAngle")

                        if (direction.value == Direction.CW) {
                            // 顺时针
                            if (offset.x < circleSize.toPx() / 2) {
                                rotateAngle = 180f - rotateAngle + 180f
                                LogUtils.d("顺时针 > 补偿180度之后: $rotateAngle")
                            }
                        } else {
                            // 逆时针
                            if (offset.x > circleSize.toPx() / 2) {
                                rotateAngle = 180f - rotateAngle + 180f
                                LogUtils.d("逆时针 > 补偿180度之后: $rotateAngle")
                            }
                        }
                        process.value = (rotateAngle / 360f).toFloat()

                        LogUtils.d(">>  当前进度 process: ${process.value}")

                        // 有时候拉的太快会监听不到，优化进度监听
                        // 进度超过75%，记录一下，如果之后进度小于25%就算通过
                        // 如果低于75%，也记录，清除快完成的标记
                        if (process.value < 0.25f && isNearlyFinish.value) {
                            LogUtils.d("拖动完成 >>> ")
                            dragNumber.value++
                            onProcessFinish()
                            isNearlyFinish.value = false
                            return@detectDragGestures
                        }

                        if (process.value >= 0.75f && !isNearlyFinish.value) {
                            isNearlyFinish.value = true
                        }

                        if (process.value < 0.75f && process.value > 0.25f && isNearlyFinish.value) {
                            isNearlyFinish.value = false
                        }

                    },
                )
            },
    ) {
        LogUtils.d("circleSize = ${circleSize.toPx()}px")


        // 触摸点图片大小（为空，默认为线宽度的1.3倍）
        touchImgSizePx = touchImgSize?.toPx()?.toInt() ?: (progressWidth.toPx() * 1.3f).toInt()

        val rect = Rect(
            center = Offset(circleSize.toPx() / 2, circleSize.toPx() / 2),
            radius = circleSize.toPx() / 2 - Math.max(progressWidth.toPx() / 2, touchImgSizePx / 2f),
        )

        val startAngle = 0f
        val circlePath = Path().apply {
            val sweepAngle = if (direction.value == Direction.CW) 360f else -360f
            addArc(
                rect,
                0f - 90f + startAngle,
                sweepAngle,
            )
        }

        Path().apply {
            addPath(circlePath)
        }

        // 通过计算path length画圆
        val pathMeasure = PathMeasure()
        pathMeasure.setPath(circlePath, true)
        val pathLength = pathMeasure.length
        val newPath = Path()
        pathMeasure.getSegment(0f, pathLength * process.value, newPath, true)

        // 设置验证区域
//        // 外圈区域 可以把上面方法拷贝过来，然后画出来
//        val verityRectOut = Rect(
//            center = Offset(circleSize.toPx() / 2, circleSize.toPx() / 2),
//            radius = circleSize.toPx() / 2 * 5f,
//        )
//        val verityCirclePathOut = Path().apply {
//            addArc(
//                verityRectOut,
//                0f,
//                360f,
//            )
//        }
//        // 内区域
//        val verityRectIn = Rect(
//            center = Offset(circleSize.toPx() / 2, circleSize.toPx() / 2),
//            radius = circleSize.toPx() / 2 * 0.4f,
//        )
//        val verityCirclePathIn = Path().apply {
//            addArc(
//                verityRectIn,
//                0f,
//                360f,
//            )
//        }
//        val verityRegionRect = Region(
//            -100,
//            -100,
//            (circleSize.toPx() * 1.3f).toInt(),
//            (circleSize.toPx() * 1.3f).toInt(),
//        )
//        regionCircleList[0].setPath(verityCirclePathOut.asAndroidPath(), Region(verityRegionRect))
//        regionCircleList[1].setPath(verityCirclePathIn.asAndroidPath(), Region(verityRegionRect))


        // 获取头部坐标 todo 优化性能
        val pathUtils = PathUtils.instance
        pathUtils.setPath(circlePath.asAndroidPath())
        touchPosition = pathUtils.getStartPosition(process.value)
        LogUtils.d("pos[0] = ${touchPosition[0]}, pos[1] = ${touchPosition[1]}")


        // 设置触摸区域
        regionTouch.set(
            touchPosition[0].toInt() - touchImgSizePx / 2,
            touchPosition[1].toInt() - touchImgSizePx / 2,
            touchPosition[0].toInt() + touchImgSizePx / 2,
            touchPosition[1].toInt() + touchImgSizePx / 2
        )


        Canvas(modifier = Modifier.size(circleSize)) {
            LogUtils.d("canvas size = $size")

            // 背景圆
            drawPath(
                path = circlePath, color = circleBgColor, style = Stroke(width = progressWidth.toPx())
            )

            // 进度圆
            drawPath(
                path = newPath, color = processColor, style = Stroke(
                    width = progressWidth.toPx(), cap = StrokeCap.Round
                )
            )

            // 头部图片,设置图片大小为圆环宽度的一半
            drawImage(
                image = circleTouchImage,
                dstOffset = IntOffset(
                    (touchPosition[0] - touchImgSizePx / 2f).toInt(), (touchPosition[1] - touchImgSizePx / 2f).toInt()
                ),
                dstSize = IntSize(touchImgSizePx, touchImgSizePx),
            )

        }
    }
}


@Preview
@Composable
fun DefaultPreview() {
    Box(
        Modifier.size(300.dp),
        contentAlignment = Alignment.Center,
    ) {
        TouchCircleProgress(
            circleSize = 200.dp,
            startProcess = 0.2f,
            progressWidth = 20.dp,
            touchImgSize = 42.dp,
            startDirection = Direction.CCW,
            onProcessFinish = {
                LogUtils.d("onProcessFinish")
            }
        )
    }
}