package com.fphoenixcorneae.bottomnavigation

import androidx.annotation.Keep

@Keep
data class BottomNavigationItem(
    var title: CharSequence?,
    var color: Int,
    var imageResource: Int,
    var imageResourceActive: Int = 0,
)
