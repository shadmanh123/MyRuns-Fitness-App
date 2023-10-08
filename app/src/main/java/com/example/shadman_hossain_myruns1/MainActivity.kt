package com.example.shadman_hossain_myruns1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy

class MainActivity : AppCompatActivity() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var startFragement:Fragment
    private lateinit var historyFragment: Fragment
    private lateinit var settingsFragment: Fragment
    private lateinit var fragments:ArrayList<Fragment>
    private lateinit var fragmentAdapter: FragmentAdapter
    private lateinit var tabLayoutMediator: TabLayoutMediator
    private val fragmentTitles = arrayOf("Start", "History", "Settings")
    private lateinit var tabConfigurationStrategy:TabConfigurationStrategy
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        viewPager2 = findViewById(R.id.viewPager2Container)
        tabLayout = findViewById(R.id.tabLayout)
        startFragement = StartFragment()
        historyFragment = HistoryFragment()
        settingsFragment = SettingsFragment()
        fragments = ArrayList()
        fragments.add(startFragement)
        fragments.add(historyFragment)
        fragments.add(settingsFragment)
        fragmentAdapter = FragmentAdapter(this,fragments)
        viewPager2.adapter = fragmentAdapter
        tabConfigurationStrategy = TabConfigurationStrategy{tab, position -> tab.text = fragmentTitles[position]  }
        tabLayoutMediator = TabLayoutMediator(tabLayout,viewPager2,tabConfigurationStrategy)
        tabLayoutMediator.attach()

    }

}