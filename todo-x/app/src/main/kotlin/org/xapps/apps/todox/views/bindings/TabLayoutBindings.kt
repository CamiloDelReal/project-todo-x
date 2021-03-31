package org.xapps.apps.todox.views.bindings

import android.content.res.ColorStateList
import androidx.databinding.BindingAdapter
import com.google.android.material.tabs.TabLayout
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.models.Category
import org.xapps.apps.todox.views.utils.ColorUtils
import timber.log.Timber


object TabLayoutBindings {

    @JvmStatic
    @BindingAdapter("tabTextColorByBackground")
    fun tabTextColorByBackground(tabLayout: TabLayout, category: Category?) {
        category?.let {
            val textColorResource = if (ColorUtils.isDarkColor(it.color)) {
                R.color.text_on_dark_tabitem
            } else {
                R.color.text_on_light_tabitem
            }
            val textColor = tabLayout.context.getColorStateList(textColorResource)
            tabLayout.tabTextColors = textColor
        }
    }

    @JvmStatic
    @BindingAdapter("tabIndicatorColorByBackground")
    fun tabIndicatorColorByBackground(tabLayout: TabLayout, category: Category?) {
        category?.let {
            val textColorResource = if (ColorUtils.isDarkColor(it.color)) {
                R.color.textOnDark
            } else {
                R.color.textOnLight
            }
            val textColor = tabLayout.context.getColor(textColorResource)
            tabLayout.setSelectedTabIndicatorColor(textColor)
        }
    }

}