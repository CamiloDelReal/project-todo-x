package org.xapps.apps.todox.core.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject


class SettingsRepository @Inject constructor(
    private val context: Context,
    private val dispatcher: CoroutineDispatcher,
    preferenceName: String
) {

    companion object {
        private val ATTR_IS_FIRST_TIME = booleanPreferencesKey("attrIsFirstTime")
        private val ATTR_ON_BOARDING_FINISHED = booleanPreferencesKey("attrOnBoardingFinished")
        private val ATTR_DARK_MODE_ON = booleanPreferencesKey("attrDarkModeOn")
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = preferenceName)

    suspend fun isFirstTimeValue(): Boolean =
        context.dataStore.data.flowOn(dispatcher).first()[ATTR_IS_FIRST_TIME] ?: true

    suspend fun setIsFirstTime(isFirstTime: Boolean) =
        context.dataStore.edit { preferences ->
            preferences[ATTR_IS_FIRST_TIME] = isFirstTime
        }

    suspend fun isOnBoardingFinishedValue(): Boolean =
        context.dataStore.data.flowOn(dispatcher).first()[ATTR_ON_BOARDING_FINISHED] ?: false

    suspend fun setOnBoardingFinished(onBoardingFinished: Boolean) =
        context.dataStore.edit { preferences ->
            preferences[ATTR_ON_BOARDING_FINISHED] = onBoardingFinished
        }

    suspend fun isDarkModeOnValue(): Boolean =
        context.dataStore.data.flowOn(dispatcher).first()[ATTR_DARK_MODE_ON] ?: false

    fun isDarkModeOn(): Flow<Boolean> =
        context.dataStore.data
            .catch { ex ->
                Timber.e(ex)
                emit(emptyPreferences())
            }
            .map { preferences ->
                val isActive = preferences[ATTR_DARK_MODE_ON] ?: false
                isActive
            }
            .flowOn(dispatcher)

    suspend fun setIsDarkModeOn(darkModeOn: Boolean) =
        context.dataStore.edit { preferences ->
            preferences[ATTR_DARK_MODE_ON] = darkModeOn
        }
}