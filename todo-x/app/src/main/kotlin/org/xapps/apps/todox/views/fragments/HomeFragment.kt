package org.xapps.apps.todox.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.xapps.apps.todox.R
import org.xapps.apps.todox.viewmodels.HomeViewModel
import org.xapps.apps.todox.viewmodels.ViewModelFactory
import javax.inject.Inject


class HomeFragment @Inject constructor() : Fragment() {

    private lateinit var currentView: View

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        currentView = inflater.inflate(R.layout.fragment_home, container, false)
        viewModel = viewModelFactory.create(HomeViewModel::class.java)
        return currentView
    }

}
