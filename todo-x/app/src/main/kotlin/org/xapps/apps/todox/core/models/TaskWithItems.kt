package org.xapps.apps.todox.core.models

import androidx.room.Embedded
import androidx.room.Relation
import java.util.*


data class TaskWithItems (
    @Embedded
    var task: Task,

    @Relation(parentColumn = "id", entityColumn = "task_id")
    var items: List<Item>
) {

    fun itemsDescriptions(): String =
        items.joinToString(separator = ", ", transform = { it -> it.description})

}