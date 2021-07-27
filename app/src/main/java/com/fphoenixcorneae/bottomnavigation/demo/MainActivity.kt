package com.fphoenixcorneae.bottomnavigation.demo

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import com.fphoenixcorneae.bottomnavigation.BottomNavigationItem
import com.fphoenixcorneae.bottomnavigation.demo.databinding.ActivityMainBinding

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

        mViewBinding!!.bnvBottomBar1.activateTabletMode()
            .setUpWithViewPager(
                mViewBinding!!.vpPager,
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

        mViewBinding!!.bnvBottomBar2.isColoredBackground(false)
            .setUpWithViewPager(
                mViewBinding!!.vpPager,
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

        mViewBinding!!.bnvBottomBar3.isWithText(false)
            .isColoredBackground(true)
            .setTextSize(
                20f,
                18f
            )
            .setItemColor(
                ContextCompat.getColor(this, R.color.colorAccent),
                ContextCompat.getColor(this, R.color.colorPrimary)
            )
            .setFont(Typeface.defaultFromStyle(Typeface.ITALIC))
            .setTabs(
                listOf(
                    BottomNavigationItem(
                        "首页",
                        Color.parseColor("#25ff0000"),
                        R.drawable.ic_nav_home
                    ),
                    BottomNavigationItem(
                        "发现",
                        Color.parseColor("#2500ff00"),
                        R.drawable.ic_nav_discover
                    ),
                    BottomNavigationItem(
                        "热门",
                        Color.parseColor("#250000ff"),
                        R.drawable.ic_nav_hot
                    ),
                    BottomNavigationItem(
                        "我的",
                        Color.parseColor("#25f0f000"),
                        R.drawable.ic_nav_mine
                    )
                )
            )
            .setOnBottomNavigationItemClickListener {

            }
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewBinding = null
    }
}
