package com.example.betpro

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val mainFragment = MainFragment()
    private val historyFragment = HistoryFragment()
    private val analyticsFragment = AnalyticsFragment()
    private val bookmakersFragment = BookmakersFragment()
    private val settingsFragment = SettingsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, mainFragment)
            }
        }

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_main -> {
                    supportFragmentManager.commit {
                        replace(R.id.fragment_container, mainFragment)
                    }
                    true
                }
                R.id.navigation_history -> {
                    supportFragmentManager.commit {
                        replace(R.id.fragment_container, historyFragment)
                    }
                    true
                }
                R.id.navigation_stats -> {
                    supportFragmentManager.commit {
                        replace(R.id.fragment_container, analyticsFragment)
                    }
                    true
                }
                R.id.navigation_bk -> {
                    supportFragmentManager.commit {
                        replace(R.id.fragment_container, bookmakersFragment)
                    }
                    true
                }
                R.id.navigation_settings -> {
                    supportFragmentManager.commit {
                        replace(R.id.fragment_container, settingsFragment)
                    }
                    true
                }
                else -> false
            }
        }
    }
}