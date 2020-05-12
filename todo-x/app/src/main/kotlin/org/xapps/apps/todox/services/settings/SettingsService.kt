package org.xapps.apps.todox.services.settings

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject


class SettingsService @Inject constructor(private val sharedPreferences: SharedPreferences) {

    companion object {
        private const val ATTR_IS_FIRST_TIME = "attrIsFirstTime"
        private const val ATTR_ON_BOARDING_FINISHED = "attrOnBoardingFinished"
    }

    fun isFirstTime(): Boolean =
        sharedPreferences.getBoolean(ATTR_IS_FIRST_TIME, true)

    fun setIsFirstTime(isFirstTime: Boolean) =
        sharedPreferences.edit { putBoolean(ATTR_IS_FIRST_TIME, isFirstTime) }

    fun isOnBoardingFinished(): Boolean =
        sharedPreferences.getBoolean(ATTR_ON_BOARDING_FINISHED, false)

    fun setOnBoardingFinished(onBoardingFinished: Boolean) =
        sharedPreferences.edit { putBoolean(ATTR_ON_BOARDING_FINISHED, onBoardingFinished) }

}