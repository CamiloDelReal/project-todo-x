package org.xapps.apps.todox.views.bindings

import android.content.res.ColorStateList
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import org.xapps.apps.todox.R
import org.xapps.apps.todox.views.utils.ColorUtils


object ImageViewBindings {

    @JvmStatic
    @BindingAdapter("tintForeground")
    fun tintForeground(view: ImageView, color: String) {
        val colorInt = if(ColorUtils.isDarkColor(color)) {
            R.color.textOnDark
        } else {
            R.color.textOnLight
        }
        val color = view.context.getColor(colorInt)
        view.imageTintList = ColorStateList.valueOf(color)
    }

    @JvmStatic
    @BindingAdapter("doneStatus")
    fun doneStatus(view: ImageView, done: Boolean) {
        if(done) {
            view.setImageResource(R.drawable.ic_checkbox_marked)
            view.imageTintList = ColorStateList.valueOf(view.resources.getColor(R.color.orange_500, null))
        } else {
            view.setImageResource(R.drawable.ic_checkbox_blank_outline)
            view.imageTintList = ColorStateList.valueOf(view.resources.getColor(R.color.textSecondary, null))
        }
    }

}