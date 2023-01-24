package com.beer080.gpstracker.main

import androidx.lifecycle.*
import com.beer080.gpstracker.main.data.LocationModel
import com.beer080.gpstracker.main.data.MainDataBase
import com.beer080.gpstracker.main.data.TrackItem
import kotlinx.coroutines.launch

class HomeViewModel(dataBase: MainDataBase): ViewModel() {
    val dao = dataBase.getDao()
    val locationUpdates = MutableLiveData<LocationModel>()
    val timeData = MutableLiveData<String>()
    val tracksList = dao.getAllTracks().asLiveData()

    fun addTracks(trackItem: TrackItem) = viewModelScope.launch {
        dao.addTrack(trackItem)
    }

    class ViewModelFactory(private val dataBase: MainDataBase) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(HomeViewModel::class.java)){
            return HomeViewModel(dataBase) as T

        }
            throw IllegalArgumentException("Unknow ViewModel class")
        }
    }
}