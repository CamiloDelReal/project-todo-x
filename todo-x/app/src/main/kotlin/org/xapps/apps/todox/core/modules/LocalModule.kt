package org.xapps.apps.todox.core.modules

import android.content.Context
import androidx.room.Room
import org.xapps.apps.todox.core.local.LocalDatabaseService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.xapps.apps.todox.core.local.CategoryDao
import org.xapps.apps.todox.core.local.ItemDao
import org.xapps.apps.todox.core.local.TaskDao
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class LocalModule {

    companion object {
        private const val DB_FILENAME = "application_database.db"
    }

    @Singleton
    @Provides
    fun provideLocalDatabaseService(@ApplicationContext context: Context): LocalDatabaseService =
        Room.databaseBuilder(context, LocalDatabaseService::class.java, DB_FILENAME).build()

    @Singleton
    @Provides
    fun provideCategoryDao(localDatabaseService: LocalDatabaseService): CategoryDao =
        localDatabaseService.categoryDao()

    @Singleton
    @Provides
    fun provideTaskDao(localDatabaseService: LocalDatabaseService): TaskDao =
        localDatabaseService.taskDao()

    @Singleton
    @Provides
    fun provideItemDao(localDatabaseService: LocalDatabaseService): ItemDao =
        localDatabaseService.itemDao()

}