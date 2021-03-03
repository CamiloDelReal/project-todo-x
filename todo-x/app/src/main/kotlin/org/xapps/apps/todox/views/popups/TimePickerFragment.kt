package org.xapps.apps.todox.views.popups

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import com.skydoves.whatif.whatIf
import dagger.hilt.android.AndroidEntryPoint
import org.xapps.apps.todox.views.custom.RangeTimePickerDialog
import java.time.LocalTime
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class TimePickerFragment @Inject constructor() : DialogFragment(),
    TimePickerDialog.OnTimeSetListener {

    companion object {
        const val REQUEST_KEY = "timePickerDefault"

        const val ATTR_TIME = "attrTime"
        private const val ATTR_MIN_TIME = "attrMinTime"
        private const val ATTR_MAX_TIME = "attrMaxTime"
        private const val ATTR_USE_24_HOURS = "attrUse24Hours"

        fun showPicker(
            fragmentManager: FragmentManager,
            time: LocalTime? = null,
            minTime: LocalTime? = null,
            maxTime: LocalTime? = null,
            use24Hours: Boolean = true,
            listener: ((requestKey: String, bundle: Bundle) -> Unit)
        ) {
            val timePriv = time ?: LocalTime.now()
            val bundle = Bundle().apply {
                putSerializable(ATTR_TIME, timePriv)
                whatIf(minTime != null) { putSerializable(ATTR_MIN_TIME, minTime) }
                whatIf(maxTime != null) { putSerializable(ATTR_MAX_TIME, maxTime) }
                putBoolean(ATTR_USE_24_HOURS, use24Hours)
            }
            val timePickerFragment = TimePickerFragment()
            timePickerFragment.arguments = bundle
            timePickerFragment.show(fragmentManager, TimePickerFragment.javaClass.name)
            timePickerFragment.setFragmentResultListener(REQUEST_KEY, listener)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val (hourOfDay, minute, use24Hours, minTime, maxTime) = arguments?.let {
            val time = it.getSerializable(ATTR_TIME) as LocalTime
            val min = if (it.containsKey(ATTR_MIN_TIME)) {
                it.getSerializable(ATTR_MIN_TIME) as LocalTime
            } else {
                null
            }
            val max = if (it.containsKey(ATTR_MAX_TIME)) {
                it.getSerializable(ATTR_MAX_TIME) as LocalTime
            } else {
                null
            }
            arrayOf(time.hour, time.minute, it.getBoolean(ATTR_USE_24_HOURS), min, max)
        } ?: run {
            val time = LocalTime.now()
            arrayOf(time.hour, time.minute, true, null, null)
        }

        val dialog = RangeTimePickerDialog(requireActivity(), this, hourOfDay as Int, minute as Int, use24Hours as Boolean)
        minTime?.let {
            it as LocalTime
            dialog.setMin(it.hour, it.minute)
        }
        maxTime?.let {
            it as LocalTime
            dialog.setMax(it.hour, it.minute)
        }

        return dialog
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val time = LocalTime.of(hourOfDay, minute)
        val bundle = Bundle().apply {
            putSerializable(ATTR_TIME, time)
        }
        setFragmentResult(REQUEST_KEY, bundle)
    }
}