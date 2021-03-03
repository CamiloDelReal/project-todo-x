package org.xapps.apps.todox.views.bindings

import android.graphics.drawable.Drawable
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.databinding.BindingAdapter
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.models.Category
import top.defaults.drawabletoolbox.DrawableBuilder


object MotionLayoutBindings {

    @JvmStatic
    @BindingAdapter("categoryBackground")
    fun categoryBackground(view: MotionLayout, category: Category?) {
        category?.let {
            ContextCompat.getColor(view.context, R.color.textOnDarkSecondary)
            val drawable: Drawable = DrawableBuilder()
                .rectangle()
                .solidColor(it.color.toColorInt())
                .build()
            view.background = drawable
        }
    }

}