package com.salaryisland.app

import android.app.Application
import com.salaryisland.app.data.SettingsDataStore

class SalaryIslandApp : Application() {
    lateinit var settingsDataStore: SettingsDataStore
        private set

    override fun onCreate() {
        super.onCreate()
        settingsDataStore = SettingsDataStore(this)
    }
}
