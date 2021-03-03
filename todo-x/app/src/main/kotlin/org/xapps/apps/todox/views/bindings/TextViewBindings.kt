package org.xapps.apps.todox.views.bindings

import android.widget.TextView
import androidx.databinding.BindingAdapter
import org.xapps.apps.todox.core.models.Category


object TextViewBindings {

    @JvmStatic
    @BindingAdapter("progressValue")
    fun progressValue(view: TextView, category: Category?) {
        category?.let {
            view.text = if (category.tasksCount == 0) {
                "100%"
            } else {
                "${100 - ((category.pendingTasksCount * 100f) / category.tasksCount).toInt()}%"
            }
        }
    }

}