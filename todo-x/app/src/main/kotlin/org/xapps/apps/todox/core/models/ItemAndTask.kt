package org.xapps.apps.todox.core.models

import androidx.room.Embedded
import androidx.room.Relation


data class ItemAndTask (
    @Embedded
    val item: Item,

    @Relation(parentColumn = "task_id", entityColumn = "id")
    val task: Task
)