package org.xapps.apps.todox.views.bindings

import android.graphics.drawable.Drawable
import androidx.core.graphics.toColorInt
import androidx.databinding.BindingAdapter
import com.google.android.material.card.MaterialCardView
import org.xapps.apps.todox.R
import top.defaults.drawabletoolbox.DrawableBuilder


object MaterialCardViewBindings {

    @JvmStatic
    @BindingAdapter("categoryBackground")
    fun categoryBackground(view: MaterialCardView, color: String) {
        val radius = view.context.resources.getDimension(R.dimen.item_corner_radius)
        val drawable: Drawable = DrawableBuilder()
            .rectangle()
            .solidColor(color.toColorInt())
            .strokeWidth(2)
            .strokeColor(color.toColorInt())
            .cornerRadius(radius.toInt())
            .build()
        view.radius = radius
        view.background = drawable
    }

    @JvmStatic
    @BindingAdapter("colorBackground")
    fun colorBackground(view: MaterialCardView, color: String) {
        val radius = view.context.resources.getDimension(R.dimen.item_corner_radius)
        val height = view.resources.displayMetrics.heightPixels
        val drawable: Drawable = DrawableBuilder()
                .rectangle()
                .solidColor(color.toColorInt())
                .strokeWidth(2)
                .strokeColor(color.toColorInt())
                .cornerRadius(height / 2)
                .build()
        view.radius = radius
        view.background = drawable
    }

}