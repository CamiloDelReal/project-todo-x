package org.xapps.apps.todox.services.local

import androidx.room.Database
import androidx.room.RoomDatabase
import org.xapps.apps.todox.services.models.Category
import org.xapps.apps.todox.services.models.Task


@Database(
    entities = [
        Category::class,
        Task::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LocalDatabaseService : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao

    abstract fun taskDao(): TaskDao

}