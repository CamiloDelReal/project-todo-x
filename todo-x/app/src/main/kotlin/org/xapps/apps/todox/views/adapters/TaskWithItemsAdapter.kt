package org.xapps.apps.todox.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.models.Task
import org.xapps.apps.todox.core.models.TaskWithItems
import org.xapps.apps.todox.core.utils.parseToString
import org.xapps.apps.todox.databinding.ItemDateHeaderBinding
import org.xapps.apps.todox.databinding.ItemTaskWithItemsBinding
import timber.log.Timber
import java.time.LocalDate


class TaskWithItemsAdapter(
    private val itemListener: ItemListener
): PagingDataAdapter<TaskWithItems, TaskWithItemsAdapter.ItemViewHolder>(TaskDiffCallback()), DateHeaderDecoration.DecorationSupport<TaskWithItemsAdapter.HeaderHolder> {

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val bindings = ItemTaskWithItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(bindings, itemListener)
    }

    interface ItemListener {
        fun clicked(task: Task)
        fun taskUpdated(task: Task)
    }

    override fun hasHeader(index: Int): Boolean {
        return true
    }

    override fun headerId(index: Int): Long {
        return getItem(index)?.task?.date?.toEpochDay() ?: Long.MAX_VALUE
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): HeaderHolder {
        val bindings = ItemDateHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HeaderHolder(bindings)
    }

    override fun onBindHeaderViewHolder(view: HeaderHolder, headerId: Long, index: Int) {
        view.bind(getItem(index)?.task?.date)
    }

    class HeaderHolder(val bindings: ItemDateHeaderBinding) : RecyclerView.ViewHolder(bindings.root) {

        fun bind(date: LocalDate?) {
            date?.let {
                bindings.txvMonth.text = it.parseToString("MMM")
                bindings.txvDay.text = it.parseToString("dd")
                bindings.txvYear.text = it.parseToString("yyyy")
            } ?: run {
                bindings.txvMonth.text = "-"
                bindings.txvDay.text = "-"
                bindings.txvYear.text = bindings.rootLayout.context.getString(R.string.no_date)
            }
        }

    }

    class ItemViewHolder(
        private val bindings: ItemTaskWithItemsBinding,
        itemListener: ItemListener
    ): RecyclerView.ViewHolder(bindings.root) {

        private var task: TaskWithItems? = null

        init {
            bindings.rootLayout.setOnClickListener {
                itemListener.clicked(task?.task!!)
            }
            bindings.btnChangePriority.setOnClickListener {
                task?.task?.let {
                    val taskUpdated = it.copy()
                    taskUpdated.important = !taskUpdated.important
                    itemListener.taskUpdated(taskUpdated)
                }
            }
        }

        fun bind(task: TaskWithItems?) {
            Timber.i("About to bind $task")
            this.task = task
            bindings.data = this.task
        }

    }

    class TaskDiffCallback: DiffUtil.ItemCallback<TaskWithItems>() {
        override fun areItemsTheSame(oldItem: TaskWithItems, newItem: TaskWithItems): Boolean {
            return oldItem.task.id == newItem.task.id
        }

        override fun areContentsTheSame(oldItem: TaskWithItems, newItem: TaskWithItems): Boolean {
            return (oldItem == newItem)
        }

    }

}