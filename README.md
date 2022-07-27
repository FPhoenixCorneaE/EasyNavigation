# EasyNavigation

Android 底部导航栏，几行代码实现 Tab 导航（支持自定义 CenterView、支持 ViewPager2）
------------------------------------------------------------------------

[![](https://jitpack.io/v/FPhoenixCorneaE/EasyNavigation.svg)](https://jitpack.io/#FPhoenixCorneaE/EasyNavigation)

<div align="center">
    <img src="https://github.com/FPhoenixCorneaE/EasyNavigation/blob/master/images/pic_preview.png" width="360" align="top"/>
</div>

How to include it in your project:
--------------
**Step 1.** Add the JitPack repository to your build file

```groovy
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

**Step 2.** Add the dependency

```groovy
dependencies {
    implementation 'com.github.FPhoenixCorneaE.EasyNavigation:easyNavigation:$latest'
}
```

### How to use：

1. #### Xml或者代码声明`EasyNavigation`：

   ```xml
       <com.fphoenixcorneae.navigation.EasyNavigation
            android:id="@+id/bnvBottomBar1"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:layout_constraintBottom_toBottomOf="parent" />
   ```

   ```kotlin
   EasyNavigation(context)
   ```

2. #### 设置属性：

    - [ ] ##### 第一种方式：Xml中设置attr属性

   ```xml
       <declare-styleable name="EasyNavigation">
           <!-- If text will be enabled for the tabs, defaulted to true -->
           <attr name="en_with_text" format="boolean" />
           <!-- If there will be a en_shadow on the view, default is true -->
           <attr name="en_shadow" format="boolean" />
           <!-- If the view should be set up for a tablet, default is false -->
           <attr name="en_tablet" format="boolean" />
           <!-- Disable or enable the background color, default is true -->
           <attr name="en_colored_background" format="boolean" />
           <!-- If a supplied ViewPager will have a slide animation, default is true -->
           <attr name="en_viewpager_slide" format="boolean" />
           <!-- Text size for the active tab, default size is 14sp -->
           <attr name="en_active_text_size" format="dimension" />
           <!-- Text size for an inactive tab, default size is 12sp -->
           <attr name="en_inactive_text_size" format="dimension" />
           <!-- The color of an activated tab. -->
           <attr name="en_item_active_color" format="color" />
           <!-- The color of an inactivated tab. -->
           <attr name="en_item_inactive_color" format="color" />
       </declare-styleable>
   ```

    - [ ] ##### 第二种方式：代码中调用方法设置属性

   ```kotlin
        enBottomBar
   			// Activate tablet mode
   			.activateTabletMode()
   			// Change item text visibility
   			.withText(false) 
   			// Set background white color or colored color
            .coloredBackground(true)
   			// Change icon size
            .iconScale(1.3f, 0.5f)
   			// Change text size
            .textSize(20f,18f)
   			// Set item color
            .itemColor(
                ContextCompat.getColor(this, R.color.colorAccent),
                ContextCompat.getColor(this, R.color.colorPrimary)
            )
   			// Set custom font for item texts
            .textFont(Typeface.defaultFromStyle(Typeface.ITALIC))
   			// Set items
            .setTabs(
                   listOf(
                       NavigationItem(
                           "首页",
                           Color.parseColor("#25ff0000"),
                           R.drawable.ic_nav_home
                       ),
                       NavigationItem(
                           "发现",
                           Color.parseColor("#2500ff00"),
                           R.drawable.ic_nav_discover
                       ),
                       NavigationItem(
                           "热门",
                           Color.parseColor("#250000ff"),
                           R.drawable.ic_nav_hot
                       ),
                       NavigationItem(
                           "我的",
                           Color.parseColor("#25f0f000"),
                           R.drawable.ic_nav_mine
                       )
                   )
               )
   			// Set navigation height
            .navigationHeight(NavigationUtil.dp2px(60f).toInt())
   			// Setup interface for item onClick
            .onItemClickListener { }
   			// Creates a connection between this navigation view and a ViewPager
   			.setupWithViewPager(viewPager, titles, colorResources, imageResources)
   			// Creates a connection between this navigation view and a ViewPager2
   			.setupWithViewPager2(viewPager2, titles, colorResources, imageResources)
            // Disable slide animation when using ViewPager or ViewPager2
            .disableSmoothScroll()
            // Disable show the shadow at the top of navigation
            .disableShowShadow()
            // Set custom center view
            .setCenterView(ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(200, 200)
                setPadding(20)
                translationY = -80f
                setImageResource(R.mipmap.ic_add)
                setOvalBg(Color.WHITE)
            })
            // Setup interface for center view onClick
            .onCenterViewClickListener {
                Log.d("onCenterViewClick", "click the center view!")
                Toast.makeText(this, "click the center view!", Toast.LENGTH_SHORT).show()
            }
   ```
   
   
