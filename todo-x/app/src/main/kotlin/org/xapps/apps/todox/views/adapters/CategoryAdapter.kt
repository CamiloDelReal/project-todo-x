package org.xapps.apps.todox.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.xapps.apps.todox.core.models.Category
import org.xapps.apps.todox.databinding.ItemCategoryBinding
import timber.log.Timber


class CategoryAdapter(
    private val itemListener: ItemListener
): PagingDataAdapter<Category, CategoryAdapter.ItemViewHolder>(CategoryDiffCallback()) {

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter.ItemViewHolder {
        val bindings = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(bindings, itemListener)
    }

    interface ItemListener {

        fun clicked(category: Category)

    }

    class ItemViewHolder(
        private val bindings: ItemCategoryBinding,
        itemListener: ItemListener
    ): RecyclerView.ViewHolder(bindings.root) {

        private var category: Category? = null

        init {
            bindings.rootLayout.setOnClickListener {
                itemListener.clicked(category!!)
            }
        }

        fun bind(category: Category?) {
            Timber.i("About to bind $category")
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