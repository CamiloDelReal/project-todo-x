package org.xapps.apps.todox.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import dagger.hilt.android.AndroidEntryPoint
import org.xapps.apps.todox.R
import org.xapps.apps.todox.databinding.FragmentCalendarBinding
import org.xapps.apps.todox.databinding.ItemCalendarDayBinding
import org.xapps.apps.todox.viewmodels.CalendarViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class CalendarFragment @Inject constructor(): Fragment() {

    private lateinit var bindings: FragmentCalendarBinding

    private val viewModel: CalendarViewModel by viewModels()

    private val today = LocalDate.now()
    private var selectedDate: LocalDate? = null
    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindings = FragmentCalendarBinding.inflate(layoutInflater)
        bindings.lifecycleOwner = viewLifecycleOwner
        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindings.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        bindings.btnNew.setOnClickListener {
            findNavController().navigate(CalendarFragmentDirections.actionCalendarFragmentToEditTaskFragment())
        }

        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        var daysOfWeek = DayOfWeek.values()
        if (firstDayOfWeek != DayOfWeek.MONDAY) {
            val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
            val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)
            daysOfWeek = rhs + lhs
        }

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(10)
        val endMonth = currentMonth.plusMonths(10)
        bindings.calendarView.setup(startMonth, endMonth, daysOfWeek.first())
        bindings.calendarView.scrollToMonth(currentMonth)

        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            val textView = ItemCalendarDayBinding.bind(view).txvDay

            init {
                view.setOnClickListener {
                    if (selectedDate != day.date) {
                        val oldDate = selectedDate
                        selectedDate = day.date
                        oldDate?.let { bindings.calendarView.notifyDateChanged(it) }
                        bindings.calendarView.notifyDateChanged(day.date)
                        // Call updat in viewmodel
                    }
                }
            }
        }

        bindings.calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.textView
                textView.text = day.date.dayOfMonth.toString()
                if (day.owner == DayOwner.THIS_MONTH) {
                    when (day.date) {
                        selectedDate -> {
                            textView.setTextColor(resources.getColor(R.color.white, null))
                            textView.setBackgroundResource(R.drawable.ic_bg_selected_day)
                        }
                        today -> {
                            textView.setTextColor(resources.getColor(R.color.white, null))
                            textView.setBackgroundResource(R.drawable.ic_bg_curent_day)
                        }
                        else -> {
                            textView.setTextColor(resources.getColor(R.color.text, null))
                            textView.background = null
                        }
                    }
                } else {
                    textView.setTextColor(resources.getColor(R.color.textSecondary, null))
                    textView.background = null
                }
            }
        }

        bindings.calendarView.monthScrollListener = {
            bindings.txvYear.text = it.yearMonth.year.toString()
            bindings.txvMonth.text = monthTitleFormatter.format(it.yearMonth)
        }
    }

}