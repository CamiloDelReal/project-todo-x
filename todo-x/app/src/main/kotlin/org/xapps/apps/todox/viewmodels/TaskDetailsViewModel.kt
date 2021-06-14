package org.xapps.apps.todox.viewmodels

import android.content.Context
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.models.Item
import org.xapps.apps.todox.core.models.TaskWithItemsAndCategory
import org.xapps.apps.todox.core.repositories.ItemRepository
import org.xapps.apps.todox.core.repositories.TaskRepository
import org.xapps.apps.todox.core.utils.debug
import org.xapps.apps.todox.core.utils.error
import org.xapps.apps.todox.core.utils.info
import org.xapps.apps.todox.views.utils.Message
import javax.inject.Inject


@HiltViewModel
class TaskDetailsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val taskRepository: TaskRepository,
    private val itemRepository: ItemRepository
): ViewModel() {

    private val _messageFlow: MutableSharedFlow<Message> = MutableSharedFlow(replay = 1)

    val messageFlow: SharedFlow<Message> = _messageFlow

    var taskId: Long = Constants.ID_INVALID
        private set
    val taskWithItemsAndCategory: ObservableField<TaskWithItemsAndCategory> = ObservableField()

    val items: ObservableArrayList<Item> = ObservableArrayList()
    private var itemUpdateRequested = false

    private var taskJob: Job? = null

    init {
        items.add(Item())
        taskId = savedStateHandle[Constants.TASK_ID] ?: throw kotlin.IllegalArgumentException("Task Id not provided")
        info<TaskDetailsViewModel>("Task $taskId received")
        task(taskId)
    }

    fun task(id: Long) {
        info<TaskDetailsViewModel>("Requesting task with id = $id")
        _messageFlow.tryEmit(Message.Loading)
        taskJob?.cancel()
        taskJob = viewModelScope.launch {
            taskRepository.taskWithItemsAndCategory(id)
                    .catch { ex ->
                        _messageFlow.emit(Message.Error(Exception(ex.localizedMessage)))
                    }
                    .collect { task ->
                        info<TaskDetailsViewModel>("Task loaded")
                        debug<TaskDetailsViewModel>("$task")
                        taskWithItemsAndCategory.set(task)
                        items.clear()
                        items.addAll(task.items)
                        if(itemUpdateRequested) {
                            itemUpdateRequested = false
                            val doneCount = task.items.count { it.done }
                            if(doneCount == task.items.size) {
                                _messageFlow.emit(Message.Success(Operation.TASK_COMPLETED))
                                completeTask()
                            } else if(task.task.done) {
                                _messageFlow.emit(Message.Success(Operation.TASK_UNCOMPLETED))
                                uncompleteTask()
                            }
                        }
                        _messageFlow.emit(Message.Loaded)
                    }
        }
    }

    fun updateItem(item: Item) {
        _messageFlow.tryEmit(Message.Loading)
        viewModelScope.launch {
            itemUpdateRequested = true
            val result = itemRepository.update(item)
            result.either({ failure ->
                error<TaskDetailsViewModel>("Error recieved $failure")
                itemUpdateRequested = false
                _messageFlow.tryEmit(Message.Error(Exception(context.getString(R.string.error_updating_item_in_db))))
            }, {
                _messageFlow.tryEmit(Message.Success(Operation.ITEM_EDIT))
            })
        }
    }

    fun changePriority() {
        _messageFlow.tryEmit(Message.Loading)
        viewModelScope.launch {
            val taskUpdated = taskWithItemsAndCategory.get()!!.task.copy()
            taskUpdated.important = !taskUpdated.important
            val result = taskRepository.update(taskUpdated)
            result.either({ failure ->
                error<TaskDetailsViewModel>("Error received $failure")
                _messageFlow.tryEmit(Message.Error(Exception(context.getString(R.string.error_updating_task_in_db))))
            }, {
                _messageFlow.tryEmit(Message.Success(Operation.TASK_EDIT))
            })
        }
    }

    fun completeTask() {
        _messageFlow.tryEmit(Message.Loading)
        viewModelScope.launch {
            val taskUpdated = taskWithItemsAndCategory.get()!!.task.copy()
            val result = taskRepository.complete(taskUpdated)
            result.either({ failure ->
                error<TaskDetailsViewModel>("Error received $failure")
                _messageFlow.tryEmit(Message.Error(Exception(context.getString(R.string.error_updating_task_in_db))))
            }, {
                _messageFlow.tryEmit(Message.Success(Operation.TASK_EDIT))
            })
        }
    }

    fun uncompleteTask() {
        _messageFlow.tryEmit(Message.Loading)
        viewModelScope.launch {
            val taskUpdated = taskWithItemsAndCategory.get()!!.task.copy()
            val result = taskRepository.uncomplete(taskUpdated)
            result.either({ failure ->
                error<TaskDetailsViewModel>("Error received $failure")
                _messageFlow.tryEmit(Message.Error(Exception(context.getString(R.string.error_updating_task_in_db))))
            }, {
                _messageFlow.tryEmit(Message.Success(Operation.TASK_EDIT))
            })
        }
    }

    fun deleteTask() {
        _messageFlow.tryEmit(Message.Loading)
        viewModelScope.launch {
            taskJob?.cancel()
            taskJob = null
            val result = taskRepository.delete(taskWithItemsAndCategory.get()!!.task)
            result.either({ failure ->
                error<TaskDetailsViewModel>("Error received $failure")
                _messageFlow.tryEmit(Message.Error(Exception(context.getString(R.string.error_updating_task_in_db))))
            }, {
                _messageFlow.tryEmit(Message.Success(Operation.TASK_DELETE))
            })
        }
    }

    enum class Operation {
        ITEM_EDIT,
        TASK_EDIT,
        TASK_DELETE,
        TASK_COMPLETED,
        TASK_UNCOMPLETED
    }
}
