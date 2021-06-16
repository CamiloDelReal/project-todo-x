package org.xapps.apps.todox.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.models.Category
import org.xapps.apps.todox.core.repositories.CategoryRepository
import org.xapps.apps.todox.core.utils.debug
import org.xapps.apps.todox.core.utils.error
import org.xapps.apps.todox.views.utils.Message
import javax.inject.Inject


@HiltViewModel
class CategoriesListViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val categoryRepository: CategoryRepository
): ViewModel(){

    private val _messageFlow: MutableSharedFlow<Message> = MutableSharedFlow(replay = 1)
    private val _categoriesPaginatedFlow: MutableSharedFlow<PagingData<Category>> = MutableSharedFlow(replay = 1)

    val messageFlow: SharedFlow<Message> = _messageFlow
    val categoriesPaginatedFlow: SharedFlow<PagingData<Category>> = _categoriesPaginatedFlow

    enum class Operation {
        CATEGORY_DELETE
    }

    init {
        categories()
    }

    fun categories() {
        _messageFlow.tryEmit(Message.Loading)
        viewModelScope.launch {
            categoryRepository.categoriesPaginated().cachedIn(viewModelScope).collectLatest { data ->
                _categoriesPaginatedFlow.emit(data)
                _messageFlow.emit(Message.Loaded)
            }
        }
    }

    fun canCategoryBeDeleted(categoryId: Long): Boolean {
        debug<CategoryDetailsViewModel>("Can category $categoryId be deleted when ${Constants.UNCLASSIFED_CATEGORY_ID} is blocked?")
        return categoryId != Constants.UNCLASSIFED_CATEGORY_ID
    }

    fun deleteCategory(categoryId: Long, deleteTasks: Boolean) {
        debug<CategoryDetailsViewModel>("Requesting delete category")
        _messageFlow.tryEmit(Message.Loading)
        viewModelScope.launch {
            val result = categoryRepository.delete(categoryId, deleteTasks = deleteTasks)
            result.either({ failure ->
                error<CategoriesListViewModel>("Error received $failure")
                _messageFlow.tryEmit(Message.Error(Exception(context.getString(R.string.error_deleting_category_from_db))))
            }, { success ->
                debug<CategoriesListViewModel>("handleCategorySuccess $success")
                _messageFlow.tryEmit(Message.Success(Operation.CATEGORY_DELETE))
            })
        }
    }

}