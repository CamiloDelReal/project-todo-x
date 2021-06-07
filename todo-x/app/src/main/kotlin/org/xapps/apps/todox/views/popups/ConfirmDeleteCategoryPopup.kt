package org.xapps.apps.todox.views.popups

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import dagger.hilt.android.AndroidEntryPoint
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.repositories.SettingsRepository
import org.xapps.apps.todox.databinding.ContentPopupConfirmDeleteCategoryBinding
import javax.inject.Inject


@AndroidEntryPoint
class ConfirmDeleteCategoryPopup @Inject constructor() : DialogFragment() {

    companion object {

        const val REQUEST_KEY = "ConfirmDeleteCategoryPopup"

        const val POPUP_OPTION = "ConfirmDeleteCategoryPopup"
        const val POPUP_OPTION_DELETE_ALL_TASKS = "OptionDeleteAllTasks"
        const val POPUP_YES = 702
        const val POPUP_NO = 703

        fun showDialog(
            fragmentManager: FragmentManager,
            listener: ((requestKey: String, bundle: Bundle) -> Unit)
        ) {
            val popup = ConfirmDeleteCategoryPopup()
            popup.show(fragmentManager, ConfirmDeleteCategoryPopup::class.java.name)
            popup.setFragmentResultListener(REQUEST_KEY, listener)
        }

    }

    private lateinit var bindings: ContentPopupConfirmDeleteCategoryBinding

    @Inject
    lateinit var settings: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val style = STYLE_NO_FRAME
        val theme = R.style.Popup_Dialog
        setStyle(style, theme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val layoutInflater = LayoutInflater.from(context)
        bindings = ContentPopupConfirmDeleteCategoryBinding.inflate(layoutInflater, container, false)
        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindings.btnYes.setOnClickListener {
            val data = Bundle().apply {
                putInt(POPUP_OPTION, POPUP_YES)
                putBoolean(POPUP_OPTION_DELETE_ALL_TASKS, bindings.chxDeleteTasks.isChecked)
            }
            close(data)
        }

        bindings.btnNo.setOnClickListener {
            val data = Bundle().apply {
                putInt(POPUP_OPTION, POPUP_NO)
            }
            close(data)
        }

        bindings.rootLayout.setOnClickListener {
            close()
        }
    }

    private fun close(data: Bundle = Bundle()) {
        setFragmentResult(REQUEST_KEY, data)
        dismiss()
    }

    override fun onResume() {
        val window = dialog!!.window
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window?.setGravity(Gravity.CENTER)

        dialog?.apply {
            setOnKeyListener { _, keyCode, _ ->
                if ((keyCode ==  KeyEvent.KEYCODE_BACK))
                {
                    close()
                    true
                }
                else false
            }
        }

        super.onResume()
    }

}