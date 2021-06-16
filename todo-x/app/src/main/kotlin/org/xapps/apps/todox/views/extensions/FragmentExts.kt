package org.xapps.apps.todox.views.extensions

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import es.dmoral.toasty.Toasty
import org.xapps.apps.todox.R


fun Fragment.showError(message: String) {
    Toasty.custom(
        requireContext(),
        message,
        AppCompatResources.getDrawable(
            requireContext(),
            R.drawable.ic_alert_outline
        ),
        ContextCompat.getColor(requireContext(), R.color.error),
        ContextCompat.getColor(requireContext(), R.color.white),
        Toasty.LENGTH_LONG,
        true,
        true
    ).show()
}

fun Fragment.showSuccess(message: String) {
    Toasty.custom(
        requireContext(),
        message,
        AppCompatResources.getDrawable(
            requireContext(),
            R.drawable.ic_check_circle_outline
        ),
        ContextCompat.getColor(requireContext(), R.color.success),
        ContextCompat.getColor(requireContext(), R.color.white),
        Toasty.LENGTH_SHORT,
        true,
        true
    ).show()
}

fun Fragment.showWarning(message: String) {
    Toasty.custom(
        requireContext(),
        message,
        AppCompatResources.getDrawable(
            requireContext(),
            R.drawable.ic_alert_circle_outline
        ),
        ContextCompat.getColor(requireContext(), R.color.warning),
        ContextCompat.getColor(requireContext(), R.color.white),
        Toasty.LENGTH_SHORT,
        true,
        true
    ).show()
}

fun Fragment.hideKeyboard(views: List<View?>) {
    val inputMethodManager: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    views.forEach { view ->
        view?.let {
            if (it.hasFocus()) {
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                return@forEach
            }
        }
    }
}