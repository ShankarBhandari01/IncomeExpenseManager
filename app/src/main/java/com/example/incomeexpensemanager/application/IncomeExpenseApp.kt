package com.example.incomeexpensemanager.application

import android.app.Application
import cat.ereza.customactivityoncrash.activity.DefaultErrorActivity
import cat.ereza.customactivityoncrash.config.CaocConfig
import com.example.incomeexpensemanager.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber


@HiltAndroidApp
class IncomeExpenseApp : Application() {


    override fun onCreate() {
        super.onCreate()
        initTimber()
        manageCrashingActivity()

    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

    private fun manageCrashingActivity() {
        CaocConfig.Builder.create()
            .logErrorOnRestart(false) //default: true
            .trackActivities(true) //default: false
            .minTimeBetweenCrashesMs(2000) //default: 3000
            .errorActivity(DefaultErrorActivity::class.java)
            .apply()
    }

}