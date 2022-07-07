package com.fphoenixcorneae.navigation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.ArcShape
import android.graphics.drawable.shapes.RoundRectShape
import android.util.TypedValue
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.ImageView
import android.widget.TextView

fun TextView.changeTextSize(from: Float, to: Float) =
    ValueAnimator.ofFloat(from, to).run {
        duration = 150
        addUpdateListener { animator ->
            setTextSize(TypedValue.COMPLEX_UNIT_PX, animator.animatedValue as Float)
        }
        start()
    }

fun TextView.changeTextColor(fromColor: Int, toColor: Int) =
    ValueAnimator.ofObject(ArgbEvaluator(), fromColor, toColor).run {
        duration = 150
        addUpdateListener { animator ->
            setTextColor(animator.animatedValue as Int)
        }
        start()
    }

fun ImageView.changeImageColorFilter(fromColor: Int, toColor: Int) =
    ValueAnimator.ofObject(ArgbEvaluator(), fromColor, toColor).run {
        duration = 150
        addUpdateListener { animator ->
            setColorFilter(animator.animatedValue as Int)
        }
        start()
    }

/**
 * 开始揭露动画
 */
fun View.startCircularReveal(
    centerX: Int,
    centerY: Int,
    startRadius: Float,
    endRadius: Float,
    onAnimationStart: (() -> Unit)? = {},
    onAnimationEnd: (() -> Unit)? = {},
) =
    ViewAnimationUtils.createCircularReveal(this, centerX, centerY, startRadius, endRadius).run {
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                onAnimationStart?.invoke()
            }

            override fun onAnimationEnd(animation: Animator?) {
                onAnimationEnd?.invoke()
            }
        })
        start()
    }


fun View.changeViewBackgroundColor(fromColor: Int, toColor: Int) =
    ValueAnimator.ofObject(ArgbEvaluator(), fromColor, toColor).run {
        duration = 150
        addUpdateListener { animator ->
            setBackgroundColor(animator.animatedValue as Int)
        }
        start()
    }


fun View.changeViewTopPadding(fromPadding: Float, toPadding: Float) =
    ValueAnimator.ofFloat(fromPadding, toPadding).run {
        duration = 150
        addUpdateListener { animator ->
            val animatedValue = animator.animatedValue as Float
            setPadding(paddingLeft, animatedValue.toInt(), paddingRight, paddingBottom)
        }
        start()
    }

fun View.changeRightPadding(fromPadding: Float, toPadding: Float) =
    ValueAnimator.ofFloat(fromPadding, toPadding).run {
        duration = 150
        addUpdateListener { animator ->
            val animatedValue = animator.animatedValue as Float
            setPadding(paddingLeft, paddingTop, animatedValue.toInt(), paddingBottom)
        }
        start()
    }

/**
 * 设置圆角矩形背景
 */
fun View.setRoundRectBg(dpRadius: Float, bgColor: Int) = kotlin.run {
    val radius = NavigationUtil.dp2px(dpRadius)
    val radiusArray = floatArrayOf(
        radius, radius,
        radius, radius,
        radius, radius,
        radius, radius
    )
    val roundRect = RoundRectShape(radiusArray, null, null)
    ShapeDrawable(roundRect).also {
        it.paint.apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = bgColor
        }
        background = it
    }
}

/**
 * 设置椭圆形背景
 */
fun View.setOvalBg(bgColor: Int) = kotlin.run {
    val oval = ArcShape(0f, 360f)
    ShapeDrawable(oval).also {
        it.paint.apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = bgColor
        }
        background = it
    }
}

fun View.getActionbarSize(): Int = kotlin.run {
    var actionbarSize = -1
    val typedValue = TypedValue()
    if (context.theme.resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
        actionbarSize = TypedValue.complexToDimensionPixelSize(
            typedValue.data,
            context.resources.displayMetrics
        )
    }
    actionbarSize
}