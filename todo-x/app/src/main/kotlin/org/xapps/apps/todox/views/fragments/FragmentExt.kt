package org.xapps.apps.todox.views.fragments

import android.os.Build
import android.view.View
import android.view.WindowInsetsController
import androidx.fragment.app.Fragment


fun Fragment.setStatusBarForegoundColor(isLightStatusBar: Boolean) {
    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        val decorView = requireActivity().window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        if(isLightStatusBar) {
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        } else {
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }
    } else {
        val statusBarAppearance = if(isLightStatusBar) WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS else 0
        requireActivity().window.insetsController?.setSystemBarsAppearance(statusBarAppearance, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
    }
}

