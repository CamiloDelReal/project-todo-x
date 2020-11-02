package org.xapps.apps.todox.views.bindings

import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.databinding.BindingAdapter
import com.google.android.material.card.MaterialCardView
import org.xapps.apps.todox.R
import top.defaults.drawabletoolbox.DrawableBuilder


object MaterialCardViewBindings {

    @JvmStatic
    @BindingAdapter("categoryBackground")
    fun categoryBackground(view: MaterialCardView, color: String) {
        ContextCompat.getColor(view.context, R.color.textOnDarkSecondary)
        Color()
        val bgColor = "#99${color.substring(1)}"
        val drawable: Drawable = DrawableBuilder()
            .rectangle()
            .solidColor(bgColor.toColorInt())
            .strokeWidth(2)
            .strokeColor(color.toColorInt())
            .cornerRadius(24)
            .build()
        view.radius = 24.0f
        view.background = drawable
    }

}