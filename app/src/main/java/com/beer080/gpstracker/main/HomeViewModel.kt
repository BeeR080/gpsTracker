package com.beer080.gpstracker.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beer080.gpstracker.main.data.LocationModel

class HomeViewModel: ViewModel() {
    val locationUpdates = MutableLiveData<LocationModel>()
    val timeData = MutableLiveData<String>()
}