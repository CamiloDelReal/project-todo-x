package org.xapps.apps.todox.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.models.Task
import org.xapps.apps.todox.core.models.TaskWithItemsAndCategory
import org.xapps.apps.todox.core.repositories.TaskRepository
import org.xapps.apps.todox.core.utils.debug
import org.xapps.apps.todox.core.utils.error
import org.xapps.apps.todox.views.utils.Message
import java.time.LocalDate
import javax.inject.Inject


@HiltViewModel
class CalendarViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val taskRepository: TaskRepository
): ViewModel() {

    private val _messageFlow: MutableSharedFlow<Message> = MutableSharedFlow(replay = 1)
    private val _tasksFlow: MutableSharedFlow<PagingData<TaskWithItemsAndCategory>> = MutableSharedFlow(replay = 1)

    val messageFlow: SharedFlow<Message> = _messageFlow
    val tasksFlow: SharedFlow<PagingData<TaskWithItemsAndCategory>> = _tasksFlow

    var lastDateForTasks: LocalDate? = null
        private set

    private var tasksJob: Job? = null


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
}