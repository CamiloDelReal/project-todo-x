package org.xapps.apps.todox.core.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime
import java.util.*


@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0,

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "date")
    var date: LocalDate? = null,

    @ColumnInfo(name = "important")
    var important: Boolean = false,

    @ColumnInfo(name = "start_time")
    var startTime: LocalTime? = null,

    @ColumnInfo(name = "end_time")
    var endTime: LocalTime? = null,

    @ColumnInfo(name = "done")
    var done: Boolean = false,

    @ColumnInfo(name = "category_id")
    var categoryId: Long = 0
)