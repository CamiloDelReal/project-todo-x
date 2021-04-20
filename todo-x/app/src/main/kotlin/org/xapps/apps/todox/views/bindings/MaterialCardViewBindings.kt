package org.xapps.apps.todox.views.bindings

import androidx.core.graphics.toColorInt
import androidx.databinding.BindingAdapter
import com.google.android.material.card.MaterialCardView
import org.xapps.apps.todox.R


object MaterialCardViewBindings {

    @JvmStatic
    @BindingAdapter("categoryBackground")
    fun categoryBackground(view: MaterialCardView, color: String) {
        val radius = view.context.resources.getDimension(R.dimen.item_corner_radius)
        view.radius = radius
        view.setCardBackgroundColor(color.toColorInt())
    }

    @JvmStatic
    @BindingAdapter("colorBackground")
    fun colorBackground(view: MaterialCardView, color: String) {
        val height = view.resources.displayMetrics.heightPixels
        view.radius = height.toFloat() / 2
        view.setCardBackgroundColor(color.toColorInt())
    }

}