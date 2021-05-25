package org.xapps.apps.todox.core.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "categories")
data class Category (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0,

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "color")
    var color: String = ""
) {

    @ColumnInfo(name = "tasks_count")
    var tasksCount: Int = 0

    @ColumnInfo(name = "pending_tasks_count")
    var pendingTasksCount: Int = 0

    @ColumnInfo(name = "today_tasks_count")
    var todayTasksCount: Int = 0

}