

可触摸的圆形进度条
==


## 功能介绍
```kotlin
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
    onProcessFinish: (() -> Unit)? = null,
    // 进度回调
    onProcessChange: ((process: Float) -> Unit)? = null
```