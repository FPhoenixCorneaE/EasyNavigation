package com.fphoenixcorneae.navigation.demo

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fphoenixcorneae.navigation.NavigationItem
import com.fphoenixcorneae.navigation.demo.databinding.ActivityMainBinding
import com.fphoenixcorneae.navigation.setOvalBg

class MainActivity : AppCompatActivity() {

    private var mViewBinding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mViewBinding!!.root)

        val fargments = arrayListOf(
            SampleFragment.newInstance("HomeFragment", ""),
            SampleFragment.newInstance("DiscoverFragment", ""),
            SampleFragment.newInstance("HotFragment", ""),
            SampleFragment.newInstance("MineFragment", "")
        )
        val fargments2 = arrayListOf(
            SampleFragment.newInstance("HomeFragment", ""),
            SampleFragment.newInstance("DiscoverFragment", ""),
            SampleFragment.newInstance("HotFragment", ""),
            SampleFragment.newInstance("MineFragment", "")
        )

        val titles = arrayListOf(
            "首页",
            "发现",
            "热门",
            "我的"
        )

        mViewBinding!!.vpPager.apply {
            adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
                override fun getCount(): Int {
                    return fargments.size
                }

                override fun getItem(position: Int): Fragment {
                    return fargments[position]
                }

                override fun getPageTitle(position: Int): CharSequence? {
                    return titles[position]
                }
            }
        }
        mViewBinding!!.vpPager2.apply {
            adapter = object : FragmentStateAdapter(this@MainActivity) {
                override fun getItemCount(): Int {
                    return fargments2.size
                }

                override fun createFragment(position: Int): Fragment {
                    return fargments2[position]
                }
            }
        }

        mViewBinding!!.bnvBottomBar1
            .activateTabletMode()
            .withText(false)
            .setupWithViewPager(
                mViewBinding!!.vpPager,
                titles,
                arrayOf(
                    Color.parseColor("#25ff0000"),
                    Color.parseColor("#2500ff00"),
                    Color.parseColor("#250000ff"),
                    Color.parseColor("#25f0f000")
                ).toIntArray(),
                arrayOf(
                    R.drawable.ic_nav_home,
                    R.drawable.ic_nav_discover,
                    R.drawable.ic_nav_hot,
                    R.drawable.ic_nav_mine
                ).toIntArray()
            )
            .disableSmoothScroll()

        mViewBinding!!.bnvBottomBar2
            .coloredBackground(false)
            .setCenterView(ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(120, 120)
                setPadding(20)
                translationY = -40f
                setImageResource(R.mipmap.ic_add)
                setOvalBg(Color.WHITE)
            })
            .onCenterViewClickListener {
                Toast.makeText(this, "click the center view!", Toast.LENGTH_SHORT).show()
            }
            .setupWithViewPager2(
                mViewBinding!!.vpPager2,
                titles,
                arrayOf(
                    Color.parseColor("#25ff0000"),
                    Color.parseColor("#2500ff00"),
                    Color.parseColor("#250000ff"),
                    Color.parseColor("#25f0f000")
                ).toIntArray(),
                arrayOf(
                    R.drawable.ic_nav_home,
                    R.drawable.ic_nav_discover,
                    R.drawable.ic_nav_hot,
                    R.drawable.ic_nav_mine
                ).toIntArray()
            )

        mViewBinding!!.bnvBottomBar3
            .withText(false)
            .coloredBackground(true)
            .textSize(
                20f,
                18f
            )
            .itemColor(
                ContextCompat.getColor(this, R.color.colorAccent),
                ContextCompat.getColor(this, R.color.colorPrimary)
            )
            .textFont(Typeface.defaultFromStyle(Typeface.ITALIC))
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
            .onItemClickListener {

            }
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewBinding = null
    }
}
