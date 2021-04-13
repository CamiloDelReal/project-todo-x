package org.xapps.apps.todox.views.adapters

import androidx.databinding.ViewDataBinding
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.models.Color
import org.xapps.apps.todox.databinding.ItemColorBinding


class ColorAdapter(colors: List<Color>) : ListBindingAdapter<Color>(colors) {

    override val itemLayout: Int
        get() = R.layout.item_color

    override fun bind(bindings: ViewDataBinding, color: Color) {
        bindings as ItemColorBinding
        bindings.color = color.value
    }

}