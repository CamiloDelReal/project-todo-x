package org.xapps.apps.todox.viewmodels

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.xapps.apps.todox.core.models.Category
import org.xapps.apps.todox.core.repositories.CategoryRepository
import org.xapps.apps.todox.views.utils.Message
import javax.inject.Inject


@HiltViewModel
class CategoryDetailsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val categoryRepository: CategoryRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    private val messageEmitter: MutableLiveData<Message> = MutableLiveData()

    val category: ObservableField<Category> = ObservableField()

    fun message(): LiveData<Message> = messageEmitter

    init {
        readCategory(savedStateHandle[Constants.CATEGORY_ID] ?: throw kotlin.IllegalArgumentException("Category Id not provided"))
    }

    fun readCategory(id: Long) {
        messageEmitter.postValue(Message.Loading)
        viewModelScope.launch {
            categoryRepository.category(id)
                .catch { ex ->
                    messageEmitter.postValue(Message.Error(Exception(ex.localizedMessage)))
                }
                .collect { cat ->
                    category.set(cat)
                    messageEmitter.postValue(Message.Success())
                }
        }
    }
}