package com.beer080.gpstracker.main.utils

import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
    if(supportFragmentManager.fragments.isNotEmpty()){
        if(supportFragmentManager.fragments[0].javaClass ==frag.javaClass)
           return
    }
    supportFragmentManager
        .beginTransaction()
        .setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right)
        .replace(R.id.fragment,frag)
        .commit()
}
fun Fragment.chekPermisson(p:String): Boolean{
    return when(PackageManager.PERMISSION_GRANTED){
        ContextCompat.checkSelfPermission(activity as AppCompatActivity, p) -> true
        else -> false
    }

}