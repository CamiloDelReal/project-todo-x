package org.xapps.apps.todox.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.xapps.apps.todox.core.models.Category
import org.xapps.apps.todox.core.repositories.CategoryRepository
import org.xapps.apps.todox.views.utils.Message
import javax.inject.Inject


@HiltViewModel
class CategoriesListViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
): ViewModel(){

    private val _messageFlow: MutableSharedFlow<Message> = MutableSharedFlow(replay = 1)
    private val _categoriesPaginatedFlow: MutableSharedFlow<PagingData<Category>> = MutableSharedFlow(replay = 1)

    val messageFlow: SharedFlow<Message> = _messageFlow
    val categoriesPaginatedFlow: SharedFlow<PagingData<Category>> = _categoriesPaginatedFlow

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