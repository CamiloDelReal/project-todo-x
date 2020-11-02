package org.xapps.apps.todox.views.bindings

import androidx.databinding.BindingAdapter
import com.mikhaellopez.circularprogressbar.CircularProgressBar


object CircularProgressBarBindings {

    @JvmStatic
    @BindingAdapter("progressBackgroundColor")
    fun progressBackgroundColor(view: CircularProgressBar, color: Int) {
        view.backgroundProgressBarColor = color
    }

    @JvmStatic
    @BindingAdapter("progressColor")
    fun progressColor(view: CircularProgressBar, color: Int) {
        view.progressBarColor = color
    }

}