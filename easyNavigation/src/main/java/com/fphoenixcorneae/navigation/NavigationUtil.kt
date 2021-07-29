package com.fphoenixcorneae.navigation

import android.content.res.Resources

/**
 * @desc：导航工具类
 * @date：2021-07-28 10:46
 */
object NavigationUtil {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    fun dp2px(dpValue: Float): Float {
        val scale = Resources.getSystem().displayMetrics.density
        return dpValue * scale + 0.5f
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位转成为 dp
     */
    fun px2dp(pxValue: Float): Float {
        val scale = Resources.getSystem().displayMetrics.density
        return pxValue / scale + 0.5f
    }

    /**
     * 将 sp 值转换为 px 值，保证文字大小不变
     */
    fun sp2px(spValue: Float): Float {
        val fontScale = Resources.getSystem().displayMetrics.scaledDensity
        return spValue * fontScale + 0.5f
    }

    /**
     * 将 px 值转换为 sp 值，保证文字大小不变
     */
    fun px2sp(pxValue: Float): Float {
        val fontScale = Resources.getSystem().displayMetrics.scaledDensity
        return pxValue / fontScale + 0.5f
    }
}