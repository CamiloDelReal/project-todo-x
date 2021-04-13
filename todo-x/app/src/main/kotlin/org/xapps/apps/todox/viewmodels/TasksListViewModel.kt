package org.xapps.apps.todox.viewmodels

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.models.Task
import org.xapps.apps.todox.core.models.TaskWithItemsAndCategory
import org.xapps.apps.todox.core.repositories.TaskRepository
import org.xapps.apps.todox.views.utils.Message
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class TasksListViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val savedStateHandle: SavedStateHandle,
    private val taskRepository: TaskRepository
): ViewModel() {

    private val messageEmitter: MutableLiveData<Message> = MutableLiveData()
    private val tasksEmitter: MutableLiveData<PagingData<TaskWithItemsAndCategory>> = MutableLiveData()

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

    fun message(): LiveData<Message> = messageEmitter
    fun tasks(): LiveData<PagingData<TaskWithItemsAndCategory>> = tasksEmitter

    init {
        filter = savedStateHandle["filter"] ?: FilterType.ALL
        Timber.i("Filter received: $filter")
    }

    private fun tasksAll() {
        tasksJob?.cancel()
        tasksJob = viewModelScope.launch{
            taskRepository.tasksWithItemsAndCategoryPaginated().cachedIn(viewModelScope)
                .catch { ex ->
                    messageEmitter.postValue(Message.Error(Exception(ex.localizedMessage)))
                }
                .collectLatest { data ->
                    tasksEmitter.postValue(data)
                }
        }
    }

    private fun tasksInSchedule() {
        tasksJob?.cancel()
        tasksJob = viewModelScope.launch {
            taskRepository.tasksInScheduleWithItemsAndCategoryPaginated()
                .catch { ex ->
                    messageEmitter.postValue(Message.Error(Exception(ex.localizedMessage)))
                }
                .collectLatest { data ->
                    tasksEmitter.postValue(data)
                }
        }
    }

    private fun tasksImportant() {
        tasksJob?.cancel()
        tasksJob = viewModelScope.launch {
            taskRepository.tasksImportantWithItemsAndCategoryPaginated().cachedIn(viewModelScope)
                .catch { ex ->
                    messageEmitter.postValue(Message.Error(Exception(ex.localizedMessage)))
                }
                .collectLatest { data ->
                    tasksEmitter.postValue(data)
                }
        }
    }

    private fun tasksCompleted() {
        tasksJob?.cancel()
        tasksJob = viewModelScope.launch {
            taskRepository.tasksCompletedWithItemsAndCategoryPaginated().cachedIn(viewModelScope)
                .catch { ex ->
                    messageEmitter.postValue(Message.Error(Exception(ex.localizedMessage)))
                }
                .collectLatest { data ->
                    tasksEmitter.postValue(data)
                }
        }
    }

    private fun tasksToday() {
        tasksJob?.cancel()
        tasksJob = viewModelScope.launch {
            taskRepository.tasksTodayWithItemsAndCategoryPaginated().cachedIn(viewModelScope)
                .catch { ex ->
                    messageEmitter.postValue(Message.Error(Exception(ex.localizedMessage)))
                }
                .collectLatest { data ->
                    tasksEmitter.postValue(data)
                }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskRepository.update(task)
                    .catch { ex ->
                        messageEmitter.postValue(Message.Error(Exception(ex.localizedMessage)))
                    }
                    .collect { success ->
                        if(!success) {
                            messageEmitter.postValue(Message.Error(Exception(context.getString(R.string.error_updating_task_in_db))))
                        }
                    }
        }
    }

}