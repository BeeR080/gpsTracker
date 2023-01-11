package com.beer080.gpstracker.main.view
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.beer080.gpstracker.R
import com.beer080.gpstracker.databinding.ActivityMainBinding
import com.beer080.gpstracker.main.utils.openFragment
import com.beer080.gpstracker.main.view.fragments.HomeFragment
import com.beer080.gpstracker.main.view.fragments.SettingsFragmnet
import com.beer080.gpstracker.main.view.fragments.TracksFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomNavItemClickListener()
        openFragment(HomeFragment.newInstance())
    }
    private fun bottomNavItemClickListener(){
        binding.btNav.apply {
            this.selectedItemId = R.id.btm_home
            this.setOnItemSelectedListener {
                when(it.itemId){
                    R.id.btm_tracks -> openFragment(TracksFragment.newInstance())
                    R.id.btm_home -> openFragment(HomeFragment.newInstance())
                    R.id.btm_settings -> openFragment(SettingsFragmnet())
                }
                true
            }

        }

    }


}