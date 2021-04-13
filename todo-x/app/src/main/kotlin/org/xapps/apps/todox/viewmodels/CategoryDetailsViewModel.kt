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
import timber.log.Timber
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
    private val tasksPaginatedEmitter: MutableLiveData<PagingData<TaskWithItems>?> = MutableLiveData(null)

    var categoryId: Long = -1
        private set
    val category: ObservableField<Category> = ObservableField()

    var filter: FilterType = FilterType.NONE
        set(value) {
            field = value
            when(value) {
                FilterType.SCHEDULED -> tasksInScheduled()
                FilterType.IMPORTANT -> tasksImportant()
                FilterType.COMPLETED -> tasksCompleted()
                else -> {}
            }
        }

    fun message(): LiveData<Message> = messageEmitter
    fun tasksPaginated(): LiveData<PagingData<TaskWithItems>?> = tasksPaginatedEmitter

    init {
        categoryId = savedStateHandle[Constants.CATEGORY_ID] ?: throw kotlin.IllegalArgumentException("Category Id not provided")
        category(categoryId)
        filter = FilterType.SCHEDULED
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

    private fun tasksInScheduled() {
        tasksJob?.cancel()
        tasksJob = viewModelScope.launch {
            taskRepository.tasksInScheduleWithItemsByCategoryPaginated(categoryId).cachedIn(viewModelScope)
                .catch { ex ->
                    messageEmitter.postValue(Message.Error(Exception(ex.localizedMessage)))
                }
                .collectLatest { data ->
                    Timber.i("Tasks in schedule received")
                    tasksPaginatedEmitter.postValue(data)
                }
        }
    }

    private fun tasksImportant() {
        tasksJob?.cancel()
        tasksJob = viewModelScope.launch {
            taskRepository.tasksImportantWithItemsByCategoryPaginated(categoryId).cachedIn(viewModelScope)
                .catch { ex ->
                    messageEmitter.postValue(Message.Error(Exception(ex.localizedMessage)))
                }
                .collectLatest { data ->
                    tasksPaginatedEmitter.postValue(data)
                }
        }
    }

    private fun tasksCompleted() {
        tasksJob?.cancel()
        tasksJob = viewModelScope.launch {
            taskRepository.tasksCompletedWithItemsByCategoryPaginated(categoryId).cachedIn(viewModelScope)
                .catch { ex ->
                    messageEmitter.postValue(Message.Error(Exception(ex.localizedMessage)))
                }
                .collectLatest { data ->
                    tasksPaginatedEmitter.postValue(data)
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