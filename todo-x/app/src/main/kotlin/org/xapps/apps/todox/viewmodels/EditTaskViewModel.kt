package org.xapps.apps.todox.viewmodels

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
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
class EditTaskViewModel @Inject constructor(
        @ApplicationContext private val context: Context,
        private val savedStateHandle: SavedStateHandle,
        private val taskRepository: TaskRepository,
        private val categoryRepository: CategoryRepository
): ViewModel() {

    private val messageEmitter: MutableLiveData<Message> = MutableLiveData()

    private var taskId: Long = Constants.ID_INVALID
    val taskWithItems: ObservableField<TaskWithItems> = ObservableField()
    val categories: ObservableField<List<Category>> = ObservableField()
    private var categoryId: Long = Constants.ID_INVALID
    val selectedCategory: ObservableField<List<Category>> = ObservableField()

    fun message(): LiveData<Message> = messageEmitter

    init {
        taskWithItems.set(TaskWithItems(task = Task(), items = listOf()))
        if(savedStateHandle.contains(Constants.TASK_ID)){
            taskId = savedStateHandle[Constants.TASK_ID] ?: throw kotlin.IllegalArgumentException("Task Id not provided")
            Timber.i("Edit task request for id $taskId")
            if(taskId != Constants.ID_INVALID) {
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
                    messageEmitter.postValue(Message.Error(Exception(ex.localizedMessage)))
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
        messageEmitter.postValue(Message.Loading)
        viewModelScope.launch {
            taskRepository.taskWithItems(id)
                    .catch { ex ->
                        messageEmitter.postValue(Message.Error(Exception(ex.localizedMessage)))
                    }
                    .collect {
                        Timber.i("Task with items received $it")
                        taskWithItems.set(it)
                        categoryId = it.task.categoryId
                        category(categoryId)
                        messageEmitter.postValue(Message.Success())
                    }
        }
    }

    private fun category(id: Long) {
        messageEmitter.postValue(Message.Loading)
        viewModelScope.launch {
            categoryRepository.category(id)
                    .catch { ex ->
                        messageEmitter.postValue(Message.Error(Exception(ex.localizedMessage)))
                    }
                    .collect { cat ->
                        selectedCategory.set(listOf(cat))
                        messageEmitter.postValue(Message.Success())
                    }
        }
    }

    fun insertTask() {
        messageEmitter.postValue(Message.Loading)
        viewModelScope.launch {
            taskWithItems.get()?.task?.categoryId = selectedCategory.get()?.get(0)?.id ?: -1
            taskRepository.insert(taskWithItems.get()?.task!!)
                .catch { ex ->
                    messageEmitter.postValue(Message.Error(Exception(ex.localizedMessage)))
                }
                .collect {
                    if(it.id != -1L) {
                        // Save items
                        messageEmitter.postValue(Message.Success())
                    } else {
                        messageEmitter.postValue(Message.Error(Exception(context.getString(R.string.error_inserting_task_in_db))))
                    }
                }
        }
    }

}