package org.xapps.apps.todox.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.models.Category
import org.xapps.apps.todox.core.models.Task
import org.xapps.apps.todox.core.models.TaskAndCategory
import org.xapps.apps.todox.core.models.TaskWithItemsAndCategory
import org.xapps.apps.todox.core.repositories.TaskRepository
import org.xapps.apps.todox.core.utils.debug
import org.xapps.apps.todox.core.utils.error
import org.xapps.apps.todox.views.utils.Message
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject


@HiltViewModel
class CalendarViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val taskRepository: TaskRepository
): ViewModel() {

    private val _messageFlow: MutableSharedFlow<Message> = MutableSharedFlow(replay = 1)
    private val _tasksFlow: MutableSharedFlow<PagingData<TaskWithItemsAndCategory>> = MutableSharedFlow(replay = 1)
    private val _tasksByMonthFlow: MutableSharedFlow<List<TaskAndCategory>> = MutableSharedFlow(replay = 1)

    private var _tasksByMonth: List<TaskAndCategory>? = null

    val messageFlow: SharedFlow<Message> = _messageFlow
    val tasksFlow: SharedFlow<PagingData<TaskWithItemsAndCategory>> = _tasksFlow
    val tasksByMonthFlow: SharedFlow<List<TaskAndCategory>> = _tasksByMonthFlow

    var currentYearMonth: YearMonth? = null
        private set

    var lastDateForTasks: LocalDate? = null
        private set

    private var tasksJob: Job? = null
    private var monthlyTasksJob: Job? = null


    fun tasksByDate(date: LocalDate) {
        _messageFlow.tryEmit(Message.Loading)
        tasksJob?.cancel()
        tasksJob = viewModelScope.launch {
            lastDateForTasks = date
            taskRepository.tasksInScheduleWithItemsAndCategoryByDatePaginated(date).cachedIn(viewModelScope)
                .catch { ex ->
                    _messageFlow.emit(Message.Error(Exception(ex.localizedMessage)))
                }
                .collectLatest { data ->
                    debug<CalendarViewModel>("$data")
                    _tasksFlow.emit(data)
                    _messageFlow.emit(Message.Loaded)
                }
        }
    }

    fun deleteTask(task: Task) {
        _messageFlow.tryEmit(Message.Loading)
        viewModelScope.launch {
            val result = taskRepository.delete(task)
            result.either({ failure ->
                error<CalendarViewModel>("Error received $failure")
                _messageFlow.tryEmit(Message.Error(Exception(context.getString(R.string.error_deleting_task_from_db))))
            }, {
                _messageFlow.tryEmit(Message.Success(CategoryDetailsViewModel.Operation.TASK_DELETE))
            })
        }
    }

    fun completeTask(task: Task) {
        _messageFlow.tryEmit(Message.Loading)
        viewModelScope.launch {
            val result = taskRepository.complete(task)
            result.either({ failure ->
                error<CalendarViewModel>("Error received $failure")
                _messageFlow.tryEmit(Message.Error(Exception(context.getString(R.string.error_updating_task_in_db))))
            }, {
                _messageFlow.tryEmit(Message.Success(CategoryDetailsViewModel.Operation.TASK_EDIT_DELETE))
            })
        }
    }

    fun updateTask(task: Task) {
        _messageFlow.tryEmit(Message.Loading)
        viewModelScope.launch {
            val result = taskRepository.update(task)
            result.either({ failure ->
                error<CalendarViewModel>("Error received $failure")
                _messageFlow.tryEmit(Message.Error(Exception(context.getString(R.string.error_updating_task_in_db))))
            }, {
                _messageFlow.tryEmit(Message.Loaded)
            })
        }
    }

    fun tasksCategoriesByDate(date: LocalDate): List<Category> {
        return _tasksByMonth?.filter { it.task.date == date }?.map { it.category }?.distinctBy { it.id } ?: listOf()
    }

    fun loadMonthTasks(yearMonth: YearMonth) {
        _messageFlow.tryEmit(Message.Loading)
        currentYearMonth = yearMonth
        monthlyTasksJob?.cancel()
        monthlyTasksJob = viewModelScope.launch {
            currentYearMonth?.let {
                taskRepository.tasksInScheduleAndCategoryByMonth(currentYearMonth!!)
                    .catch { ex ->
                        error<CalendarViewModel>(ex, "Exception captured")
                        _messageFlow.emit(Message.Error(Exception(ex.localizedMessage)))
                    }
                    .collect { tasks ->
                        debug<CalendarViewModel>(tasks.toString())
                        _tasksByMonth = tasks
                        _tasksByMonthFlow.emit(tasks)
                        _messageFlow.emit(Message.Loaded)
                    }
            }
        }
    }
}