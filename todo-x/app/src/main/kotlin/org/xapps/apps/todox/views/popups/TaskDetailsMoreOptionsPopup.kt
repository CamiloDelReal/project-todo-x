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
import org.xapps.apps.todox.databinding.ContentPopupTaskDetailsMoreOptionsBinding
import javax.inject.Inject


@AndroidEntryPoint
class TaskDetailsMoreOptionsPopup @Inject constructor(
    private val isCompleted: Boolean
) : DialogFragment() {

    companion object {

        const val REQUEST_KEY = "TaskDetailsMoreOptionsPopup"

        const val MORE_OPTIONS_POPUP_OPTION = "TaskDetailsMoreOptionsPopup"
        const val MORE_OPTIONS_POPUP_COMPLETE = 302
        const val MORE_OPTIONS_POPUP_UNCOMPLETE = 303
        const val MORE_OPTIONS_POPUP_EDIT = 304
        const val MORE_OPTIONS_POPUP_DELETE = 305

        fun showDialog(
            fragmentManager: FragmentManager,
            isCompleted: Boolean,
            listener: ((requestKey: String, bundle: Bundle) -> Unit)
        ) {
            val popup = TaskDetailsMoreOptionsPopup(isCompleted)
            popup.show(fragmentManager, TaskDetailsMoreOptionsPopup::class.java.name)
            popup.setFragmentResultListener(REQUEST_KEY, listener)
        }

    }

    private lateinit var bindings: ContentPopupTaskDetailsMoreOptionsBinding

    @Inject
    lateinit var settings: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val style = STYLE_NO_FRAME
        val theme = R.style.Popup_Menu
        setStyle(style, theme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layoutInflater = LayoutInflater.from(context)
        bindings = ContentPopupTaskDetailsMoreOptionsBinding.inflate(layoutInflater, container, false)
        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindings.btnComplete.text = if(isCompleted) {
            getString(R.string.uncomplete)
        } else {
            getString(R.string.complete)
        }

        bindings.btnComplete.setOnClickListener {
            val data = Bundle().apply {
                putInt(MORE_OPTIONS_POPUP_OPTION, if(isCompleted) MORE_OPTIONS_POPUP_UNCOMPLETE else MORE_OPTIONS_POPUP_COMPLETE)
            }
            close(data)
        }

        bindings.btnEdit.setOnClickListener {
            val data = Bundle().apply {
                putInt(MORE_OPTIONS_POPUP_OPTION, MORE_OPTIONS_POPUP_EDIT)
            }
            close(data)
        }

        bindings.btnDelete.setOnClickListener {
            val data = Bundle().apply {
                putInt(MORE_OPTIONS_POPUP_OPTION, MORE_OPTIONS_POPUP_DELETE)
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