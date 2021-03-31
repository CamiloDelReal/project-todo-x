package org.xapps.apps.todox.views.bindings

import android.widget.TextView
import androidx.databinding.BindingAdapter
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.models.Category
import org.xapps.apps.todox.core.utils.parseToString
import timber.log.Timber
import java.lang.StringBuilder
import java.time.LocalTime


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

    @JvmStatic
    @BindingAdapter(value = ["localTimeStartValue", "localTimeEndValue"], requireAll = false)
    fun localTimeValue(
        view: TextView,
        localTimeStartValue: LocalTime?,
        localTimeEndValue: LocalTime?
    ) {
        Timber.i("localTimeValue $localTimeStartValue $localTimeEndValue")
        val timestamp = localTimeStartValue?.run {
            val builder = StringBuilder().append(parseToString())
            localTimeEndValue?.let { builder.append(" - ").append(it.parseToString()) }
            builder.toString()
        } ?: run {
            view.context.getString(R.string.unspecified_time)
        }
        view.text = timestamp
    }

}