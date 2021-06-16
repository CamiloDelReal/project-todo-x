package org.xapps.apps.todox.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.models.Task
import org.xapps.apps.todox.core.utils.debug
import org.xapps.apps.todox.core.utils.error
import org.xapps.apps.todox.core.utils.info
import org.xapps.apps.todox.core.utils.parseToString
import org.xapps.apps.todox.databinding.FragmentCalendarBinding
import org.xapps.apps.todox.databinding.ItemCalendarDayBinding
import org.xapps.apps.todox.viewmodels.CalendarViewModel
import org.xapps.apps.todox.viewmodels.Constants
import org.xapps.apps.todox.views.adapters.TaskWithItemsAndCategoryAndNoHeaderAdapter
import org.xapps.apps.todox.views.extensions.showError
import org.xapps.apps.todox.views.popups.ConfirmPopup
import org.xapps.apps.todox.views.utils.Message
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

    private var messageJob: Job? = null
    private var paginationStatesJob: Job? = null
    private var paginationJob: Job? = null

    private val today = LocalDate.now()
    private var selectedDate: LocalDate? = null
    private val monthTitleFormatter = DateTimeFormatter.ofPattern(Constants.MONTH_PATTERN)

    private lateinit var taskAdapter: TaskWithItemsAndCategoryAndNoHeaderAdapter

    private val tasksItemListener = object: TaskWithItemsAndCategoryAndNoHeaderAdapter.ItemListener {
        override fun clicked(task: Task) {
            findNavController().navigate(CalendarFragmentDirections.actionCalendarFragmentToTaskDetailsFragment(task.id))
        }

        override fun taskUpdated(task: Task) {
            viewModel.updateTask(task)
        }

        override fun requestEdit(task: Task) {
            findNavController().navigate(CalendarFragmentDirections.actionCalendarFragmentToEditTaskFragment(task.id))
        }

        override fun requestDelete(task: Task) {
            ConfirmPopup.showDialog(
                parentFragmentManager,
                getString(R.string.confirm_delete_task)
            ) { _, data ->
                val option = if (data.containsKey(ConfirmPopup.POPUP_OPTION)) {
                    data.getInt(ConfirmPopup.POPUP_OPTION)
                } else {
                    -1
                }
                when(option) {
                    ConfirmPopup.POPUP_YES -> {
                        viewModel.deleteTask(task)
                    }
                    ConfirmPopup.POPUP_NO -> {
                        info<CalendarFragment>("User has cancelled the delete operation")
                    }
                }
            }
        }

        override fun requestComplete(task: Task) {
            viewModel.completeTask(task)
        }
    }

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

        bindings.lstTasks.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        taskAdapter = TaskWithItemsAndCategoryAndNoHeaderAdapter(tasksItemListener)
        bindings.lstTasks.adapter = taskAdapter

        taskAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                bindings.noTasksView.isVisible = (taskAdapter.itemCount == 0)
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                bindings.noTasksView.isVisible = (taskAdapter.itemCount == 0)
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {}

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                bindings.noTasksView.isVisible = (taskAdapter.itemCount == 0)
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {}

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {}
        })

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
                        updateCurrentDay(day.date)
                        viewModel.tasksByDate(day.date)
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

        messageJob = lifecycleScope.launchWhenResumed {
            viewModel.messageFlow.collect {
                when (it) {
                    is Message.Loading -> {
                        bindings.progressbar.isVisible = true
                    }
                    is Message.Loaded -> {
                        bindings.progressbar.isVisible = false
                    }
                    is Message.Success -> {
                        bindings.progressbar.isVisible = false
                    }
                    is Message.Error -> {
                        bindings.progressbar.isVisible = false
                        error<CalendarFragment>("Error received: ${it.exception.localizedMessage}")
                        showError(it.exception.message ?: getString(R.string.unknown_error))
                    }
                }
            }
        }

        paginationStatesJob = lifecycleScope.launch {
            taskAdapter.loadStateFlow.collectLatest { loadStates ->
                bindings.progressbar.isVisible = (loadStates.refresh is LoadState.Loading)
            }
        }

        paginationJob = lifecycleScope.launchWhenResumed {
            viewModel.tasksFlow
                .collectLatest {
                    debug<CalendarFragment>("Pagination data received $it")
                    taskAdapter.submitData(it)
                }
        }

        viewModel.lastDateForTasks?.let {
            updateCurrentDay(it)
            viewModel.tasksByDate(it)
        } ?: run {
            val current = LocalDate.now()
            updateCurrentDay(current)
            viewModel.tasksByDate(current)
        }
    }

    private fun updateCurrentDay(date: LocalDate) {
        bindings.txvDate.text = date.parseToString(Constants.DATE_PATTERN_UI)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        messageJob?.cancel()
        messageJob = null
        paginationStatesJob?.cancel()
        paginationStatesJob = null
        paginationJob?.cancel()
        paginationJob = null
    }

}