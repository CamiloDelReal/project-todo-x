package org.xapps.apps.todox.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.models.Category
import org.xapps.apps.todox.core.repositories.CategoryRepository
import org.xapps.apps.todox.core.repositories.SettingsRepository
import org.xapps.apps.todox.core.utils.debug
import org.xapps.apps.todox.core.utils.error
import org.xapps.apps.todox.core.utils.info
import org.xapps.apps.todox.views.utils.Message
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository,
    private val categoryRepository: CategoryRepository
): ViewModel() {

    private val _messageFlow: MutableSharedFlow<Message> = MutableSharedFlow(replay = 1)

    val messageFlow: SharedFlow<Message> = _messageFlow

    fun prepareApp() {
        viewModelScope.launch {
            _messageFlow.emit(Message.Loading)
            if(settingsRepository.isFirstTimeValue()) {
                info<SplashViewModel>("Inserting default categories")
                val categories = listOf (
                    Category(name = context.getString(R.string.unclassified), color = "#${Integer.toHexString(context.resources.getColor(R.color.concrete, null))}"),
                    Category(name = context.getString(R.string.family), color = "#${Integer.toHexString(context.resources.getColor(R.color.alizarin, null))}"),
                    Category(name = context.getString(R.string.groceries), color = "#${Integer.toHexString(context.resources.getColor(R.color.turquoise, null))}"),
                    Category(name = context.getString(R.string.friends), color = "#${Integer.toHexString(context.resources.getColor(R.color.amethyst, null))}"),
                    Category(name = context.getString(R.string.work), color = "#${Integer.toHexString(context.resources.getColor(R.color.nephritis, null))}"),
                    Category(name = context.getString(R.string.sport), color = "#${Integer.toHexString(context.resources.getColor(R.color.peterriver, null))}")
                )
                debug<SplashViewModel>("Categories $categories")
                val result = categoryRepository.insert(categories)
                result.either({ failure ->
                    error<SplashViewModel>("Error received $failure")
                    _messageFlow.tryEmit((Message.Error(Exception(context.getString(R.string.error_creating_default_categories)))))
                }, {
                    _messageFlow.tryEmit((Message.Success()))
                    viewModelScope.launch {
                        settingsRepository.setIsFirstTime(false)
                    }
                })
            } else {
                info<SplashViewModel>("Isn't first time ToDoX is running in this device")
                _messageFlow.emit((Message.Success()))
            }
        }
    }

}