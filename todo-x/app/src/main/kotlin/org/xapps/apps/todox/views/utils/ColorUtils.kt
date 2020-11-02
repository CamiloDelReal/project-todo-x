package org.xapps.apps.todox.views.utils

import android.graphics.Color
import androidx.core.graphics.toColorInt


object ColorUtils {

    @JvmStatic
    fun isDarkColor(color: String): Boolean =
        isDarkColor(color.toColorInt())

    @JvmStatic
    fun isDarkColor(color: Int): Boolean =
        (1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255) >= 0.35

}