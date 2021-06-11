package org.xapps.apps.todox.viewmodels

import android.content.Context
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.models.Item
import org.xapps.apps.todox.core.repositories.ItemRepository
import org.xapps.apps.todox.core.utils.debug
import org.xapps.apps.todox.core.utils.error
import org.xapps.apps.todox.core.utils.info
import org.xapps.apps.todox.views.utils.Message
import javax.inject.Inject


@HiltViewModel
class TaskEditItemsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val itemRepository: ItemRepository
): ViewModel() {

    private val _messageFlow: MutableSharedFlow<Message> = MutableSharedFlow(replay = 1)

    val messageFlow: SharedFlow<Message> = _messageFlow

    var taskId: Long = Constants.ID_INVALID
        private set

    val items: ObservableArrayList<Item> = ObservableArrayList()

    enum class Operation {
        ITEMS_EMPTY,
        ITEMS_SAVED
    }

    init {
        items.add(Item())
        taskId = savedStateHandle[Constants.TASK_ID] ?: throw kotlin.IllegalArgumentException("Task Id not provided")
        info<TaskDetailsViewModel>("Task $taskId received")
        items()
    }

    fun items() {
        info<TaskEditItemsViewModel>("Requesting items of task with id = $taskId")
        _messageFlow.tryEmit(Message.Loading)
        viewModelScope.launch {
            val result = itemRepository.itemsByTask(taskId)
            result.either({ failure ->
                error<TaskEditItemsViewModel>("Error received $failure")
                _messageFlow.tryEmit(Message.Error(Exception(context.getString(R.string.error_reading_items_from_db))))
            }, { currentItems ->
                info<TaskEditItemsViewModel>("Items loaded")
                debug<TaskEditItemsViewModel>("$currentItems")
                items.clear()
                if(currentItems.isEmpty()) {
                    items.add(Item())
                } else {
                    items.addAll(currentItems)
                }
                _messageFlow.tryEmit(Message.Loaded)
            })
        }
    }

    fun saveItems() {
        _messageFlow.tryEmit(Message.Loading)
        viewModelScope.launch {
            val fixedItems = items.filter { it.description.isNotEmpty() && it.description.isNotBlank() }
            if(fixedItems.isNotEmpty()) {
                fixedItems.forEach { it.taskId = taskId }
                val result = itemRepository.insert(fixedItems)
                result.either({ failure ->
                    error<TaskEditItemsViewModel>("Error received $failure")
                    _messageFlow.tryEmit(Message.Error(Exception(context.getString(R.string.error_saving_items_in_db))))
                }, { success ->
                    _messageFlow.tryEmit(Message.Success(Operation.ITEMS_SAVED))
                })
            } else {
                _messageFlow.emit(Message.Success(Operation.ITEMS_EMPTY))
            }
        }
    }
}