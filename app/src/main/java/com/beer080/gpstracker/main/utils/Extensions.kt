package com.beer080.gpstracker.main.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.beer080.gpstracker.R

fun Fragment.openFragment(frag: Fragment){
    activity?.supportFragmentManager
        ?.beginTransaction()
        ?.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right)
        ?.replace(R.id.fragment,frag)
        ?.commit()
}

fun AppCompatActivity.openFragment(frag: Fragment){
    supportFragmentManager
        .beginTransaction()
        .setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right)
        .replace(R.id.fragment,frag)
        .commit()
}