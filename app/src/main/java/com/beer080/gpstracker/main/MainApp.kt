package com.beer080.gpstracker.main

import android.app.Application
import com.beer080.gpstracker.main.data.MainDataBase

class MainApp: Application() {
val database by lazy{ MainDataBase.getDatabase(this) }

}