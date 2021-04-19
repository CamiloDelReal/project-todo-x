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
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class EditCategoryViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val messageEmitter: MutableLiveData<Message> = MutableLiveData()
    val category: ObservableField<Category> = ObservableField()

    var categoryId: Long = Constants.ID_INVALID
        private set

    fun message(): LiveData<Message> = messageEmitter

    init {
        categoryId = savedStateHandle[Constants.CATEGORY_ID] ?: Constants.ID_INVALID
        Timber.i("Category id received $categoryId")
        if(categoryId == Constants.ID_INVALID) {
            category.set(Category(name = "Camlo"))
        } else {
            category(categoryId)
        }
    }

    fun category(id: Long) {
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

    fun saveCategory() {
        Timber.i("Category saved ${category.get()}")
    }

    fun setColor(colorHex: String) {
        category.get()?.color = colorHex
        category.notifyChange()
    }

}