package org.xapps.apps.todox.views.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.xapps.apps.todox.core.models.Category
import org.xapps.apps.todox.core.settings.SettingsService
import org.xapps.apps.todox.databinding.FragmentHomeBinding
import org.xapps.apps.todox.viewmodels.HomeViewModel
import org.xapps.apps.todox.views.adapters.CategoryAdapter
import org.xapps.apps.todox.views.popups.HomeMoreOptionsPopup
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment @Inject constructor() : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: HomeViewModel by viewModels()

    private val onBackPressedCallback: OnBackPressedCallback by lazy {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Timber.i("Pending to do")
            }
        }
    }

    @Inject
    lateinit var settings: SettingsService

    private lateinit var categoryAdapter: CategoryAdapter

    private val categoriesItemListener = object : CategoryAdapter.ItemListener {
        override fun clicked(category: Category) {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToCategoryDetailsFragment(
                    category.id
                )
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listCategory.layoutManager =
            GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false)
        categoryAdapter = CategoryAdapter(categoriesItemListener)
        listCategory.adapter = categoryAdapter

        btnMoreOptions.setOnClickListener {
            HomeMoreOptionsPopup.showDialog(
                parentFragmentManager
            ) { _, data ->
                val option = if (data.containsKey(HomeMoreOptionsPopup.MORE_OPTIONS_POPUP_OPTION)) {
                    data.getInt(HomeMoreOptionsPopup.MORE_OPTIONS_POPUP_OPTION)
                } else {
                    -1
                }
                when (option) {
                    HomeMoreOptionsPopup.MORE_OPTIONS_POPUP_DARK_MODE_UPDATED -> {
                        AppCompatDelegate.setDefaultNightMode(if (settings.isDarkModeOn()) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
                    }
                    HomeMoreOptionsPopup.MORE_OPTIONS_POPUP_OPEN_ABOUT_VIEW -> {
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAboutFragment())
                    }
                }
            }
        }

        btnCalendar.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCalendarFragment())
        }

        btnNewTask.setOnClickListener{
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToEditTaskFragment())
        }

        btnHighPriority.setOnClickListener {}

        btnInSchedule.setOnClickListener {}

        btnToday.setOnClickListener {}

        lifecycleScope.launch {
            categoryAdapter.loadStateFlow.collectLatest { loadStates ->
                progressbar.isVisible = (loadStates.refresh is LoadState.Loading)
            }
        }

        viewModel.categoriesPaginated().observe(viewLifecycleOwner, Observer {
            lifecycleScope.launch {
                categoryAdapter.submitData(it)
            }
        })

        viewModel.categories()
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
        setStatusBarForegoundColor(!settings.isDarkModeOn())
    }

}
