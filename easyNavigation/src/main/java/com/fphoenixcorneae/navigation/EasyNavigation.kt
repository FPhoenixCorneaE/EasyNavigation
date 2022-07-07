package com.fphoenixcorneae.navigation

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * @desc：EasyNavigation
 * @date：2021-07-28 14:10
 */
class EasyNavigation @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : RelativeLayout(context, attrs, defStyleAttr) {

    companion object {
        const val MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT
        const val ICON_SCALE_ANIMATION_DURATION = 150L
        const val ICON_ACTIVE_SCALE = 1.1f
        const val ICON_INACTIVE_SCALE = 0.9f
    }

    /**
     * navigation height、container、colored background temp view
     */
    private var mNavigationHeight = 0
    private lateinit var mNavigationContainer: FrameLayout
    private lateinit var mNavigationItemParent: LinearLayout
    private lateinit var mNavigationColoredBackgroundTempView: View

    /**
     * line：height、color、view
     */
    private var mNavigationLineHeight = 0
    private var mNavigationLineColor = 0
    private lateinit var mNavigationLineView: View

    /**
     * item：onClickListener
     */
    private var mOnItemClickListener: ((Int) -> Unit)? = null

    /**
     * item：List、ViewList
     */
    private lateinit var mNavigationItems: ArrayList<NavigationItem>
    private lateinit var mNavigationItemViews: ArrayList<View>

    /**
     * item：activeColor、inactiveColor
     */
    private var mItemActiveColor = 0
    private var mItemInactiveColor = 0

    /**
     * itemText：activeSize、inactiveSize、font
     */
    private var mTextActiveSize = 0f
    private var mTextInactiveSize = 0f
    private var mTextFont: Typeface? = null

    /**
     * itemIcon：activeScale、inactiveScale
     */
    private var mIconActiveScale = 0f
    private var mIconInactiveScale = 0f

    /**
     * view padding top
     */
    private var mViewActivePaddingTop = 0
    private var mViewInactivePaddingTop = 0
    private var mViewInactivePaddingTopWithoutText = 0

    /**
     * shadow view
     */
    private lateinit var mShadowView: View

    /**
     * shadow height
     */
    private var mShadowHeight = 0

    /**
     * Whether to show shadow
     */
    private var mShowShadow = true

    /**
     * Whether to show text
     */
    private var mWithText = true

    /**
     * Whether to colored the navigation background
     */
    private var mColoredBackground = true

    /**
     * Whether to activate tablet mode
     */
    private var isTablet = false

    /**
     * Whether to smoothly scroll to the new item
     */
    private var mSmoothScroll = true

    /**
     * pager to connect to [EasyNavigation]
     */
    private var mViewPager: ViewPager? = null
    private var mViewPager2: ViewPager2? = null

    /**
     * the current item position
     */
    private var mCurrentItem = 0

    /**
     * center view
     */
    private var mCenterView: View? = null

    /**
     * center view onClick
     */
    private var mOnCenterViewClickListener: (() -> Unit)? = null

