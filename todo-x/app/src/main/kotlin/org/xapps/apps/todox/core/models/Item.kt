package org.xapps.apps.todox.core.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "items")
data class Item (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0,

    @ColumnInfo(name = "description")
    var description: String = "",

    @ColumnInfo(name = "done")
    var done: Boolean = false,

    @ColumnInfo(name = "task_id")
    var taskId: Long = 0
) {

    override fun equals(other: Any?): Boolean {
        return if(other is Item) {
            if(id == other.id && description == other.description && done == other.done && taskId == other.taskId) {
                (this === other)
            } else {
                false
            }
        } else {
            super.equals(other)
        }
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}