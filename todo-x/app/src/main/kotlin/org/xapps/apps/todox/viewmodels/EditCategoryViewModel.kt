package org.xapps.apps.todox.viewmodels

import android.content.Context
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.models.Category
import org.xapps.apps.todox.core.models.Color
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

    val colors: ObservableArrayList<Color> = ObservableArrayList()
    val chosenColor: ObservableField<String> = ObservableField()

    fun message(): LiveData<Message> = messageEmitter

    init {
        categoryId = savedStateHandle[Constants.CATEGORY_ID] ?: Constants.ID_INVALID
        Timber.i("Category id received $categoryId")
        if(categoryId == Constants.ID_INVALID) {
            val defaultColor = "#${Integer.toHexString(context.resources.getColor(R.color.concrete, null))}"
            category.set(Category(color = defaultColor))
            chosenColor.set(defaultColor)
        } else {
            category(categoryId)
        }

        colors.addAll(listOf(
            Color("#${Integer.toHexString(context.resources.getColor(R.color.turquoise, null))}"),
            Color("#${Integer.toHexString(context.resources.getColor(R.color.emerald, null))}"),
            Color("#${Integer.toHexString(context.resources.getColor(R.color.peterriver, null))}"),
            Color("#${Integer.toHexString(context.resources.getColor(R.color.amethyst, null))}"),
            Color("#${Integer.toHexString(context.resources.getColor(R.color.wetasphalt, null))}"),
            Color("#${Integer.toHexString(context.resources.getColor(R.color.greensea, null))}"),
            Color("#${Integer.toHexString(context.resources.getColor(R.color.nephritis, null))}"),
            Color("#${Integer.toHexString(context.resources.getColor(R.color.belizehole, null))}"),
            Color("#${Integer.toHexString(context.resources.getColor(R.color.wisteria, null))}"),
            Color("#${Integer.toHexString(context.resources.getColor(R.color.midnightblue, null))}"),
            Color("#${Integer.toHexString(context.resources.getColor(R.color.sunflower, null))}"),
            Color("#${Integer.toHexString(context.resources.getColor(R.color.carrot, null))}"),
            Color("#${Integer.toHexString(context.resources.getColor(R.color.alizarin, null))}"),
            Color("#${Integer.toHexString(context.resources.getColor(R.color.clouds, null))}"),
            Color("#${Integer.toHexString(context.resources.getColor(R.color.concrete, null))}"),
            Color("#${Integer.toHexString(context.resources.getColor(R.color.orange, null))}"),
            Color("#${Integer.toHexString(context.resources.getColor(R.color.pumpkin, null))}"),
            Color("#${Integer.toHexString(context.resources.getColor(R.color.pomegranate, null))}"),
            Color("#${Integer.toHexString(context.resources.getColor(R.color.silver, null))}"),
            Color("#${Integer.toHexString(context.resources.getColor(R.color.asbestos, null))}")
        ))
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
                    chosenColor.set(cat.color)
                    messageEmitter.value = Message.Success(null)
                }
        }
    }

    fun saveCategory() {
        Timber.i("Category saved ${category.get()}")
        messageEmitter.postValue(Message.Loading)
        viewModelScope.launch {
            if(categoryId == Constants.ID_INVALID) {
                categoryRepository.insertCategory(category.get()!!)
                        .catch { ex ->
                            Timber.e(ex)
                            messageEmitter.postValue(Message.Error(Exception(ex.localizedMessage)))
                        }
                        .collect { success ->
                            val message = if(success) {
                                Timber.e("Category saved in db")
                                Message.Success(true)
                            } else {
                                Timber.e("Error inserting category in db")
                                Message.Error(Exception(context.getString(R.string.error_inserting_category_in_db)))
                            }
                            messageEmitter.postValue(message)
                        }
            } else {
                categoryRepository.updateCategory(category.get()!!)
                        .catch { ex ->
                            Timber.e(ex)
                            messageEmitter.postValue(Message.Error(Exception(ex.localizedMessage)))
                        }
                        .collect { success ->
                            val message = if(success) {
                                Timber.e("Category updated in db")
                                Message.Success(true)
                            } else {
                                Timber.e("Error updating category in db")
                                Message.Error(Exception(context.getString(R.string.error_updating_category_in_db)))
                            }
                            messageEmitter.postValue(message)
                        }
            }
        }
    }

    fun setColor(colorHex: String) {
        category.get()?.color = colorHex
        category.notifyChange()
        chosenColor.set(colorHex)
    }

}