    init {
        defaultSetting()
        initAttrs(context, attrs)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.EasyNavigation)
            mWithText = array.getBoolean(R.styleable.EasyNavigation_en_with_text, mWithText)
            mColoredBackground = array.getBoolean(R.styleable.EasyNavigation_en_colored_background, mColoredBackground)
            mShowShadow = array.getBoolean(R.styleable.EasyNavigation_en_shadow, mShowShadow)
            isTablet = array.getBoolean(R.styleable.EasyNavigation_en_tablet, isTablet)
            mSmoothScroll = array.getBoolean(R.styleable.EasyNavigation_en_viewpager_slide, mSmoothScroll)
            mItemActiveColor = array.getColor(R.styleable.EasyNavigation_en_item_active_color, mItemActiveColor)
            mItemInactiveColor = array.getColor(R.styleable.EasyNavigation_en_item_inactive_color, mItemInactiveColor)
            mTextActiveSize = array.getDimension(R.styleable.EasyNavigation_en_active_text_size, mTextActiveSize)
            mTextInactiveSize = array.getDimension(R.styleable.EasyNavigation_en_inactive_text_size, mTextInactiveSize)
            array.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val (measuredWidth, measuredHeight) = if (isTablet) {
            // width、height in tablet mode
            (mNavigationHeight + mNavigationLineHeight) to MATCH_PARENT
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                elevation = resources.getDimension(R.dimen.en_elevation)
            }
            // width、height in non tablet mode
            MATCH_PARENT to kotlin.run {
                val centerViewTranslationY = (mCenterView?.translationY?.absoluteValue ?: 0f).roundToInt()
                if (mShowShadow) {
                    mNavigationHeight + max(centerViewTranslationY, mShadowHeight)
                } else {
                    mNavigationHeight + centerViewTranslationY
                }
            }
        }
        layoutParams = layoutParams.apply {
            width = measuredWidth
            height = measuredHeight
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        removeAllViews()
        // add shadow or line
        addShadowOrLine()
        // add navigation container
        addNavigationContainer()
        // add navigation colored bg view
        addNavigationColoredBgView()
        // add navigation item parent
        addNavigationItemParent()
        // add navigation item
        addNavigationItem()
        // add center view
        addCenterView()
    }

    /**
     * add shadow or line
     */
    private fun addShadowOrLine() {
        kotlin.run {
            if (isTablet) {
                mNavigationLineView.apply {
                    layoutParams = LayoutParams(mNavigationLineHeight, MATCH_PARENT).apply {
                        addRule(ALIGN_PARENT_RIGHT)
                    }
                    setBackgroundColor(mNavigationLineColor)
                }
            } else {
                mShadowView.apply {
                    layoutParams = LayoutParams(MATCH_PARENT, mShadowHeight).apply {
                        addRule(ABOVE, mNavigationContainer.id)
                        setBackgroundResource(R.drawable.en_shape_shadow)
                    }
                }
            }
        }.also {
            addView(it)
        }
    }

    /**
     * add navigation container
     */
    private fun addNavigationContainer() {
        mNavigationContainer.apply {
            removeAllViews()
            layoutParams = kotlin.run {
                if (isTablet) {
                    LayoutParams(mNavigationHeight, MATCH_PARENT).apply {
                        addRule(ALIGN_PARENT_LEFT)
                    }
                } else {
                    LayoutParams(MATCH_PARENT, mNavigationHeight).apply {
                        addRule(ALIGN_PARENT_BOTTOM)
                    }
                }
            }
        }.also {
            addView(it)
        }
    }

    /**
     * add navigation colored bg view
     */
    private fun addNavigationColoredBgView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mNavigationColoredBackgroundTempView.apply {
                layoutParams = if (isTablet) {
                    LayoutParams(mNavigationHeight, MATCH_PARENT).apply {
                        addRule(ALIGN_PARENT_LEFT)
                    }
                } else {
                    LayoutParams(MATCH_PARENT, mNavigationHeight).apply {
                        addRule(ALIGN_PARENT_BOTTOM)
                    }
                }
            }.also {
                mNavigationContainer.addView(it)
            }
        }
    }

    /**
     * add navigation item parent
     */
    private fun addNavigationItemParent() {
        mNavigationItemParent.apply {
            removeAllViews()
            if (isTablet) {
                orientation = LinearLayout.VERTICAL
                layoutParams = LayoutParams(mNavigationHeight, MATCH_PARENT)
                setPadding(0, mNavigationHeight / 2, 0, 0)
            } else {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LayoutParams(MATCH_PARENT, mNavigationHeight)
            }
        }.also {
            mNavigationContainer.addView(it)
        }
    }

    /**
     * add navigation item
     */
    private fun addNavigationItem() {
        checkCurrentItem()
        checkItemSize()
        val inflater = LayoutInflater.from(context)
        mNavigationItemViews.clear()
        for (i in 0 until mNavigationItems.size) {
            if (!mColoredBackground) {
                mNavigationItems[i].bgColor = Color.WHITE
            }
            if (i == mCurrentItem) {
                mNavigationContainer.setBackgroundColor(mNavigationItems[i].bgColor)
            }

            val view = inflater.inflate(R.layout.en_item_layout_navigation, mNavigationItemParent, false)
            val icon = view.findViewById<View>(R.id.en_item_icon) as? ImageView
            val title = view.findViewById<View>(R.id.en_item_title) as? TextView
            icon?.apply {
                if (mNavigationItems[i].imgResActive != 0) {
                    if (i == mCurrentItem) {
                        setImageResource(mNavigationItems[i].imgResActive)
                    } else {
                        setImageResource(mNavigationItems[i].imgRes)
                    }
                } else {
                    setImageResource(mNavigationItems[i].imgRes)
                    setColorFilter(if (i == mCurrentItem) {
                        mItemActiveColor
                    } else {
                        mItemInactiveColor
                    })
                }

                if (i == mCurrentItem) {
                    scaleX = mIconActiveScale
                    scaleY = mIconActiveScale
                } else {
                    scaleX = mIconInactiveScale
                    scaleY = mIconInactiveScale
                }
            }
            title?.apply {
                typeface = mTextFont
                paint.isFakeBoldText = true
                text = mNavigationItems[i].title
                if (i == mCurrentItem) {
                    setTextColor(mItemActiveColor)
                } else {
                    setTextColor(mItemInactiveColor)
                }
                val textSize = when {
                    i == mCurrentItem -> mTextActiveSize
                    mWithText -> mTextInactiveSize
                    else -> 0f
                }
                setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            }

            view.apply {
                layoutParams = layoutParams.apply {
                    if (isTablet) {
                        width = MATCH_PARENT
                        height = mNavigationHeight
                    } else {
                        width = 0
                        height = MATCH_PARENT
                        (this as? LinearLayout.LayoutParams)?.weight = 1f
                    }
                }
                setPadding(
                    paddingLeft,
                    run {
                        if (isTablet) {
                            paddingTop
                        } else {
                            when {
                                i == mCurrentItem -> mViewActivePaddingTop
                                mWithText -> mViewInactivePaddingTop
                                else -> mViewInactivePaddingTopWithoutText
                            }
                        }
                    },
                    paddingRight,
                    paddingBottom
                )
                setOnClickListener {
                    onNavigationItemClick(i)
                }
            }.also {
                mNavigationItemViews.add(it)
                mNavigationItemParent.addView(it)
            }
        }
    }

    /**
     * add center view
     */
    private fun addCenterView() {
        mCenterView?.let {
            // 解决 clipChildren 超出 itemParent 部分无法响应点击问题
            it.post {
                val hitRect = Rect()
                // 获取布局当前有效可点击区域
                it.getHitRect(hitRect)
                // 扩大布局点击区域
                hitRect.top += it.translationY.toInt()
                val touchDelegate: TouchDelegate = SimpleTouchDelegate(hitRect, it)
                this.isClickable = true
                // 拦截事件分发
                this.touchDelegate = touchDelegate
            }
            it.setOnClickListener {
                mOnCenterViewClickListener?.invoke()
            }
            // 解决某些Android设备超出parent不响应点击问题
            it.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    v.performClick()
                }
                return@setOnTouchListener true
            }
            val index = mNavigationItems.size / 2
            mNavigationItemParent.apply {
                disableClipChildren()
                addView(it, index)
            }
        }
    }

    /**
     * not to clip children to their bounds
     */
    private fun ViewGroup?.disableClipChildren() = run {
        var parentView: ViewGroup? = this
        while (parentView != null) {
            parentView.clipChildren = false
            parentView = (parentView.parent as? ViewGroup)
        }
    }

    /**
     * check item size
     */
    @Throws(RuntimeException::class)
    private fun checkItemSize() {
        if (mNavigationItems.isEmpty()) {
            throw RuntimeException("You need at least one item")
        }
    }

    /**
     * check current item
     */
    @Throws(IndexOutOfBoundsException::class)
    private fun checkCurrentItem() {
        if (mCurrentItem !in 0 until mNavigationItems.size) {
            throw IndexOutOfBoundsException(
                if (mCurrentItem < 0) {
                    "Position must be 0 or greater than 0, current is $mCurrentItem."
                } else {
                    "Position must be less than items size, items size is ${mNavigationItems.size}, current is $mCurrentItem."
                }
            )
        }
    }

    /**
     * on navigation item click
     */
    private fun onNavigationItemClick(position: Int) {
        if (mCurrentItem == position) {
            return
        }
        for (i in mNavigationItemViews.indices) {
            val view = mNavigationItemViews[i].findViewById<View>(R.id.en_container)
            val title = view.findViewById<View>(R.id.en_item_title) as TextView
            val icon = view.findViewById<View>(R.id.en_item_icon) as ImageView
            when (i) {
                position -> {
                    // change click item icon and text
                    changeClickItemIconAndText(position, icon, title)
                    // change click item view padding
                    changeClickItemViewPadding(view)
                    // change navigation background color
                    changeNavigationBackgroundColor(position)
                }
                mCurrentItem -> {
                    // change current item icon and text
                    changeCurrentItemIconAndText(icon, title)
                    // change current item view padding
                    changeCurrentItemViewPadding(view)
                }
            }
        }

        mViewPager?.setCurrentItem(position, mSmoothScroll)
        mViewPager2?.setCurrentItem(position, mSmoothScroll)
        mOnItemClickListener?.invoke(position)
        mCurrentItem = position
    }

    /**
     * change click item icon and text
     */
    private fun changeClickItemIconAndText(position: Int, icon: ImageView, title: TextView) {
        icon.apply {
            if (mNavigationItems[position].imgResActive != 0) {
                setImageResource(mNavigationItems[position].imgResActive)
            } else {
                changeImageColorFilter(mItemInactiveColor, mItemActiveColor)
            }

            animate()
                .withLayer()
                .setDuration(ICON_SCALE_ANIMATION_DURATION)
                .scaleX(mIconActiveScale)
                .scaleY(mIconActiveScale)
                .start()
        }
        title.apply {
            changeTextColor(mItemInactiveColor, mItemActiveColor)
            if (mWithText) {
                changeTextSize(mTextInactiveSize, mTextActiveSize)
            } else {
                changeTextSize(0f, mTextActiveSize)
            }
        }
    }

    /**
     * change click item view padding
     */
    private fun changeClickItemViewPadding(view: View) {
        if (mWithText) {
            view.changeViewTopPadding(
                mViewInactivePaddingTop.toFloat(),
                mViewActivePaddingTop.toFloat()
            )
        } else {
            view.changeViewTopPadding(
                mViewInactivePaddingTopWithoutText.toFloat(),
                mViewActivePaddingTop.toFloat()
            )
        }
    }

    /**
     * change current item icon and text
     */
    private fun changeCurrentItemIconAndText(icon: ImageView, title: TextView) {
        icon.apply {
            if (mNavigationItems[mCurrentItem].imgResActive != 0) {
                setImageResource(mNavigationItems[mCurrentItem].imgRes)
            } else {
                changeImageColorFilter(mItemActiveColor, mItemInactiveColor)
            }
            animate()
                .withLayer()
                .setDuration(ICON_SCALE_ANIMATION_DURATION)
                .scaleX(mIconInactiveScale)
                .scaleY(mIconInactiveScale)
                .start()
        }
        title.apply {
            changeTextColor(mItemActiveColor, mItemInactiveColor)
            if (mWithText) {
                changeTextSize(mTextActiveSize, mTextInactiveSize)
            } else {
                changeTextSize(mTextActiveSize, 0f)
            }
        }
    }

    /**
     * change current item view padding
     */
    private fun changeCurrentItemViewPadding(view: View) {
        if (mWithText) {
            view.changeViewTopPadding(
                mViewActivePaddingTop.toFloat(),
                mViewInactivePaddingTop.toFloat()
            )
        } else {
            view.changeViewTopPadding(
                mViewActivePaddingTop.toFloat(),
                mViewInactivePaddingTopWithoutText.toFloat()
            )
        }
    }

    /**
     * change navigation background color
     */
    private fun changeNavigationBackgroundColor(itemIndex: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // start circular reveal
            val finalRadius = max(width, height)
            val (centerX, centerY) = if (isTablet) {
                (mNavigationItemViews[itemIndex].width / 2) to
                        (mNavigationItemViews[itemIndex].y.toInt() + mNavigationItemViews[itemIndex].height / 2)
            } else {
                (mNavigationItemViews[itemIndex].x.toInt() + mNavigationItemViews[itemIndex].width / 2) to
                        (mNavigationItemViews[itemIndex].height / 2)
            }
            mNavigationColoredBackgroundTempView.startCircularReveal(
                centerX, centerY, 0f, finalRadius.toFloat(),
                {
                    mNavigationColoredBackgroundTempView.setBackgroundColor(mNavigationItems[itemIndex].bgColor)
                },
                {
                    mNavigationContainer.setBackgroundColor(mNavigationItems[itemIndex].bgColor)
                }
            )
        } else {
            mNavigationContainer.changeViewBackgroundColor(
                mNavigationItems[mCurrentItem].bgColor,
                mNavigationItems[itemIndex].bgColor
            )
        }
    }

    /**
     * reset each parameter
     */
    fun defaultSetting() = apply {
        mNavigationHeight = resources.getDimensionPixelSize(R.dimen.en_height)
        mNavigationContainer = FrameLayout(context).apply {
            id = View.generateViewId()
        }
        mNavigationItemParent = LinearLayout(context)
        mNavigationColoredBackgroundTempView = View(context)

        mNavigationLineHeight = resources.getDimensionPixelSize(R.dimen.en_line_width)
        mNavigationLineColor = ContextCompat.getColor(context, R.color.en_lineColor)
        mNavigationLineView = View(context)

        mOnItemClickListener = null
        mNavigationItems = ArrayList<NavigationItem>()
        mNavigationItemViews = ArrayList<View>()

        mItemActiveColor = ContextCompat.getColor(
            context,
            R.color.en_itemActiveColorWithoutColoredBackground
        )
        mItemInactiveColor = ContextCompat.getColor(
            context,
            R.color.en_itemInactiveColorWithoutColoredBackground
        )
        mTextActiveSize = resources.getDimension(R.dimen.en_text_size_active)
        mTextInactiveSize = resources.getDimension(R.dimen.en_text_size_inactive)
        mTextFont = null

        mIconActiveScale = ICON_ACTIVE_SCALE
        mIconInactiveScale = ICON_INACTIVE_SCALE

        mViewActivePaddingTop = resources.getDimensionPixelSize(R.dimen.en_padding_top_active)
        mViewInactivePaddingTop = resources.getDimensionPixelSize(R.dimen.en_padding_top_inactive)
        mViewInactivePaddingTopWithoutText =
            resources.getDimensionPixelSize(R.dimen.en_padding_top_inactive_without_text)

        mShadowView = View(context)
        mShadowHeight = if (mColoredBackground) {
            resources.getDimensionPixelSize(R.dimen.en_shadow_height)
        } else {
            resources.getDimensionPixelSize(R.dimen.en_shadow_height_without_colored_background)
        }
        mShowShadow = true
        mWithText = true
        mColoredBackground = true
        isTablet = false
        mSmoothScroll = true

        mViewPager = null
        mViewPager2 = null
        mCurrentItem = 0
        mCenterView = null
    }

    /**
     * Creates a connection between this navigation view and a ViewPager
     *
     * @param viewPager      pager to connect to navigation view
     * @param colorResources color resources for every item in the ViewPager adapter
     * @param imageResources images resources for every item in the ViewPager adapter
     */
    fun setupWithViewPager(
        viewPager: ViewPager,
        titles: List<CharSequence?>,
        colorResources: IntArray,
        imageResources: IntArray,
    ) = apply {
        mViewPager = viewPager
        if (viewPager.adapter == null) {
            throw IllegalArgumentException("you should set a adapter for ViewPager first!")
        }
        if (viewPager.adapter!!.count != titles.size || viewPager.adapter!!.count != colorResources.size || viewPager.adapter!!.count != imageResources.size) {
            throw IllegalArgumentException("titles, colorResources and imageResources must be equal to the ViewPager items : " + viewPager.adapter!!.count)
        }
        viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                selectTab(position)
            }
        })
        for (i in 0 until viewPager.adapter!!.count) {
            addTab(NavigationItem(titles[i], colorResources[i], imageResources[i]))
        }
    }

    /**
     * Creates a connection between this navigation view and a ViewPager2
     *
     * @param viewPager2     pager to connect to navigation view
     * @param colorResources color resources for every item in the ViewPager2 adapter
     * @param imageResources images resources for every item in the ViewPager2 adapter
     */
    fun setupWithViewPager2(
        viewPager2: ViewPager2,
        titles: List<CharSequence?>,
        colorResources: IntArray,
        imageResources: IntArray,
    ) = apply {
        mViewPager2 = viewPager2
        if (viewPager2.adapter == null) {
            throw IllegalArgumentException("you should set a adapter for ViewPager2 first!")
        }
        if (viewPager2.adapter!!.itemCount != titles.size || viewPager2.adapter!!.itemCount != colorResources.size || viewPager2.adapter!!.itemCount != imageResources.size) {
            throw IllegalArgumentException("titles, colorResources and imageResources must be equal to the ViewPager2 items : " + viewPager2.adapter!!.itemCount)
        }
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                selectTab(position)
            }
        })
        for (i in 0 until viewPager2.adapter!!.itemCount) {
            addTab(NavigationItem(titles[i], colorResources[i], imageResources[i]))
        }
    }

    /**
     * Set items for [EasyNavigation]
     *
     * @param items items to set
     */
    fun setTabs(items: List<NavigationItem>) = apply {
        mNavigationItems.clear()
        mNavigationItems.addAll(items)
    }

    /**
     * Add item for [EasyNavigation]
     *
     * @param item item to add
     */
    fun addTab(vararg item: NavigationItem) = apply {
        mNavigationItems.addAll(listOf(*item))
    }

    /**
     * set navigation height
     */
    fun navigationHeight(@Px navigationHeight: Int) = apply {
        mNavigationHeight = navigationHeight
    }

    /**
     * Activate [EasyNavigation] tablet mode
     */
    fun activateTabletMode() = apply {
        isTablet = true
    }

    /**
     * Change text visibility
     *
     * @param withText disable or enable item text
     */
    fun withText(withText: Boolean) = apply {
        mWithText = withText
    }

    /**
     * Item Color
     *
     * @param itemActiveColor   active item color
     * @param itemInactiveColor inactive item color
     */
    fun itemColor(itemActiveColor: Int, itemInactiveColor: Int) = apply {
        mItemActiveColor = itemActiveColor
        mItemInactiveColor = itemInactiveColor
    }

    /**
     * With this [EasyNavigation] background will be white
     *
     * @param coloredBackground disable or enable background color
     */
    fun coloredBackground(coloredBackground: Boolean) = apply {
        mColoredBackground = coloredBackground
    }

    /**
     * Change tab programmatically
     *
     * @param position selected tab position
     */
    fun selectTab(position: Int) = apply {
        onNavigationItemClick(position)
    }

    /**
     * Disable show the shadow at the top of [EasyNavigation]
     */
    fun disableShowShadow() = apply {
        mShowShadow = false
    }

    /**
     * Disable slide animation when using ViewPager
     */
    fun disableSmoothScroll() = apply {
        mSmoothScroll = false
    }

    /**
     * Change icon size
     */
    fun iconScale(iconActiveScale: Float, iconInactiveScale: Float) = apply {
        mIconActiveScale = iconActiveScale
        mIconInactiveScale = iconInactiveScale
    }

    /**
     * Change text size
     *
     * @param textActiveSize   active pixel size
     * @param textInactiveSize inactive pixel size
     */
    fun textSize(textActiveSize: Float, textInactiveSize: Float) = apply {
        mTextActiveSize = textActiveSize
        mTextInactiveSize = textInactiveSize
    }

    /**
     * Setup interface for item onClick
     */
    fun onItemClickListener(onItemClickListener: ((Int) -> Unit)?) = apply {
        mOnItemClickListener = onItemClickListener
    }

    /**
     * set custom font for item texts
     *
     * @param font custom font
     */
    fun textFont(font: Typeface?) = apply {
        mTextFont = font
    }

    /**
     * set custom center view
     */
    fun setCenterView(centerView: View) = apply {
        mCenterView = centerView
    }

    /**
     * Setup interface for center view onClick
     */
    fun onCenterViewClickListener(onCenterViewClick: () -> Unit) = apply {
        mOnCenterViewClickListener = onCenterViewClick
    }

    /**
     * @return custom center view
     */
    fun getCenterView() = mCenterView

    /**
     * Returns the item that is currently selected
     *
     * @return currently selected item
     */
    fun getCurrentItem() = mCurrentItem

    /**
     * get item text size on active status
     *
     * @return font size
     */
    fun getTextActiveSize() = mTextActiveSize

    /**
     * get item text size on inactive status
     *
     * @return font size
     */
    fun getTextInactiveSize() = mTextInactiveSize

    fun getItem(position: Int) = mNavigationItems[position]
}
