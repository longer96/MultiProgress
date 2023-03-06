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
import android.graphics.Point
import androidx.compose.foundation.layout.BoxWithConstraints


/**
 * @Author: longer
 * @Date: 2023-01-11 20:58
 * @Description: 可触摸的圆形进度条
 */
@Composable
fun TouchRoundProgress(
    // 圆形进度条的大小,默认为父布局的最小值
    circleSize: Dp = 0.dp,
    // 进度条的宽度
    progressWidth: Dp = 30.dp,
    // 进度条的背景颜色
    circleBgColor: Color = Color(0xFFE5E5E5),
    // 进度条的颜色
    processColor: Color = Color(0xFF00BFFF),
    // 进度条的进度
    startProcess: Float = 0f,
    // 开始角度
    startAngle: Float = 0f,
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
    BoxWithConstraints {
        // 圆形进度条的大小,默认为父布局的最小值
        var circleSize = circleSize

        // 触摸点图片大小（touchImgSize 为空，默认为线宽度的1.3倍）
        val touchImgSizePx: Int = touchImgSize?.toPx()?.toInt() ?: (progressWidth.toPx() * 1.3f).toInt()
        // 当前进度 0f- 1f
        val process = remember { mutableStateOf(startProcess) }

        // 触摸点的位置信息
        var touchPosition: FloatArray
        // 触摸点的区域 触摸图标的位置 + 触摸图标的大小
        val regionTouch = remember { Region() }
        var regionTouchVerify = false

        // 可拖动区域 内外圈
        val regionCircleList = listOf(Region(), Region())

        // 触摸事件num,当变动之后，就不在消费事件
        val dragNumber = remember { mutableStateOf(0) }

        // 滑动方向 默认顺时针
        val direction = remember { mutableStateOf(startDirection) }

        // 标记是否快结束
        val isNearlyFinish = remember { mutableStateOf(false) }

        // 如果没有设置圆形进度条的大小,默认为父布局的最小值
        if (circleSize.toPx() <= 0) {
            circleSize = if (maxWidth > maxHeight) maxHeight else maxWidth
            LogUtils.d("maxWidth:$maxWidth,maxHeight:$maxHeight")
        }

        LogUtils.d("circleSize = ${circleSize.toPx()}px")
        // 设置验证区域大小（可触摸安全范围）
        setVerityRegion(circleSize, regionCircleList)


        // 绘制底部圆
        val radius = circleSize.toPx() / 2 - Math.max(progressWidth.toPx() / 2, touchImgSizePx / 2f)
        val rect = Rect(
            center = Offset(circleSize.toPx() / 2, circleSize.toPx() / 2),
            radius = radius,
        )

        // 画圆 如果startAngle=0,起始位置默认为12点方向
        val circlePath = Path().apply {
            val sweepAngle = if (direction.value == Direction.CW) 360f else -360f
            addArc(
                rect,
                0f - 90f + startAngle,
                sweepAngle,
            )
        }

        // 通过计算path length画圆
        val pathMeasure = PathMeasure()
        pathMeasure.setPath(circlePath, true)

        // 测量出来的path长度
        val pathLength = pathMeasure.length

        // 获取头部坐标，这里通过转Android的Path，然后获取头部坐标（因为compose的path目前没有api）
        val pathUtils = PathUtils(circlePath.asAndroidPath())

        // 计算得到的头部坐标
        val headPoint = getPoint(
            p1 = Point((circleSize.toPx() / 2).toInt(), (circleSize.toPx() / 2).toInt()),
            radius = radius,
            angle = startAngle.toDouble()
        )


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

                            // 验证是不是在圆形区域内, 不在就不处理
                            if (!verityCircle(regionCircleList, offset.x, offset.y)) {
                                LogUtils.d("不在触摸区域内，不处理，不在消耗事件")
                                dragNumber.value++
                                return@detectDragGestures
                            }


                            if (!regionTouchVerify) {
                                LogUtils.d("拖动点 不在触摸区域内，不处理，不在消耗事件")
                                dragNumber.value++
                                return@detectDragGestures
                            }

                            LogUtils.d(">>  detectTapGestures: $offset")


                            var rotateAngle = calculateAngle(
                                p1 = Point((circleSize.toPx() / 2).toInt(), (circleSize.toPx() / 2).toInt()),
                                p2 = Point(headPoint.x, headPoint.y),
                                p3 = Point(offset.x.toInt(), offset.y.toInt()),
                            )
                            LogUtils.d("得到的角度: $rotateAngle")

                            if (process.value < 0.2f) {
                                if (rotateAngle >= 0f) {
                                    // 顺时针
                                    if (direction.value != Direction.CW) direction.value = Direction.CW
                                } else {
                                    // 逆时针
                                    if (direction.value != Direction.CCW) direction.value = Direction.CCW
                                }
                            }


                            if (direction.value == Direction.CW) {
                                // 顺时针
                                rotateAngle = getRotate(rotateAngle)
                                LogUtils.d("顺时针 > 补偿之后的角度: $rotateAngle")
                            } else {
                                // 逆时针
                                rotateAngle = 360f - getRotate(rotateAngle)
                                LogUtils.d("逆时针 > 补偿之后的角度: $rotateAngle")
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


            // 获取头部坐标
            touchPosition = pathUtils.getStartPosition(process.value)
            LogUtils.d("进度条头部坐标点 x = ${touchPosition[0]}, y = ${touchPosition[1]}")


            // 设置可触摸区域
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
                        (touchPosition[0] - touchImgSizePx / 2f).toInt(),
                        (touchPosition[1] - touchImgSizePx / 2f).toInt()
                    ),
                    dstSize = IntSize(touchImgSizePx, touchImgSizePx),
                )

            }
        }
    }
}


@Preview
@Composable
fun DefaultPreview() {
    Box(
        Modifier.size(200.dp),
        contentAlignment = Alignment.Center,
    ) {
        TouchRoundProgress(
            startProcess = 0.2f,
            startAngle = 90f,
            progressWidth = 20.dp,
            touchImgSize = 42.dp,
            startDirection = Direction.CCW,
            onProcessFinish = {
                LogUtils.d("onProcessFinish")
            }
        )
    }
}