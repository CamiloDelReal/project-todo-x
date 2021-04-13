package org.xapps.apps.todox.viewmodels

import android.content.Context
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.models.Color
import javax.inject.Inject


@HiltViewModel
class EditCategoryViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    val colors: ObservableArrayList<Color> = ObservableArrayList()

    init {
        colors.addAll(listOf(
            Color("#${Integer.toHexString(context.resources.getColor(R.color.turquoise, null))}")
        ))
    }

}