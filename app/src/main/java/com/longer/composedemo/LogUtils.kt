package com.longer.composedemo

/**
 * @Author: longer
 * @Date: 2023-03-01 21:11
 * @Description:
 */
object LogUtils {

    private const val TAG = "TouchPathProgress"

    private var DEBUG = true

    fun setDebug(debug: Boolean) {
        DEBUG = debug
    }

    fun d(msg: String) {
        if (DEBUG) {
            android.util.Log.d(TAG, msg)
        }
    }

    fun i(msg: String) {
        if (DEBUG) {
            android.util.Log.i(TAG, msg)
        }
    }

    fun e(msg: String) {
        if (DEBUG) {
            android.util.Log.e(TAG, msg)
        }
    }
}