package org.xapps.apps.todox.views.bindings

import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.graphics.toColorInt
import androidx.databinding.BindingAdapter
import org.xapps.apps.todox.R
import top.defaults.drawabletoolbox.DrawableBuilder


object ViewBindings {

    @JvmStatic
    @BindingAdapter("categoryBackground")
    fun categoryBackground(view: View, color: String) {
        val radius = view.context.resources.getDimension(R.dimen.item_category_rounded_radius).toInt()
        val drawable: Drawable = DrawableBuilder()
                .rectangle()
                .solidColor(color.toColorInt())
                .cornerRadius(radius)
                .build()
        view.background = drawable
    }

}