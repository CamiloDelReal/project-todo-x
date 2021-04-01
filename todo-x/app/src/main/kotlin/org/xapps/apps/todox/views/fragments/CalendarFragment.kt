package org.xapps.apps.todox.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.xapps.apps.todox.databinding.FragmentCalendarBinding
import org.xapps.apps.todox.viewmodels.CalendarViewModel
import javax.inject.Inject


@AndroidEntryPoint
class CalendarFragment @Inject constructor(): Fragment() {

    private lateinit var bindings: FragmentCalendarBinding

    private val viewModel: CalendarViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindings = FragmentCalendarBinding.inflate(layoutInflater)
        bindings.lifecycleOwner = viewLifecycleOwner
        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}