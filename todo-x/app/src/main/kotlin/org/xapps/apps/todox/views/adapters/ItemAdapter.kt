package org.xapps.apps.todox.views.adapters

import androidx.databinding.ObservableArrayList
import androidx.databinding.ViewDataBinding
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.models.Item
import org.xapps.apps.todox.core.utils.debug
import org.xapps.apps.todox.databinding.ItemItemBinding
import org.xapps.apps.todox.databinding.ItemItemEditBinding
import ru.rambler.libs.swipe_layout.SwipeLayout
import java.lang.Math.random


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
                bindings.swipeRevealLayout.reset()

                bindings.swipeRevealLayout.setOnSwipeListener(object: SwipeLayout.OnSwipeListener {
                    override fun onBeginSwipe(swipeLayout: SwipeLayout?, moveToRight: Boolean) {}

                    override fun onSwipeClampReached(swipeLayout: SwipeLayout?, moveToRight: Boolean) {
                        debug<ItemAdapter>("Data size = ${data.size}, items size = ${items.size}")
                        if(data.size > 1) {
                            val index = items.indexOf(item)
                            data.removeAt(index)
                        } else {
                            item.done = false
                            item.description = ""
                            val index = items.indexOf(item)
                            data.set(index, item)
                            bindings.swipeRevealLayout.animateReset()
                        }
                    }

                    override fun onLeftStickyEdge(swipeLayout: SwipeLayout?, moveToRight: Boolean) {}

                    override fun onRightStickyEdge(swipeLayout: SwipeLayout?, moveToRight: Boolean) {}
                })

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

                val index = items.indexOf(item)
//                bindings.coloredRow.isVisible = (index % 2 == 0)
                val colorResource = bindings.rootLayout.context.resources.getColor(R.color.backgroundSecondary)
                bindings.rootLayout.setCardBackgroundColor(colorResource)

                bindings.rootLayout.setOnClickListener {
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