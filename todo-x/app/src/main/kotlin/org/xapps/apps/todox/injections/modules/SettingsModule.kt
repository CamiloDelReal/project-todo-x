package org.xapps.apps.todox.injections.modules

import android.content.Context
import android.content.SharedPreferences
import org.xapps.apps.todox.services.settings.SettingsService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class SettingsModule {

    companion object {
        const val PREFERENCE_FILENAME = "application_preferences.xml"
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFERENCE_FILENAME, Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideApplicationSettings(sharedPreferences: SharedPreferences): SettingsService =
        SettingsService(sharedPreferences)

}