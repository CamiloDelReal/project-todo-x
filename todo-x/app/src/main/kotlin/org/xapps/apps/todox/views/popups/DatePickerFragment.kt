package org.xapps.apps.todox.views.popups

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject


@AndroidEntryPoint
class DatePickerFragment @Inject constructor() : DialogFragment(),
    DatePickerDialog.OnDateSetListener {

    companion object {
        const val REQUEST_KEY = "datePickerDefault"

        const val ATTR_DATE = "attrDate"

        fun showPicker(
            fragmentManager: FragmentManager,
            date: LocalDate? = null,
            listener: ((requestKey: String, bundle: Bundle) -> Unit)
        ) {
            val datePriv = date ?: LocalDate.now()
            Timber.i("Date now =  $datePriv");
            val bundle = Bundle().apply {
                putSerializable(ATTR_DATE, datePriv)
            }
            val datePickerFragment = DatePickerFragment()
            datePickerFragment.arguments = bundle
            datePickerFragment.show(fragmentManager, DatePickerFragment.javaClass.name)
            datePickerFragment.setFragmentResultListener(REQUEST_KEY, listener)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val (dayOfMonth, month, year) = arguments?.let {
            val date = it.getSerializable(ATTR_DATE) as LocalDate
            arrayOf(date.dayOfMonth, date.monthValue, date.year)
        } ?: run {
            val date = LocalDate.now()
            arrayOf(date.dayOfMonth, date.monthValue, date.year)
        }

        val dialog = DatePickerDialog(requireActivity(), this, year, month - 1, dayOfMonth)

        return dialog
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val time = LocalDate.of(year, month + 1, dayOfMonth)
        val bundle = Bundle().apply {
            putSerializable(ATTR_DATE, time)
        }
        setFragmentResult(REQUEST_KEY, bundle)
    }
}