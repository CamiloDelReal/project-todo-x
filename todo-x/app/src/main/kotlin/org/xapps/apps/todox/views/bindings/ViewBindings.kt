package org.xapps.apps.todox.views.bindings

import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.graphics.toColorInt
import androidx.databinding.BindingAdapter
import org.xapps.apps.todox.R
import timber.log.Timber
import top.defaults.drawabletoolbox.DrawableBuilder
import java.lang.Exception


object ViewBindings {

    @JvmStatic
    @BindingAdapter("categoryBackground")
    fun categoryBackground(view: View, color: String?) {
        Timber.i("categoryBackground color = $color")
        color?.let {
            if(it.isNotEmpty()) {
                try {
                    val radius = view.context.resources.getDimension(R.dimen.item_category_rounded_radius).toInt()
                    val drawable: Drawable = DrawableBuilder()
                        .rectangle()
                        .solidColor(it.toColorInt())
                        .cornerRadius(radius)
                        .build()
                    view.background = drawable
                    Timber.i("finished")
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
        }
    }

}