package org.xapps.apps.todox.services.settings

import android.content.Context
import androidx.core.content.edit
import javax.inject.Inject


class SettingsService @Inject constructor(
    private val context: Context,
    private val fileName: String
) {

    companion object {
        private const val ATTR_IS_FIRST_TIME = "attrIsFirstTime"
        private const val ATTR_ON_BOARDING_FINISHED = "attrOnBoardingFinished"
        private const val ATTR_DARK_MODE_ON = "attrDarkModeOn"
    }

    private val sharedPreferences =
        context.getSharedPreferences(fileName, Context.MODE_PRIVATE)

    fun isFirstTime(): Boolean =
        sharedPreferences.getBoolean(ATTR_IS_FIRST_TIME, true)

    fun setIsFirstTime(isFirstTime: Boolean) =
        sharedPreferences.edit { putBoolean(ATTR_IS_FIRST_TIME, isFirstTime) }

    fun isOnBoardingFinished(): Boolean =
        sharedPreferences.getBoolean(ATTR_ON_BOARDING_FINISHED, false)

    fun setOnBoardingFinished(onBoardingFinished: Boolean) =
        sharedPreferences.edit { putBoolean(ATTR_ON_BOARDING_FINISHED, onBoardingFinished) }

    fun isDarkModeOn(): Boolean =
        sharedPreferences.getBoolean(ATTR_DARK_MODE_ON, false)

    fun setIsDarkModeOn(darkModeOn: Boolean) =
        sharedPreferences.edit { putBoolean(ATTR_DARK_MODE_ON, darkModeOn) }
}