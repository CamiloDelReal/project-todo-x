package org.xapps.apps.todox.viewmodels

import android.content.Context
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.lifecycle.*
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
import org.xapps.apps.todox.core.models.Task
import org.xapps.apps.todox.core.models.TaskWithItemsAndCategory
import org.xapps.apps.todox.core.repositories.ItemRepository
import org.xapps.apps.todox.core.repositories.TaskRepository
import org.xapps.apps.todox.core.repositories.failures.ItemFailure
import org.xapps.apps.todox.core.repositories.failures.TaskFailure
import org.xapps.apps.todox.core.utils.debug
import org.xapps.apps.todox.core.utils.info
import org.xapps.apps.todox.views.utils.Message
import timber.log.Timber
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
                        _messageFlow.emit(Message.Loaded)
                    }
        }
    }

    fun updateItem(item: Item) {
        _messageFlow.tryEmit(Message.Loading)
        viewModelScope.launch {
            val result = itemRepository.update(item)
            result.either(::handleItemFailure, ::handleItemSuccess)
        }
    }

    fun updateTask() {
        _messageFlow.tryEmit(Message.Loading)
        viewModelScope.launch {
            val taskUpdated = taskWithItemsAndCategory.get()!!.task.copy()
            taskUpdated.important = !taskUpdated.important
            val result = taskRepository.update(taskUpdated)
            result.either(::handleTaskFailure, ::handleTaskSuccess)
        }
    }

    private fun handleItemFailure(failure: ItemFailure) {
        _messageFlow.tryEmit(Message.Error(Exception(context.getString(R.string.error_updating_item_in_db))))
    }

    private fun handleItemSuccess(item: Item) {
        _messageFlow.tryEmit(Message.Success())
    }

    private fun handleTaskFailure(failure: TaskFailure) {
        _messageFlow.tryEmit(Message.Error(Exception(context.getString(R.string.error_updating_task_in_db))))
    }

    private fun handleTaskSuccess(task: Task) {
        _messageFlow.tryEmit(Message.Success())
    }

    private fun handleTaskDeleteSuccess(task: Task) {
        _messageFlow.tryEmit(Message.Success(true))
    }

    fun completeTask() {
        _messageFlow.tryEmit(Message.Loading)
        viewModelScope.launch {
            val taskUpdated = taskWithItemsAndCategory.get()!!.task.copy()
            taskUpdated.done = !taskUpdated.done
            val result = taskRepository.update(taskUpdated)
            result.either(::handleTaskFailure, ::handleTaskSuccess)
        }
    }

    fun deleteTask() {
        _messageFlow.tryEmit(Message.Loading)
        viewModelScope.launch {
            taskJob?.cancel()
            taskJob = null
            val result = taskRepository.delete(taskWithItemsAndCategory.get()!!.task)
            result.either(::handleTaskFailure, ::handleTaskDeleteSuccess)
        }
    }
}
