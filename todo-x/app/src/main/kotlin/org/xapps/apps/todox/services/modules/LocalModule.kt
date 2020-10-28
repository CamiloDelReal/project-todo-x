package org.xapps.apps.todox.services.modules

import android.content.Context
import androidx.room.Room
import org.xapps.apps.todox.services.local.LocalDatabaseService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
class LocalModule {

    companion object {
        private const val DB_FILENAME = "application_database.db"
    }

    @Singleton
    @Provides
    fun provideLocalDatabaseService(context: Context): LocalDatabaseService =
        Room.databaseBuilder(context, LocalDatabaseService::class.java, DB_FILENAME).build()

}