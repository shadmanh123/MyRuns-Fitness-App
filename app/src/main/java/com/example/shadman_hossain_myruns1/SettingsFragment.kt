package com.example.shadman_hossain_myruns1

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment: PreferenceFragmentCompat() {
    private lateinit var intentToProfile: Intent
    private lateinit var intentToBrowser: Intent
    private lateinit var classUrl:String

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        setupProfilePreference()
        setupWebpagePreference()
        setupDistanceUnitPreference()
    }

    private fun setupDistanceUnitPreference() {
        var distanceUnitPreference = findPreference<ListPreference>("unit_preference_listview")
        distanceUnitPreference?.setOnPreferenceChangeListener { _, newValue ->
            var unitPreference = newValue.toString()
            val unitType: SharedPreferences =
                requireContext().getSharedPreferences("Unit", AppCompatActivity.MODE_PRIVATE)
            unitType.edit().apply {
                putString("unitType", unitPreference)
                apply()
            }
            true
        }
    }

    private fun setupWebpagePreference() {
        var webpage = findPreference<Preference>("webpage")
        webpage?.setOnPreferenceClickListener {
            classUrl = "https://www.sfu.ca/computing.html"
            intentToBrowser = Intent(Intent.ACTION_VIEW, Uri.parse(classUrl))
            startActivity(intentToBrowser)
            true
        }
    }

    private fun setupProfilePreference() {
        var profile = findPreference<Preference>("profile")
        profile?.setOnPreferenceClickListener {
            intentToProfile = Intent(requireContext(), Profile::class.java)
            startActivity(intentToProfile)
            true
        }
    }
}