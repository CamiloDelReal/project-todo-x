package org.xapps.apps.todox.views.popups

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import dagger.hilt.android.AndroidEntryPoint
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.settings.SettingsService
import org.xapps.apps.todox.databinding.ContentPopupTasksListMoreOptionsBinding
import org.xapps.apps.todox.viewmodels.FilterType
import javax.inject.Inject


@AndroidEntryPoint
class TasksListMoreOptionsPopup @Inject constructor(private val starterFilter: FilterType) : DialogFragment() {

    companion object {

        const val REQUEST_KEY = "TasksListMoreOptionsPopup"

        const val MORE_OPTIONS_FILTER_OPTION = "filterSelected"
        const val MORE_OPTIONS_FILTER_IMPORTANT = 203
        const val MORE_OPTIONS_FILTER_IN_SCHEDULE = 204
        const val MORE_OPTIONS_FILTER_TODAY = 205
        const val MORE_OPTIONS_FILTER_ALL = 206
        const val MORE_OPTIONS_FILTER_COMPLETED = 207

        fun showDialog(
                fragmentManager: FragmentManager,
                starterFilter: FilterType,
                listener: ((requestKey: String, bundle: Bundle) -> Unit)
        ) {
            val popup = TasksListMoreOptionsPopup(starterFilter)
            popup.show(fragmentManager, TasksListMoreOptionsPopup::class.java.name)
            popup.setFragmentResultListener(REQUEST_KEY, listener)
        }

    }

    private lateinit var bindings: ContentPopupTasksListMoreOptionsBinding

    @Inject
    lateinit var settings: SettingsService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val style = STYLE_NO_FRAME
        val theme = R.style.PopupStyle
        setStyle(style, theme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layoutInflater = LayoutInflater.from(context)
        bindings = ContentPopupTasksListMoreOptionsBinding.inflate(layoutInflater, container, false)
        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when(starterFilter) {
            FilterType.IMPORTANT -> bindings.btnImportant.isChecked = true
            FilterType.SCHEDULED -> bindings.btnInSchedule.isChecked= true
            FilterType.TODAY -> bindings.btnToday.isChecked = true
            FilterType.ALL -> bindings.btnAll.isChecked = true
            FilterType.COMPLETED -> bindings.btnCompleted.isChecked = true
            else -> bindings.btnAll.isChecked = true
        }

        bindings.rootLayout.setOnClickListener {
            close()
        }

        bindings.buttonGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            val filter = when(checkedId) {
                R.id.btnImportant -> FilterType.IMPORTANT
                R.id.btnInSchedule -> FilterType.SCHEDULED
                R.id.btnToday -> FilterType.TODAY
                R.id.btnAll -> FilterType.ALL
                R.id.btnCompleted -> FilterType.COMPLETED
                else -> FilterType.ALL
            }
            val data = Bundle().apply {
                putSerializable(MORE_OPTIONS_FILTER_OPTION, filter)
            }
            close(data)
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