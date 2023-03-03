package com.alg.roundprogress

import android.graphics.Path
import android.graphics.PathMeasure

/**
 * @Author: longer
 * @Date: 2023-01-12 09:53
 * @Description:  通过转化为Android 从而使用pathMeasure。 compose pathMeasure api 只有1个
 */
class PathUtils(path: Path) {
    private val pathMeasure: PathMeasure = PathMeasure()

    init {
        pathMeasure.setPath(path, true)
    }

    private val totalLength: Float
        get() {
            return pathMeasure.length
        }

    fun getStartPosition(process: Float): FloatArray {
        // 如果大于1，就取1，如果小于0，就取0
        val p = if (process > 1) 1f else if (process < 0) 0f else process

        val pos = FloatArray(2)
        pathMeasure.getPosTan(totalLength * p, pos, null)
        return pos
    }

}