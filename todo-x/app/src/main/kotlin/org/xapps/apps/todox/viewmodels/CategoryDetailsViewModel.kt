package org.xapps.apps.todox.viewmodels

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.SavedStateHandle
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
import org.xapps.apps.todox.core.models.TaskWithItems
import org.xapps.apps.todox.core.repositories.CategoryRepository
import org.xapps.apps.todox.core.repositories.TaskRepository
import org.xapps.apps.todox.core.repositories.failures.TaskFailure
import org.xapps.apps.todox.core.utils.debug
import org.xapps.apps.todox.core.utils.error
import org.xapps.apps.todox.core.utils.info
import org.xapps.apps.todox.views.utils.Message
import javax.inject.Inject


@HiltViewModel
class CategoryDetailsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val categoryRepository: CategoryRepository,
    private val taskRepository: TaskRepository
): ViewModel() {

    private var tasksJob: Job? = null
    private val _messageFlow: MutableSharedFlow<Message> = MutableSharedFlow(replay = 1)
    private val _tasksPaginatedFlow: MutableSharedFlow<PagingData<TaskWithItems>?> = MutableSharedFlow(replay = 1)

    var categoryId: Long = Constants.ID_INVALID
        private set
    val category: ObservableField<Category> = ObservableField()

    var filter: FilterType = FilterType.NONE
        set(value) {
            info<CategoryDetailsViewModel>("Task filter request $value")
            field = value
            when(value) {
                FilterType.SCHEDULED -> tasksInScheduled()
                FilterType.IMPORTANT -> tasksImportant()
                FilterType.COMPLETED -> tasksCompleted()
                else -> {}
            }
        }

    val messageFlow: SharedFlow<Message> = _messageFlow
    val tasksPaginatedFlow: SharedFlow<PagingData<TaskWithItems>?> = _tasksPaginatedFlow

    init {
        categoryId = savedStateHandle[Constants.CATEGORY_ID] ?: throw kotlin.IllegalArgumentException("Category Id not provided")
        category(categoryId)
        filter = FilterType.SCHEDULED
    }

    fun category(id: Long) {
        _messageFlow.tryEmit(Message.Loading)
        viewModelScope.launch {
            categoryRepository.category(id)
                .catch { ex ->
                    _messageFlow.emit(Message.Error(Exception(ex.localizedMessage)))
                }
                .collect { cat ->
                    category.set(cat)
                    _messageFlow.emit(Message.Success())
                }
        }
    }

    private fun tasksInScheduled() {
        tasksJob?.cancel()
        tasksJob = viewModelScope.launch {
            taskRepository.tasksInScheduleWithItemsByCategoryPaginated(categoryId).cachedIn(viewModelScope)
                .catch { ex ->
                    error<CategoryDetailsViewModel>(ex, "Exception captured")
                    _messageFlow.emit(Message.Error(Exception(ex.localizedMessage)))
                }
                .collectLatest { data ->
                    info<CategoryDetailsViewModel>("Tasks in schedule received")
                    debug<CategoryDetailsViewModel>("Data $data")
                    _tasksPaginatedFlow.emit(data)
                }
        }
    }

    private fun tasksImportant() {
        tasksJob?.cancel()
        tasksJob = viewModelScope.launch {
            taskRepository.tasksImportantWithItemsByCategoryPaginated(categoryId).cachedIn(viewModelScope)
                .catch { ex ->
                    error<CategoryDetailsViewModel>(ex, "Exception captured")
                    _messageFlow.emit(Message.Error(Exception(ex.localizedMessage)))
                }
                .collectLatest { data ->
                    info<CategoryDetailsViewModel>("Tasks important received")
                    debug<CategoryDetailsViewModel>("Data $data")
                    _tasksPaginatedFlow.emit(data)
                }
        }
    }

    private fun tasksCompleted() {
        tasksJob?.cancel()
        tasksJob = viewModelScope.launch {
            taskRepository.tasksCompletedWithItemsByCategoryPaginated(categoryId).cachedIn(viewModelScope)
                .catch { ex ->
                    error<CategoryDetailsViewModel>(ex, "Exception captured")
                    _messageFlow.emit(Message.Error(Exception(ex.localizedMessage)))
                }
                .collectLatest { data ->
                    info<CategoryDetailsViewModel>("Tasks completed received")
                    debug<CategoryDetailsViewModel>("Data $data")
                    _tasksPaginatedFlow.emit(data)
                }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            val result = taskRepository.update(task)
            result.either(::handleTaskFailure, ::handleTaskSuccess)
        }
    }

    private fun handleTaskSuccess(task: Task) {}

    private fun handleTaskFailure(failure: TaskFailure) {
        _messageFlow.tryEmit(Message.Error(Exception(context.getString(R.string.error_updating_task_in_db))))
    }

}