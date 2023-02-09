package com.longer.composedemo

import android.graphics.Path
import android.graphics.PathMeasure

/**
 * @Author: longer
 * @Date: 2023-01-12 09:53
 * @Description:  通过转化为Android 从而使用pathMeasure。 compose pathMeasure api 只有1个
 */
class PathUtils {
    private var path : Path? = null

    private val totalLength : Float
        get() {
            val pathMeasure = getPathMeasure()
            return pathMeasure.length
        }

    fun setPath(path: Path) {
        this.path = path
    }

    fun getStartPosition(process: Float): FloatArray {
        // 如果大于1，就取1，如果小于0，就取0
        val p = if (process > 1) 1f else if (process < 0) 0f else process

        val pathMeasure = getPathMeasure()
        val pos = FloatArray(2)
        pathMeasure.getPosTan(totalLength * p, pos, null)
        return pos
    }


    //  todo 可优化，耗时操作
    private fun getPathMeasure(): PathMeasure {
        val pathMeasure = PathMeasure()
        pathMeasure.setPath(path, true)
        return pathMeasure
    }


    // 构建单例 todo
    companion object {
        val instance: PathUtils by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED){
            PathUtils()
        }
    }
}