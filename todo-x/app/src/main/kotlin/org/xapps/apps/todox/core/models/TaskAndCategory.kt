package org.xapps.apps.todox.core.models

import androidx.room.Embedded
import androidx.room.Relation


data class TaskAndCategory (
    @Embedded
    val task: Task,

    @Relation(parentColumn = "category_id", entityColumn = "id")
    val category: Category
)