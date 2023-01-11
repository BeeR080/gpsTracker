package com.beer080.gpstracker.main.view.fragments

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.PreferenceFragmentCompat
import com.beer080.gpstracker.R
const val UPDATE_TIME_KEY ="update_time_key"
const val UPDATE_COLOR_KEY ="update_color_key"
class SettingsFragmnet:PreferenceFragmentCompat() {

    private lateinit var timePref:Preference
    private lateinit var colorPref:Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.mainpref,rootKey)
        init()
    }

    private fun init(){
        timePref = findPreference("update_time_key")!!
        colorPref = findPreference("update_color_key")!!
        val listener = onChangeListener()
        timePref.onPreferenceChangeListener = listener
        colorPref.onPreferenceChangeListener = listener

        initPref()
    }
private fun onChangeListener(): OnPreferenceChangeListener{
    return  Preference.OnPreferenceChangeListener{
        pref,value ->
        when(pref.key){
        UPDATE_TIME_KEY -> onTimeChange(value.toString())
        UPDATE_COLOR_KEY -> pref.icon?.setTint(Color.parseColor(value.toString()))
        }


        true
    }


}


    private fun onTimeChange(value: String){
        val nameArray = resources.getStringArray(R.array.loc_time_update_name)
        val valueArray = resources.getStringArray(R.array.loc_time_update_value)
        val title =timePref.title.toString().substringBefore(":")
        timePref.title = "$title: ${nameArray[valueArray.indexOf(value)]}"
    }
    private fun initPref(){
        val timepref = timePref.preferenceManager.sharedPreferences
        val colorpref = colorPref.preferenceManager.sharedPreferences
        val nameArray = resources.getStringArray(R.array.loc_time_update_name)
        val valueArray = resources.getStringArray(R.array.loc_time_update_value)
        val title = timePref.title
        timePref.title = "$title: ${nameArray[valueArray
            .indexOf(
                timepref?.getString("update_time_key","3000"))]}"
        val trackColor = colorpref?.getString("update_color_key","#2CE519")
        colorPref.icon?.setTint(Color.parseColor(trackColor))
    }
}