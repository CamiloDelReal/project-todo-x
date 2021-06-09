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
import org.xapps.apps.todox.core.repositories.failures.CategoryFailure
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

    private var categoryJob: Job? = null
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
        categoryJob?.cancel()
        categoryJob = viewModelScope.launch {
            categoryRepository.category(id)
                .catch { ex ->
                    _messageFlow.emit(Message.Error(Exception(ex.localizedMessage)))
                }
                .collect { cat ->
                    category.set(cat)
                    _messageFlow.emit(Message.Loaded)
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
        _messageFlow.tryEmit(Message.Loading)
        viewModelScope.launch {
            val result = taskRepository.update(task)
            result.either({ failure ->
                _messageFlow.tryEmit(Message.Error(Exception(context.getString(R.string.error_updating_task_in_db))))
            }, { task ->
                _messageFlow.tryEmit(Message.Success(Operation.TASK_EDIT_DELETE))
            })
        }
    }

    fun deleteAllTasks() {
        _messageFlow.tryEmit(Message.Loading)
        viewModelScope.launch {
            val result = taskRepository.deleteTasksInCategory(categoryId)
            result.either({ failure ->
                _messageFlow.tryEmit(Message.Error(Exception(context.getString(R.string.error_deleting_tasks_from_db))))
            }, { success ->
                _messageFlow.tryEmit(Message.Success(Operation.TASK_EDIT_DELETE))
            })
        }
    }

    fun completeAllTasks() {
        _messageFlow.tryEmit(Message.Loading)
        viewModelScope.launch {
            val result = taskRepository.completeTasksInCategory(categoryId)
            result.either({ failure ->
                _messageFlow.tryEmit(Message.Error(Exception(context.getString(R.string.error_updating_task_in_db))))
            }, { success ->
                _messageFlow.tryEmit(Message.Success(Operation.TASK_EDIT_DELETE))
            })
        }
    }

    fun canCategoryBeDeleted(): Boolean {
        debug<CategoryDetailsViewModel>("Can category $categoryId be deleted when ${Constants.UNCLASSIFED_CATEGORY_ID} is blocked?")
        return categoryId != Constants.UNCLASSIFED_CATEGORY_ID
    }

    fun deleteCategory(deleteTasks: Boolean) {
        debug<CategoryDetailsViewModel>("Requesting delete category")
        _messageFlow.tryEmit(Message.Loading)
        viewModelScope.launch {
            categoryJob?.cancel()
            categoryJob = null
            tasksJob?.cancel()
            tasksJob = null
            val result = categoryRepository.delete(categoryId, deleteTasks = deleteTasks)
            result.either({ failure ->
                _messageFlow.tryEmit(Message.Error(Exception(context.getString(R.string.error_deleting_category_from_db))))
            }, { success ->
                debug<CategoryDetailsViewModel>("handleCategorySuccess $success")
                _messageFlow.tryEmit(Message.Success(Operation.CATEGORY_DELETE))
            })
        }
    }

    fun deleteTask(task: Task) {
        _messageFlow.tryEmit(Message.Loading)
        viewModelScope.launch {
            val result = taskRepository.delete(task)
            result.either({ failure ->
                _messageFlow.tryEmit(Message.Error(Exception(context.getString(R.string.error_deleting_task_from_db))))
            }, { success ->
                _messageFlow.tryEmit(Message.Success(Operation.TASK_DELETE))
            })
        }
    }

    fun completeTask(task: Task) {
        _messageFlow.tryEmit(Message.Loading)
        viewModelScope.launch {
            val result = taskRepository.complete(task)
            result.either({ failure ->
                _messageFlow.tryEmit(Message.Error(Exception(context.getString(R.string.error_updating_task_in_db))))
            }, { success ->
                _messageFlow.tryEmit(Message.Success(Operation.TASK_EDIT_DELETE))
            })
        }
    }

    enum class Operation {
        CATEGORY_DELETE,
        TASK_EDIT_DELETE,
        TASK_DELETE
    }

}