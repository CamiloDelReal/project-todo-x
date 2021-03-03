package org.xapps.apps.todox.core.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.xapps.apps.todox.core.local.converters.LocalDateConverter
import org.xapps.apps.todox.core.local.converters.LocalTimeConverter
import org.xapps.apps.todox.core.models.Category
import org.xapps.apps.todox.core.models.Item
import org.xapps.apps.todox.core.models.Task


@Database(
    entities = [
        Category::class,
        Task::class,
        Item::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(*[
    LocalTimeConverter::class,
    LocalDateConverter::class
])
abstract class LocalDatabaseService : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao

    abstract fun taskDao(): TaskDao

    abstract fun itemDao(): ItemDao

}