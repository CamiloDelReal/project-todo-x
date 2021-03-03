package org.xapps.apps.todox.core.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "items")
data class Item (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "description")
    var description: String,

    @ColumnInfo(name = "done")
    var done: Boolean = false,

    @ColumnInfo(name = "task_id")
    var taskId: Long = 0
)