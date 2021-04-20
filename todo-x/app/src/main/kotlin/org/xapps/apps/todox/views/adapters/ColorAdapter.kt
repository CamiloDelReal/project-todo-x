package org.xapps.apps.todox.views.adapters

import androidx.core.view.isVisible
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.databinding.ViewDataBinding
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.models.Color
import org.xapps.apps.todox.databinding.ItemColorBinding


class ColorAdapter(
        colors: List<Color>,
        private val current: ObservableField<String>,
        private val listener: ItemListener)
    : ListBindingAdapter<Color>(colors) {

    init {
        current.addOnPropertyChangedCallback(object: Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                notifyDataSetChanged()
            }
        })
    }

    override val itemLayout: Int
        get() = R.layout.item_color

    override fun bind(bindings: ViewDataBinding, color: Color) {
        bindings as ItemColorBinding
        bindings.btnColor.setOnClickListener {
            listener.clicked(color.value)
        }
        bindings.color = color.value
        bindings.imgCheck.isVisible = (color.value == current.get())
    }


    interface ItemListener {

        fun clicked(colorHex: String)

    }
}