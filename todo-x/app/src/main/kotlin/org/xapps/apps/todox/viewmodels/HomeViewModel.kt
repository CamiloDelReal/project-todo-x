package org.xapps.apps.todox.viewmodels

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.xapps.apps.todox.core.models.Category
import org.xapps.apps.todox.core.repositories.CategoryRepository
import org.xapps.apps.todox.core.repositories.SettingsRepository
import org.xapps.apps.todox.core.repositories.TaskRepository
import org.xapps.apps.todox.views.utils.Message
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val tasksRepository: TaskRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _messageFlow: MutableSharedFlow<Message> = MutableSharedFlow(replay = 1)
    private val _categoriesPaginatedFlow: MutableSharedFlow<PagingData<Category>> = MutableSharedFlow(replay = 1)

    val messageFlow: SharedFlow<Message> = _messageFlow
    val categoriesPaginatedFlow: SharedFlow<PagingData<Category>> = _categoriesPaginatedFlow

    fun tasksImportantCount(): SharedFlow<Int> = tasksRepository.tasksImportantCount()
            .catch { ex ->
                _messageFlow.emit(Message.Error(Exception(ex.localizedMessage)))
            }
            .shareIn(viewModelScope, SharingStarted.Lazily, replay = 1)

    fun tasksInScheduleCount(): SharedFlow<Int> = tasksRepository.tasksInScheduleCount()
            .catch {  ex ->
                _messageFlow.emit(Message.Error(Exception(ex.localizedMessage)))
            }
            .shareIn(viewModelScope, SharingStarted.Lazily, replay = 1)

    fun tasksTodayCount(): SharedFlow<Int> = tasksRepository.tasksTodayCount()
            .catch { ex ->
                _messageFlow.emit(Message.Error(Exception(ex.localizedMessage)))
            }
            .shareIn(viewModelScope, SharingStarted.Lazily, replay = 1)

    fun isDarkModeOn(): Flow<Boolean> = settingsRepository.isDarkModeOn()

    suspend fun isDarkModeOnValue(): Boolean = settingsRepository.isDarkModeOnValue()

    init {
        categories()
    }

    fun categories() {
        viewModelScope.launch {
            categoryRepository.categoriesPaginated().cachedIn(viewModelScope).collectLatest { data ->
                _categoriesPaginatedFlow.emit(data)
            }
        }
    }

}