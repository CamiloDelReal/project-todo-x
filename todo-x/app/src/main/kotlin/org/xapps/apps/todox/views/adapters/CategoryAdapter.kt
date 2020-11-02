package org.xapps.apps.todox.views.adapters

import androidx.databinding.ViewDataBinding
import org.xapps.apps.todox.databinding.ItemCategoryBinding
import org.xapps.apps.todox.services.models.Category
import timber.log.Timber


class CategoryAdapter(items: List<Category>) : ListBindingAdapter<Category>(items) {

    override val itemLayout: Int = org.xapps.apps.todox.R.layout.item_category

    override fun bind(binding: ViewDataBinding, item: Category) {
        when (binding) {
            is ItemCategoryBinding -> {
                binding.data = item
                binding.rootLayout.setOnClickListener {
                    Timber.i("Card clicked")
                }
            }
        }
    }

}