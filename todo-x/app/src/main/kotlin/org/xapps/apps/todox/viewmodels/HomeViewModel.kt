package org.xapps.apps.todox.viewmodels

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.xapps.apps.todox.core.models.Category
import org.xapps.apps.todox.core.repositories.CategoryRepository
import org.xapps.apps.todox.core.repositories.TaskRepository
import org.xapps.apps.todox.views.utils.Message
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val tasksRepository: TaskRepository
) : ViewModel() {

    private val messageEmitter: MutableLiveData<Message> = MutableLiveData()
    private val categoriesPaginatedEmitter: MutableLiveData<PagingData<Category>> = MutableLiveData()

    fun message(): LiveData<Message> = messageEmitter
    fun categoriesPaginated(): LiveData<PagingData<Category>> = categoriesPaginatedEmitter

    fun tasksImportantCount(): LiveData<Int> = tasksRepository.tasksImportantCount()
            .catch { ex ->
                messageEmitter.postValue(Message.Error(Exception(ex.localizedMessage)))
            }
            .asLiveData()

    fun tasksInScheduleCount(): LiveData<Int> = tasksRepository.tasksInScheduleCount()
            .catch {  ex ->
                messageEmitter.postValue(Message.Error(Exception(ex.localizedMessage)))
            }
            .asLiveData()

    fun tasksTodayCount(): LiveData<Int> = tasksRepository.tasksTodayCount()
            .catch { ex ->
                messageEmitter.postValue(Message.Error(Exception(ex.localizedMessage)))
            }
            .asLiveData()

    init {
        categories()
    }

    fun categories() {
        viewModelScope.launch {
            categoryRepository.categoriesPaginated().cachedIn(viewModelScope).collectLatest { data ->
                categoriesPaginatedEmitter.postValue(data)
            }
        }
    }

}