package org.xapps.apps.todox.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val messageEmitter: MutableLiveData<Message> = MutableLiveData()
    private val categoriesPaginatedEmitter: MutableLiveData<PagingData<Category>> = MutableLiveData()

    fun message(): LiveData<Message> = messageEmitter
    fun categoriesPaginated(): LiveData<PagingData<Category>> = categoriesPaginatedEmitter

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