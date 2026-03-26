package com.duhan.videototext.Data.LocalDataSource

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SummarySettingsManager @Inject constructor(@ApplicationContext context: Context) {

    private val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    companion object {
        const val KEY_SUMMARY_RATIO = "summary_ratio"
        const val DEFAULT_SUMMARY_RATIO = 20
    }

    fun saveSummaryRatio(ratio: Int){
        prefs.edit().putInt(KEY_SUMMARY_RATIO, ratio).apply()
    }

    fun getSummaryRatio(): Int {
        return prefs.getInt(KEY_SUMMARY_RATIO, DEFAULT_SUMMARY_RATIO)
    }
}