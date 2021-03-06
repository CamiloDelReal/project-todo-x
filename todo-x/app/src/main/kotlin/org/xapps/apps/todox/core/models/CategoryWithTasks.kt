package org.xapps.apps.todox.core.models

import androidx.room.Embedded
import androidx.room.Relation


data class CategoryWithTasks (
    @Embedded
    val category: Category,

    @Relation(parentColumn = "id", entityColumn = "category_id")
    val tasks: List<Task>
)