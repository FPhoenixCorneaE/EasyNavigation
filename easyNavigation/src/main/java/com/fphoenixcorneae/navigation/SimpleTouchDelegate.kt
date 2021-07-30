package com.fphoenixcorneae.navigation

import android.graphics.Rect
import android.view.TouchDelegate
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration

/**
 * @desc：SimpleTouchDelegate
 * @date：2021/07/30 13:43
 */
class SimpleTouchDelegate(
    targetBounds: Rect,
    /**
     * View that should receive forwarded touch events
     */
    private val mDelegateView: View
) : TouchDelegate(targetBounds, mDelegateView) {
    /**
     * Bounds in local coordinates of the containing view that should be mapped to the delegate
     * view. This rect is used for initial hit testing.
     */
    private val mTargetBounds = Rect()

    /**
     * Bounds in local coordinates of the containing view that are actual bounds of the delegate
     * view. This rect is used for event coordinate mapping.
     */
    private val mActualBounds = Rect()

    /**
     * mTargetBounds inflated to include some slop. This rect is to track whether the motion events
     * should be considered to be be within the delegate view.
     */
    private val mSlopBounds = Rect()
    private val mSlop: Int = ViewConfiguration.get(mDelegateView.context).scaledTouchSlop

    /**
     * True if the delegate had been targeted on a down event (intersected mTargetBounds).
     */
    private var mDelegateTargeted = false

    fun setBounds(desiredBounds: Rect) {
        mTargetBounds.set(desiredBounds)
        mSlopBounds.set(desiredBounds)
        mSlopBounds.inset(-mSlop, -mSlop)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()
        var sendToDelegate = false
        var hit = true
        var handled = false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> if (mTargetBounds.contains(x, y)) {
                mDelegateTargeted = true
                sendToDelegate = true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_MOVE -> {
                sendToDelegate = mDelegateTargeted
                if (sendToDelegate) {
                    if (!mSlopBounds.contains(x, y)) {
                        hit = false
                    }
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                sendToDelegate = mDelegateTargeted
                mDelegateTargeted = false
            }
            else -> {
            }
        }
        if (sendToDelegate) {
            if (hit.not()) {
                val slop = mSlop
                event.setLocation(-(slop * 2).toFloat(), -(slop * 2).toFloat())
            }
            handled = mDelegateView.dispatchTouchEvent(event)
        }
        return handled
    }

    init {
        setBounds(targetBounds)
    }
}