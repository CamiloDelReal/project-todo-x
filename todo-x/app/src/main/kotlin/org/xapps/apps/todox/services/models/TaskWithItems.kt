package org.xapps.apps.todox.services.models

import androidx.room.Embedded
import androidx.room.Relation


data class TaskWithItems (
    @Embedded
    val task: Task,

    @Relation(parentColumn = "id", entityColumn = "task_id")
    val items: List<Item>
)