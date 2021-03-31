package org.xapps.apps.todox.core.models

import androidx.room.Embedded
import androidx.room.Relation


data class TaskWithItems (
    @Embedded
    val task: Task,

    @Relation(parentColumn = "id", entityColumn = "task_id")
    val items: List<Item>
) {

    fun itemsDescriptions(): String =
        items.joinToString(separator = ", ", transform = { it -> it.description})

}