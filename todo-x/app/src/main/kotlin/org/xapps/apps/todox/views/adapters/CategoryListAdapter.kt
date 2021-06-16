package org.xapps.apps.todox.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ObservableField
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.xapps.apps.todox.core.models.Category
import org.xapps.apps.todox.databinding.ItemCategoryListBinding
import ru.rambler.libs.swipe_layout.SwipeLayout


class CategoryListAdapter(
        private val itemListener: ItemListener
): PagingDataAdapter<Category, CategoryListAdapter.ItemViewHolder>(CategoryDiffCallback()) {

    private val currentOpenedMenuItem: ObservableField<ItemCategoryListBinding> = ObservableField()

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListAdapter.ItemViewHolder {
        val bindings = ItemCategoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(bindings, currentOpenedMenuItem, itemListener)
    }

    interface ItemListener {
        fun clicked(category: Category)
        fun requestOpen(category: Category)
        fun requestDelete(category: Category)
    }

    class ItemViewHolder(
            private val bindings: ItemCategoryListBinding,
            val wrapperCurrentOpenedMenuItem: ObservableField<ItemCategoryListBinding>,
            itemListener: ItemListener
    ): RecyclerView.ViewHolder(bindings.root) {

        private var category: Category? = null

        init {
            bindings.rootLayout.setOnClickListener {
                itemListener.clicked(category!!)
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
            bindings.btnOpen.setOnClickListener {
                bindings.swipeRevealLayout.animateReset()
                category?.let {
                    itemListener.requestOpen(it)
                }
            }
            bindings.btnDelete.setOnClickListener {
                bindings.swipeRevealLayout.animateReset()
                category?.let {
                    itemListener.requestDelete(it)
                }
            }
        }

        fun bind(category: Category?) {
            this.category = category
            bindings.data = this.category
        }

    }

    class CategoryDiffCallback: DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return (oldItem == newItem)
        }

    }

}