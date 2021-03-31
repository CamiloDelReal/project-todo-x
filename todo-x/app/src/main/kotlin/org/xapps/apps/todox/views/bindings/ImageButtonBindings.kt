package org.xapps.apps.todox.views.bindings

import android.content.res.ColorStateList
import android.widget.ImageButton
import androidx.databinding.BindingAdapter
import org.xapps.apps.todox.R


object ImageButtonBindings {

    @JvmStatic
    @BindingAdapter("colorByPriority")
    fun colorByPriority(view: ImageButton, important: Boolean) {
        val colorResource = if (important) {
            R.color.hightPriority
        } else {
            R.color.textSecondary
        }
        val color = view.context.getColor(colorResource)

        view.imageTintList = ColorStateList.valueOf(color)
    }

    @JvmStatic
    @BindingAdapter("iconByPriority")
    fun iconByPriority(view: ImageButton, important: Boolean) {
        view.setImageResource(if(important) {
            R.drawable.ic_star
        } else {
            R.drawable.ic_star_outline
        })
    }

}