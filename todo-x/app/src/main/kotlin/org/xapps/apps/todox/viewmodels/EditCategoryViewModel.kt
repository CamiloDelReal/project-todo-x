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

}