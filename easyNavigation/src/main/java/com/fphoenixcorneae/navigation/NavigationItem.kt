package com.fphoenixcorneae.navigation

import androidx.annotation.Keep

/**
 * @desc：导航 Item
 * @date：2021-07-28 10:46
 */
@Keep
data class NavigationItem(
    var title: CharSequence?,
    var bgColor: Int,
    var imgRes: Int,
    var imgResActive: Int = 0,
)
