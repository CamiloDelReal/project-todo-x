package org.xapps.apps.todox.injections.modules

import android.content.Context
import androidx.room.Room
import org.xapps.apps.todox.services.local.LocalStorageService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class LocalStorageServiceModule {

    companion object {
        private const val DB_FILENAME = "application_database.db"
    }

    @Singleton
    @Provides
    fun provideApplicationDatabase(context: Context): LocalStorageService =
        Room.databaseBuilder(context, LocalStorageService::class.java, DB_FILENAME).build()

}