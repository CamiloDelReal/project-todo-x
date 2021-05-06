package org.xapps.apps.todox.viewmodels

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.xapps.apps.todox.core.models.TaskWithItemsAndCategory
import org.xapps.apps.todox.core.repositories.TaskRepository
import org.xapps.apps.todox.views.utils.Message
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class TaskDetailsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val taskRepository: TaskRepository
): ViewModel() {

    private val messageEmitter: MutableLiveData<Message> = MutableLiveData()

    fun message(): LiveData<Message> = messageEmitter

    var taskId: Long = Constants.ID_INVALID
        private set
    val taskWithItemsAndCategory: ObservableField<TaskWithItemsAndCategory> = ObservableField()

    init {
        taskId = savedStateHandle[Constants.TASK_ID] ?: throw kotlin.IllegalArgumentException("Task Id not provided")
        Timber.i("Task $taskId received")
        task(taskId)
    }

    fun task(id: Long) {
        messageEmitter.postValue(Message.Loading)
        viewModelScope.launch {
            taskRepository.taskWithItemsAndCategory(id)
                    .catch { ex ->
                        messageEmitter.postValue(Message.Error(Exception(ex.localizedMessage)))
                    }
                    .collect { task ->
                        Timber.i("Data loaded $task")
                        taskWithItemsAndCategory.set(task)
                        messageEmitter.postValue(Message.Success())
                    }
        }
    }
}