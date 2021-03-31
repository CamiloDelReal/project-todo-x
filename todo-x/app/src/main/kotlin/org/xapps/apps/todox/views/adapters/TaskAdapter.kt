package org.xapps.apps.todox.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.xapps.apps.todox.core.models.TaskWithItems
import org.xapps.apps.todox.databinding.ItemTaskWithItemsBinding
import timber.log.Timber


class TaskWithItemsAdapter(private val itemListener: ItemListener): PagingDataAdapter<TaskWithItems, TaskWithItemsAdapter.ItemViewHolder>(TaskDiffCallback()) {

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskWithItemsAdapter.ItemViewHolder {
        val bindings = ItemTaskWithItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(bindings, itemListener)
    }

    interface ItemListener {

        fun clicked(task: TaskWithItems)

    }

    class ItemViewHolder(
        private val bindings: ItemTaskWithItemsBinding,
        itemListener: ItemListener
    ): RecyclerView.ViewHolder(bindings.root) {

        private var task: TaskWithItems? = null

        init {
            bindings.rootLayout.setOnClickListener {
                itemListener.clicked(task!!)
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