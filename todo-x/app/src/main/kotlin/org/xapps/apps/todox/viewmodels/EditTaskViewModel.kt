package org.xapps.apps.todox.viewmodels

import android.content.Context
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.models.Category
import org.xapps.apps.todox.core.models.Item
import org.xapps.apps.todox.core.models.Task
import org.xapps.apps.todox.core.models.TaskWithItems
import org.xapps.apps.todox.core.repositories.CategoryRepository
import org.xapps.apps.todox.core.repositories.TaskRepository
import org.xapps.apps.todox.core.utils.error
import org.xapps.apps.todox.views.utils.Message
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class EditTaskViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val savedStateHandle: SavedStateHandle,
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository
): ViewModel() {

    private val _messageFlow: MutableSharedFlow<Message> = MutableSharedFlow(replay = 1)

    private var taskId: Long = Constants.ID_INVALID
    val taskWithItems: ObservableField<TaskWithItems> = ObservableField()
    val categories: ObservableField<List<Category>> = ObservableField()
    private var categoryId: Long = Constants.ID_INVALID
    val selectedCategory: ObservableField<List<Category>> = ObservableField()

    val items: ObservableArrayList<Item> = ObservableArrayList()

    val messageFlow: SharedFlow<Message> = _messageFlow

    val hasTaskToEdit: ObservableField<Boolean> = ObservableField(false)

    init {
        taskWithItems.set(TaskWithItems(task = Task(), items = listOf()))
        items.add(Item())
        if(savedStateHandle.contains(Constants.TASK_ID)){
            taskId = savedStateHandle[Constants.TASK_ID] ?: throw kotlin.IllegalArgumentException("Task Id not provided")
            Timber.i("Edit task request for id $taskId")
            if(taskId != Constants.ID_INVALID) {
                hasTaskToEdit.set(true)
                taskWithItems(taskId)
            } else {
                categoryId = savedStateHandle[Constants.CATEGORY_ID] ?: Constants.UNCLASSIFED_CATEGORY_ID
                if(categoryId == Constants.ID_INVALID) {
                    categoryId = Constants.UNCLASSIFED_CATEGORY_ID
                }
            }
        } else {
            categoryId = savedStateHandle[Constants.CATEGORY_ID] ?: Constants.UNCLASSIFED_CATEGORY_ID
        }

        viewModelScope.launch {
            categoryRepository.categories()
                .catch { ex ->
                    _messageFlow.emit(Message.Error(Exception(ex.localizedMessage)))
                }
                .collect {
                    categories.set(it)
                    it.find { it.id == categoryId }?.let{ category ->
                        selectedCategory.set(listOf(category))
                    }
                }
        }
    }

    private fun taskWithItems(id: Long) {
        _messageFlow.tryEmit(Message.Loading)
        viewModelScope.launch {
            taskRepository.taskWithItems(id)
                .catch { ex ->
                    _messageFlow.emit(Message.Error(Exception(ex.localizedMessage)))
                }
                .collect {
                    Timber.i("Task with items received $it")
                    taskWithItems.set(it)
                    items.clear()
                    if(it.items.isEmpty()) {
                        items.add(Item())
                    } else {
                        items.addAll(it.items)
                    }
                    categoryId = it.task.categoryId
                    category(categoryId)
                }
        }
    }

    private fun category(id: Long) {
        _messageFlow.tryEmit(Message.Loading)
        viewModelScope.launch {
            categoryRepository.category(id)
                .catch { ex ->
                    _messageFlow.emit(Message.Error(Exception(ex.localizedMessage)))
                }
                .collect { cat ->
                    selectedCategory.set(listOf(cat))
                    _messageFlow.emit(Message.Loaded)
                }
        }
    }

    fun saveTask() {
        _messageFlow.tryEmit(Message.Loading)
        viewModelScope.launch {
            taskWithItems.get()?.task?.categoryId = selectedCategory.get()?.get(0)?.id ?: Constants.ID_INVALID
            val fixedItems = items.filter { it.description.isNotEmpty() && it.description.isNotBlank() }
            taskWithItems.get()!!.items = fixedItems
            if(taskId == Constants.ID_INVALID) {
                val result = taskRepository.insertWithItems(taskWithItems.get()!!)
                result.either({ failure ->
                    error<EditTaskViewModel>("Error received $failure")
                    _messageFlow.tryEmit(Message.Error(Exception(context.getString(R.string.error_inserting_task_in_db))))
                }, { taskWithItems ->
                    _messageFlow.tryEmit(Message.Success())
                })
            } else {
                val result = taskRepository.updateWithItems(taskWithItems.get()!!)
                result.either({ failure ->
                    error<EditTaskViewModel>("Error received $failure")
                    _messageFlow.tryEmit(Message.Error(Exception(context.getString(R.string.error_updating_task_in_db))))
                }, { taskWithItems ->
                    _messageFlow.tryEmit(Message.Success())
                })
            }
        }
    }

}