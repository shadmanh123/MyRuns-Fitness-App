package com.example.shadman_hossain_myruns1

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

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