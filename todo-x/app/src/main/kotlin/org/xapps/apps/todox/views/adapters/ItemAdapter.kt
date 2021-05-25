package org.xapps.apps.todox.views.adapters

import androidx.databinding.ObservableArrayList
import androidx.databinding.ViewDataBinding
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.models.Item
import org.xapps.apps.todox.core.models.Task
import org.xapps.apps.todox.core.utils.debug
import org.xapps.apps.todox.databinding.ItemItemBinding
import org.xapps.apps.todox.databinding.ItemItemEditBinding


class ItemAdapter(
    private val data: ObservableArrayList<Item>,
    private val isEdit: Boolean,
    private val itemListener: ItemListener? = null
) : ListBindingAdapter<Item>(data) {

    override val itemLayout: Int
        get() = if(isEdit) R.layout.item_item_edit else R.layout.item_item

    override fun bind(bindings: ViewDataBinding, item: Item) {
        when (bindings) {
            is ItemItemEditBinding -> {
                debug<ItemAdapter>("ItemItemEditBinding detected")
                bindings.item = item

                bindings.btnAddAbove.setOnClickListener {
                    val index = items.indexOf(item)
                    data.add(index, Item())
                }

                bindings.btnAddBelow.setOnClickListener {
                    val index = items.indexOf(item)
                    if(index == (items.size - 1)) {
                        data.add(Item())
                    } else {
                        data.add(index + 1, Item())
                    }
                }
            }
            is ItemItemBinding -> {
                debug<ItemAdapter>("ItemItemBinding detected")
                bindings.item = item

                bindings.chxDone.setOnClickListener {
                    debug<ItemAdapter>("Checkbox for done request has been clicked, itemListener = $itemListener")
                    val itemUpdated = item.copy()
                    itemUpdated.done = !itemUpdated.done
                    itemListener?.itemUpdated(itemUpdated)
                }
            }
        }
    }

    interface ItemListener {
        fun itemUpdated(item: Item)
    }

}