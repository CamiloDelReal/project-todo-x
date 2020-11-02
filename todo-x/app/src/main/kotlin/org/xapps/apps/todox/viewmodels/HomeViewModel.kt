package org.xapps.apps.todox.viewmodels

import androidx.databinding.ObservableArrayList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import org.xapps.apps.todox.services.models.Category


class HomeViewModel @ViewModelInject constructor() : ViewModel() {

    val categories = ObservableArrayList<Category>()


    init {
        categories.addAll(listOf(
            Category(name = "Groceries", color = "#11998e"),
            Category(name = "Work", color = "#ED213A"),
            Category(name = "Freelance", color = "#00B4DB"),
            Category(name = "Sport", color = "#FF0099"),
            Category(name = "Travel", color = "#FFE000"),
            Category(name = "Family", color = "#24FE41"),
            Category(name = "Friends", color = "#5C258D"),
            Category(name = "Unclassified", color = "#ffffff")
        ))
    }

}