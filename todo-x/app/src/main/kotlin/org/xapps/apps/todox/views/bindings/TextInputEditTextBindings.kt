package org.xapps.apps.todox.views.bindings

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputEditText
import org.xapps.apps.todox.core.utils.debug
import org.xapps.apps.todox.core.utils.parseToString
import java.time.LocalDate


object TextInputEditTextBindings {

    @JvmStatic
    @BindingAdapter("date")
    fun date(view: TextInputEditText, date: LocalDate?) {
        debug<TextInputEditTextBindings>("Date $date")
        date?.let {
            view.setText(date.parseToString())
        }
    }

}