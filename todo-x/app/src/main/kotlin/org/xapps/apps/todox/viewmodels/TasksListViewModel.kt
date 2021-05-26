package org.xapps.apps.todox.viewmodels

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.models.Task
import org.xapps.apps.todox.core.models.TaskWithItems
import org.xapps.apps.todox.core.models.TaskWithItemsAndCategory
import org.xapps.apps.todox.core.repositories.TaskRepository
import org.xapps.apps.todox.core.repositories.failures.TaskFailure
import org.xapps.apps.todox.views.utils.Message
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class TasksListViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val savedStateHandle: SavedStateHandle,
    private val taskRepository: TaskRepository
): ViewModel() {

    private val _messageFlow: MutableSharedFlow<Message> = MutableSharedFlow(replay = 1)
    private val _tasksFlow: MutableSharedFlow<PagingData<TaskWithItemsAndCategory>> = MutableSharedFlow(replay = 1)

    var filter: FilterType = FilterType.ALL
        set(value) {
            Timber.i("Set filter with value $value")
            field = value
            when(value) {
                FilterType.ALL -> {
                    filterTextValue.set(context.getString(R.string.tasks))
                    tasksAll()
                }
                FilterType.SCHEDULED -> {
                    filterTextValue.set(context.getString(R.string.in_schedule))
                    tasksInSchedule()
                }
                FilterType.IMPORTANT -> {
                    filterTextValue.set(context.getString(R.string.important))
                    tasksImportant()
                }
                FilterType.COMPLETED -> {
                    filterTextValue.set(context.getString(R.string.completed))
                    tasksCompleted()
                }
                FilterType.TODAY -> {
                    filterTextValue.set(context.getString(R.string.today))
                    tasksToday()
                }
                else -> {
                    filterTextValue.set(context.getString(R.string.tasks))
                    tasksAll()
                }
            }
        }

    var filterTextValue: ObservableField<String> = ObservableField(context.getString(R.string.tasks))
        private set

    private var tasksJob: Job? = null

    val messageFlow: SharedFlow<Message> = _messageFlow
    val tasksFlow: SharedFlow<PagingData<TaskWithItemsAndCategory>> = _tasksFlow

    init {
        filter = savedStateHandle["filter"] ?: FilterType.ALL
        Timber.i("Filter received: $filter")
    }

    private fun tasksAll() {
        tasksJob?.cancel()
        tasksJob = viewModelScope.launch{
            taskRepository.tasksWithItemsAndCategoryPaginated().cachedIn(viewModelScope)
                .catch { ex ->
                    _messageFlow.emit(Message.Error(Exception(ex.localizedMessage)))
                }
                .collectLatest { data ->
                    _tasksFlow.emit(data)
                }
        }
    }

    private fun tasksInSchedule() {
        tasksJob?.cancel()
        tasksJob = viewModelScope.launch {
            taskRepository.tasksInScheduleWithItemsAndCategoryPaginated().cachedIn(viewModelScope)
                .catch { ex ->
                    _messageFlow.emit(Message.Error(Exception(ex.localizedMessage)))
                }
                .collectLatest { data ->
                    _tasksFlow.emit(data)
                }
        }
    }

    private fun tasksImportant() {
        tasksJob?.cancel()
        tasksJob = viewModelScope.launch {
            taskRepository.tasksImportantWithItemsAndCategoryPaginated().cachedIn(viewModelScope)
                .catch { ex ->
                    _messageFlow.emit(Message.Error(Exception(ex.localizedMessage)))
                }
                .collectLatest { data ->
                    _tasksFlow.emit(data)
                }
        }
    }

    private fun tasksCompleted() {
        tasksJob?.cancel()
        tasksJob = viewModelScope.launch {
            taskRepository.tasksCompletedWithItemsAndCategoryPaginated().cachedIn(viewModelScope)
                .catch { ex ->
                    _messageFlow.emit(Message.Error(Exception(ex.localizedMessage)))
                }
                .collectLatest { data ->
                    _tasksFlow.emit(data)
                }
        }
    }

    private fun tasksToday() {
        tasksJob?.cancel()
        tasksJob = viewModelScope.launch {
            taskRepository.tasksTodayWithItemsAndCategoryPaginated().cachedIn(viewModelScope)
                .catch { ex ->
                    _messageFlow.emit(Message.Error(Exception(ex.localizedMessage)))
                }
                .collectLatest { data ->
                    _tasksFlow.emit(data)
                }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            val result = taskRepository.update(task)
            result.either(::handleTaskFailure, ::handleTaskSuccess)
        }
    }

    private fun handleTaskSuccess(task: Task) {
    }

    private fun handleTaskFailure(failure: TaskFailure) {
        _messageFlow.tryEmit(Message.Error(Exception(context.getString(R.string.error_updating_task_in_db))))
    }

}