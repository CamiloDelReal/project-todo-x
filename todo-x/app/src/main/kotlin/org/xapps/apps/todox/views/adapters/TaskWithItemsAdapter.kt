package org.xapps.apps.todox.views.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.ObservableField
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.models.Task
import org.xapps.apps.todox.core.models.TaskWithItems
import org.xapps.apps.todox.core.utils.debug
import org.xapps.apps.todox.core.utils.info
import org.xapps.apps.todox.core.utils.parseToString
import org.xapps.apps.todox.databinding.ItemDateHeaderBinding
import org.xapps.apps.todox.databinding.ItemTaskWithItemsBinding
import ru.rambler.libs.swipe_layout.SwipeLayout
import timber.log.Timber
import java.time.LocalDate


class TaskWithItemsAdapter(
    private val itemListener: ItemListener
): PagingDataAdapter<TaskWithItems, TaskWithItemsAdapter.ItemViewHolder>(TaskDiffCallback()), DateHeaderDecoration.DecorationSupport<TaskWithItemsAdapter.HeaderHolder> {

    private val currentOpenedMenuItem: ObservableField<ItemTaskWithItemsBinding> = ObservableField()

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val bindings = ItemTaskWithItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(bindings, currentOpenedMenuItem, itemListener)
    }

    interface ItemListener {
        fun clicked(task: Task)
        fun taskUpdated(task: Task)
        fun requestEdit(task: Task)
        fun requestDelete(task: Task)
        fun requestComplete(task: Task)
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
        val bindings: ItemTaskWithItemsBinding,
        val wrapperCurrentOpenedMenuItem: ObservableField<ItemTaskWithItemsBinding>,
        itemListener: ItemListener
    ): RecyclerView.ViewHolder(bindings.root) {

        private var task: TaskWithItems? = null

        init {
            bindings.rootLayout.setOnClickListener {
                bindings.swipeRevealLayout.animateReset()
                task?.task?.let {
                    itemListener.clicked(it)
                }
            }
            bindings.swipeRevealLayout.setOnSwipeListener(object: SwipeLayout.OnSwipeListener {
                override fun onBeginSwipe(swipeLayout: SwipeLayout?, moveToRight: Boolean) {}

                override fun onSwipeClampReached(swipeLayout: SwipeLayout?, moveToRight: Boolean) {
                    wrapperCurrentOpenedMenuItem.get()?.apply {
                        if(this != bindings) {
                            swipeRevealLayout.animateReset()
                        }
                    }
                    wrapperCurrentOpenedMenuItem.set(bindings)
                }

                override fun onLeftStickyEdge(swipeLayout: SwipeLayout?, moveToRight: Boolean) {}

                override fun onRightStickyEdge(swipeLayout: SwipeLayout?, moveToRight: Boolean) {}
            })
            bindings.btnChangePriority.setOnClickListener {
                task?.task?.let {
                    val taskUpdated = it.copy()
                    taskUpdated.important = !taskUpdated.important
                    itemListener.taskUpdated(taskUpdated)
                }
            }
            bindings.btnComplete.setOnClickListener {
                bindings.swipeRevealLayout.animateReset()
                task?.task?.let {
                    itemListener.requestComplete(it)
                }
            }
            bindings.btnEdit.setOnClickListener {
                bindings.swipeRevealLayout.animateReset()
                task?.task?.let {
                    itemListener.requestEdit(it)
                }
            }
            bindings.btnDelete.setOnClickListener {
                bindings.swipeRevealLayout.animateReset()
                task?.task?.let {
                    itemListener.requestDelete(it)
                }
            }
        }

        fun bind(task: TaskWithItems?) {
            debug<TaskWithItemsAdapter>("About to bind $task")
            this.task = task
            bindings.data = this.task
            bindings.btnComplete.isVisible = !(task!!.task.done)
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