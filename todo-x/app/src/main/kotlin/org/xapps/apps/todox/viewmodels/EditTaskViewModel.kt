package org.xapps.apps.todox.viewmodels

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.models.Category
import org.xapps.apps.todox.core.models.Task
import org.xapps.apps.todox.core.repositories.CategoryRepository
import org.xapps.apps.todox.core.repositories.TaskRepository
import org.xapps.apps.todox.views.utils.Message
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class EditTaskViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository
): ViewModel() {

    private val messageEmitter: MutableLiveData<Message> = MutableLiveData()

    val task: ObservableField<Task> = ObservableField()

    val categories: ObservableField<List<Category>> = ObservableField()
    val selectedCategory: ObservableField<List<Category>> = ObservableField()

    fun message(): LiveData<Message> = messageEmitter

    init {
        // Temporal, when we have the edit flow, the Task will be from DB
        task.set(Task())

        viewModelScope.launch {
            categoryRepository.categories()
                .catch { ex ->
                    messageEmitter.postValue(Message.Error(Exception(ex.localizedMessage)))
                }
                .collect {
                    Timber.i("Cat received ${it.size}")
                    categories.set(it)
                }
        }
    }

    fun insertTask() {
        messageEmitter.postValue(Message.Loading)
        viewModelScope.launch {
            task.get()?.categoryId = selectedCategory.get()?.get(0)?.id ?: -1
            Timber.i("Insert ${task.get()}  ${selectedCategory.get()?.size}")
            taskRepository.insert(task.get()!!)
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