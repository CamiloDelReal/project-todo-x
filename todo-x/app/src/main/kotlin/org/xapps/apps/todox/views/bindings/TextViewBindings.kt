package org.xapps.apps.todox.views.bindings

import android.widget.TextView
import androidx.databinding.BindingAdapter
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.models.Category
import org.xapps.apps.todox.core.utils.parseToString
import timber.log.Timber
import java.lang.StringBuilder
import java.time.LocalDate
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
    @BindingAdapter("localDateValue")
    fun localDateValue(
        view: TextView,
        localDateValue: LocalDate?
    ) {
        Timber.i("localDateValue $localDateValue")
        val date = localDateValue?.run {
            parseToString()
        } ?: run {
            view.context.getString(R.string.no_date)
        }
        view.text = date
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

    @JvmStatic
    @BindingAdapter("tasksCount")
    fun tasksCount(view: TextView, count: Int) {
        view.text = view.context.resources.getQuantityString(R.plurals.task_count, count, count)
    }

    @JvmStatic
    @BindingAdapter("tasksRemaining")
    fun tasksRemaining(view: TextView, count: Int) {
        view.text = view.context.resources.getString(R.string.remaining, count)
    }

    @JvmStatic
    @BindingAdapter("todayTasksCount")
    fun todayTasksCount(view: TextView, count: Int) {
        view.text = view.context.resources.getQuantityString(R.plurals.today_task_count, count, count)
    }

}