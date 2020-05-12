package org.xapps.apps.todox.services.local

import androidx.room.Database
import androidx.room.RoomDatabase
import org.xapps.apps.todox.services.models.Todo


@Database(
    entities = [
        Todo::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LocalStorageService : RoomDatabase()