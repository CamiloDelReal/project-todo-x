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
import org.xapps.apps.todox.core.models.Category
import org.xapps.apps.todox.core.models.Task
import org.xapps.apps.todox.core.models.TaskWithItems
import org.xapps.apps.todox.core.repositories.CategoryRepository
import org.xapps.apps.todox.core.repositories.TaskRepository
import org.xapps.apps.todox.views.utils.Message
import javax.inject.Inject


@HiltViewModel
class CategoryDetailsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val savedStateHandle: SavedStateHandle,
    private val categoryRepository: CategoryRepository,
    private val taskRepository: TaskRepository
): ViewModel() {

    private var tasksJob: Job? = null
    private val messageEmitter: MutableLiveData<Message> = MutableLiveData()
    private val tasksEmitter: MutableLiveData<PagingData<TaskWithItems>> = MutableLiveData()

    var categoryId: Long = -1
        private set
    val category: ObservableField<Category> = ObservableField()

    fun message(): LiveData<Message> = messageEmitter
    fun tasks(): LiveData<PagingData<TaskWithItems>> = tasksEmitter

    var filter: FilterType = FilterType.SCHEDULED
        private set

    enum class FilterType {
        SCHEDULED, IMPORTANT, COMPLETED
    }

    init {
        categoryId = savedStateHandle[Constants.CATEGORY_ID] ?: throw kotlin.IllegalArgumentException("Category Id not provided")
        category(categoryId)
    }

    fun category(id: Long) {
        messageEmitter.postValue(Message.Loading)
        viewModelScope.launch {
            categoryRepository.category(id)
                .catch { ex ->
                    messageEmitter.postValue(Message.Error(Exception(ex.localizedMessage)))
                }
                .collect { cat ->
                    category.set(cat)
                    messageEmitter.postValue(Message.Success())
                }
        }
    }

    fun tasksScheduled() {
        tasksJob?.cancel()
        tasksJob = viewModelScope.launch {
            filter = FilterType.SCHEDULED
            taskRepository.tasksScheduledPaginated(categoryId).cachedIn(viewModelScope)
                .catch { ex ->
                    messageEmitter.postValue(Message.Error(Exception(ex.localizedMessage)))
                }
                .collectLatest { data ->
                    tasksEmitter.postValue(data)
                }
        }
    }

    fun tasksImportant() {
        tasksJob?.cancel()
        tasksJob = viewModelScope.launch {
            filter = FilterType.IMPORTANT
            taskRepository.tasksImportantPaginated(categoryId).cachedIn(viewModelScope)
                .catch { ex ->
                    messageEmitter.postValue(Message.Error(Exception(ex.localizedMessage)))
                }
                .collectLatest { data ->
                    tasksEmitter.postValue(data)
                }
        }
    }

    fun tasksCompleted() {
        tasksJob?.cancel()
        tasksJob = viewModelScope.launch {
            filter = FilterType.COMPLETED
            taskRepository.tasksCompletedPaginated(categoryId).cachedIn(viewModelScope)
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