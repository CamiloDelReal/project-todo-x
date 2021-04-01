package org.xapps.apps.todox.viewmodels

import android.content.Context
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
import org.xapps.apps.todox.core.repositories.CategoryRepository
import org.xapps.apps.todox.core.settings.SettingsService
import org.xapps.apps.todox.views.utils.Message
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsService: SettingsService,
    private val categoryRepository: CategoryRepository
): ViewModel() {

    private val messageEmitter: MutableLiveData<Message> = MutableLiveData()

    fun message(): LiveData<Message> = messageEmitter

    fun prepareApp() {
        viewModelScope.launch {
            messageEmitter.postValue(Message.Loading)
            if(settingsService.isFirstTime()) {
                val categories = listOf (
                    Category(name = context.getString(R.string.unclassified), color = "#${Integer.toHexString(context.resources.getColor(R.color.unclassified, null))}"),
                    Category(name = context.getString(R.string.family), color = "#${Integer.toHexString(context.resources.getColor(R.color.family, null))}"),
                    Category(name = context.getString(R.string.groceries), color = "#${Integer.toHexString(context.resources.getColor(R.color.groceries, null))}"),
                    Category(name = context.getString(R.string.friends), color = "#${Integer.toHexString(context.resources.getColor(R.color.friends, null))}"),
                    Category(name = context.getString(R.string.work), color = "#${Integer.toHexString(context.resources.getColor(R.color.work, null))}"),
                    Category(name = context.getString(R.string.sport), color = "#${Integer.toHexString(context.resources.getColor(R.color.sport, null))}")
                )
                categoryRepository.insert(categories)
                    .catch { ex ->
                        messageEmitter.postValue(Message.Error(Exception(ex.localizedMessage)))
                    }
                    .collect { success ->
                        Timber.i("Result $success")
                        messageEmitter.postValue(if(success) Message.Success() else Message.Error(Exception(context.getString(R.string.error_creating_default_categories))))
                    }
                settingsService.setIsFirstTime(false)
            } else {
                messageEmitter.postValue(Message.Success())
            }
        }
    }
}