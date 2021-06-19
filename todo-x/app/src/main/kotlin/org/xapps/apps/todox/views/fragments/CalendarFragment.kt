package org.xapps.apps.todox.views.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.toColorInt
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
import com.kizitonwose.calendarview.utils.yearMonth
import com.nex3z.flowlayout.FlowLayout
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
import org.xapps.apps.todox.databinding.ItemCategoryDotBinding
import org.xapps.apps.todox.viewmodels.CalendarViewModel
import org.xapps.apps.todox.viewmodels.Constants
import org.xapps.apps.todox.views.adapters.TaskWithItemsAndCategoryAndNoHeaderAdapter
import org.xapps.apps.todox.views.extensions.showError
import org.xapps.apps.todox.views.popups.ConfirmPopup
import org.xapps.apps.todox.views.utils.Message
import top.defaults.drawabletoolbox.DrawableBuilder
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
    private var calendarTasksJob: Job? = null

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

    private val onBackPressedCallback: OnBackPressedCallback by lazy {
        object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
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

        viewModel.loadMonthTasks(YearMonth.now())
        selectedDate = LocalDate.now()
        val startMonth = viewModel.currentYearMonth!!.minusMonths(10)
        val endMonth = viewModel.currentYearMonth!!.plusMonths(10)
        bindings.calendarView.setup(startMonth, endMonth, daysOfWeek.first())
        bindings.calendarView.scrollToMonth(viewModel.currentYearMonth!!)

        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            val textView: TextView
            val categoriesLayout: FlowLayout

            init {
                val itemBindings = ItemCalendarDayBinding.bind(view)
                textView = itemBindings.txvDay
                categoriesLayout = itemBindings.categoriesLayout

                view.setOnClickListener {
                    if (selectedDate != day.date) {
                        val oldDate = selectedDate
                        selectedDate = day.date
                        oldDate?.let { bindings.calendarView.notifyDateChanged(it) }
                        bindings.calendarView.notifyDateChanged(day.date)
                        bindings.calendarView.scrollToMonth(day.date.yearMonth)
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
                container.textView.text = day.date.dayOfMonth.toString()
                container.categoriesLayout.removeAllViews()
                if (day.owner == DayOwner.THIS_MONTH) {
                    when (day.date) {
                        selectedDate -> {
                            container.textView.setTextColor(resources.getColor(R.color.white, null))
                            container.textView.setBackgroundResource(R.drawable.ic_bg_selected_day)
                            container.categoriesLayout.removeAllViews()
                        }
                        today -> {
                            container.textView.setTextColor(resources.getColor(R.color.text, null))
                            container.textView.setBackgroundResource(R.drawable.ic_bg_curent_day)
                        }
                        else -> {
                            container.textView.setTextColor(resources.getColor(R.color.text, null))
                            container.textView.background = null
                        }
                    }

                    var categories = viewModel.tasksCategoriesByDate(day.date)
                    if(categories.isNotEmpty()) {
                        val moreIndicator = categories.size > 5
                        if(moreIndicator) {
                            val countToDrop = categories.size - 4
                            categories = categories.dropLast(countToDrop)
                        }
                        categories.forEach {
                            val dotBindings = ItemCategoryDotBinding.inflate(layoutInflater)
                            val params = ViewGroup.LayoutParams(
                                view.context.resources.getDimension(R.dimen.item_category_dot_size).toInt(),
                                view.context.resources.getDimension(R.dimen.item_category_dot_size).toInt()
                            )
                            dotBindings.root.layoutParams = params
                            val radius = view.context.resources.getDimension(R.dimen.item_category_dot_radius).toInt()
                            val drawable: Drawable = DrawableBuilder()
                                .rectangle()
                                .cornerRadius(radius)
                                .solidColor(it.color.toColorInt())
                                .build()
                            dotBindings.dot.background = drawable
                            container.categoriesLayout.addView(dotBindings.root)
                        }
                        if(moreIndicator) {
                            val dotBindings = ItemCategoryDotBinding.inflate(layoutInflater)
                            val params = ViewGroup.LayoutParams(
                                view.context.resources.getDimension(R.dimen.item_category_dot_size).toInt(),
                                view.context.resources.getDimension(R.dimen.item_category_dot_size).toInt()
                            )
                            dotBindings.root.layoutParams = params
                            val drawable: Drawable = DrawableBuilder()
                                .rectangle()
                                .cornerRadius(view.context.resources.getDimension(R.dimen.item_category_dot_radius).toInt())
                                .strokeColor(view.context.resources.getColor(R.color.blue_500, null))
                                .strokeWidth(view.context.resources.getDimension(R.dimen.item_category_dot_stroke_width).toInt())
                                .build()
                            dotBindings.dot.background = drawable
                            container.categoriesLayout.addView(dotBindings.root)
                        }
                    }
                } else {
                    container.textView.setTextColor(resources.getColor(R.color.textSecondary, null))
                    container.textView.background = null
                }
            }
        }

        bindings.calendarView.monthScrollListener = {
            bindings.txvYear.text = it.yearMonth.year.toString()
            bindings.txvMonth.text = monthTitleFormatter.format(it.yearMonth)
            viewModel.loadMonthTasks(it.yearMonth)
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

        calendarTasksJob = lifecycleScope.launchWhenResumed {
            viewModel.tasksByMonthFlow
                .collectLatest {
                    debug<CalendarFragment>("Tasks by month received")
                    bindings.calendarView.notifyCalendarChanged()
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

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
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
        calendarTasksJob?.cancel()
        calendarTasksJob = null
    }

}