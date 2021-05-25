package org.xapps.apps.todox.views.bindings

import androidx.databinding.BindingAdapter
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import org.xapps.apps.todox.core.models.Category
import org.xapps.apps.todox.core.utils.debug


object CircularProgressBarBindings {

    @JvmStatic
    @BindingAdapter("progressBackgroundColor")
    fun progressBackgroundColor(view: CircularProgressBar, color: Int) {
        view.backgroundProgressBarColor = color
    }

    @JvmStatic
    @BindingAdapter("progressColor")
    fun progressColor(view: CircularProgressBar, color: Int) {
        view.progressBarColor = color
    }

    @JvmStatic
    @BindingAdapter("progressValue")
    fun progressValue(view: CircularProgressBar, category: Category?) {
        debug<CircularProgressBarBindings>("Binding $category progress given total = ${category?.tasksCount} with pending = ${category?.pendingTasksCount}")
        category?.let {
            view.progress = if (category.tasksCount == 0) {
                100f
            } else {
                (100f - ((category.pendingTasksCount * 100f) / category.tasksCount))
            }
        }
    }

}