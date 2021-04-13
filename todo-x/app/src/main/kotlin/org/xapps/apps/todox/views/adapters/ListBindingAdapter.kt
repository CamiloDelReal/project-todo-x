package org.xapps.apps.todox.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableList
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView


abstract class ListBindingAdapter<TItem>(items: List<TItem>) :
    RecyclerView.Adapter<ListBindingAdapter.BindingViewHolder>() {

    var items: List<TItem> = ArrayList()
        set(value) {
            val lastList = field
            if (lastList !== value) {
                if (lastList is ObservableList) {
                    lastList.removeOnListChangedCallback(onListChangedCallback)
                }
                if (value is ObservableList) {
                    value.addOnListChangedCallback(onListChangedCallback)
                }
                if (lastList.isNotEmpty()) {
                    notifyItemRangeRemoved(0, lastList.size)
                }
            }
            field = value
            notifyItemRangeInserted(0, value.size)
        }

    class BindingViewHolder constructor(val bindings: ViewDataBinding) :
        RecyclerView.ViewHolder(bindings.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val bindings = createBinding(parent)
        return BindingViewHolder(bindings)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun createBinding(parent: ViewGroup): ViewDataBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            itemLayout,
            parent,
            false
        )
    }

    abstract val itemLayout: Int

    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        if (position < itemCount) {
            bind(holder.bindings, getItem(position))
            holder.bindings.executePendingBindings()
        }
    }

    private fun getItem(position: Int): TItem {
        return items[position]
    }

    protected abstract fun bind(bindings: ViewDataBinding, item: TItem)

    private val onListChangedCallback =
        object : ObservableList.OnListChangedCallback<ObservableList<TItem>>() {
            override fun onChanged(sender: ObservableList<TItem>?) {
                notifyDataSetChanged()
            }

            override fun onItemRangeRemoved(
                sender: ObservableList<TItem>?,
                positionStart: Int,
                itemCount: Int
            ) {
                notifyItemRangeRemoved(positionStart, itemCount)
            }

            override fun onItemRangeMoved(
                sender: ObservableList<TItem>?,
                fromPosition: Int,
                toPosition: Int,
                itemCount: Int
            ) {
                notifyItemMoved(fromPosition, toPosition)
            }

            override fun onItemRangeInserted(
                sender: ObservableList<TItem>?,
                positionStart: Int,
                itemCount: Int
            ) {
                notifyItemRangeChanged(positionStart, itemCount)
            }

            override fun onItemRangeChanged(
                sender: ObservableList<TItem>?,
                positionStart: Int,
                itemCount: Int
            ) {
                notifyItemRangeChanged(positionStart, itemCount)
            }
        }

    init {
        this.items = items
    }
}