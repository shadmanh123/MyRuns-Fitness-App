package com.example.shadman_hossain_myruns1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment: PreferenceFragmentCompat() {
    private lateinit var intentToProfile: Intent
    private lateinit var intentToBrowser: Intent
    private lateinit var classUrl:String

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        var profile = findPreference<Preference>("profile")
        profile?.setOnPreferenceClickListener {
            intentToProfile = Intent(requireContext(), Profile::class.java)
            startActivity(intentToProfile)
            true
        }
        var webpage = findPreference<Preference>("webpage")
        webpage?.setOnPreferenceClickListener {
            classUrl = "https://www.sfu.ca/computing.html"
            intentToBrowser = Intent(Intent.ACTION_VIEW, Uri.parse(classUrl))
            startActivity(intentToBrowser)
            true
        }
        var distanceUnitPreference = findPreference<Preference>("unit_preference_entries")
        distanceUnitPreference?.setOnPreferenceChangeListener { _, newValue ->
            var unitPreference = newValue.toString()
            val adapter = activity as ActivityEntriesAdapter
//            adapter.setUnitPreference(unitPreference)
//            if(unitPreference == "kilometers"){
//                adapter.getUnitPreference(1)
//            }
//            else{
//                adapter.getUnitPreference(2)
//            }
            true
        }
    }
}