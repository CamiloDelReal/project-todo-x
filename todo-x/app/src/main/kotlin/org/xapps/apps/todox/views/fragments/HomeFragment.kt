package org.xapps.apps.todox.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.models.Category
import org.xapps.apps.todox.core.settings.SettingsService
import org.xapps.apps.todox.databinding.FragmentHomeBinding
import org.xapps.apps.todox.viewmodels.FilterType
import org.xapps.apps.todox.viewmodels.HomeViewModel
import org.xapps.apps.todox.views.adapters.CategoryHomeAdapter
import org.xapps.apps.todox.views.popups.HomeMoreOptionsPopup
import org.xapps.apps.todox.views.utils.Message
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment @Inject constructor() : Fragment() {

    private lateinit var bindings: FragmentHomeBinding

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

    private lateinit var categoryAdapter: CategoryHomeAdapter

    private val categoriesItemListener = object : CategoryHomeAdapter.ItemListener {
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
    ): View {
        bindings = FragmentHomeBinding.inflate(layoutInflater)
        bindings.lifecycleOwner = viewLifecycleOwner
        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindings.lstCategories.layoutManager = GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false)
        categoryAdapter = CategoryHomeAdapter(categoriesItemListener)
        bindings.lstCategories.adapter = categoryAdapter

        bindings.btnMoreOptions.setOnClickListener {
            HomeMoreOptionsPopup.showDialog(
                parentFragmentManager
            ) { _, data ->
                val option = if (data.containsKey(HomeMoreOptionsPopup.MORE_OPTIONS_POPUP_OPTION)) {
                    data.getInt(HomeMoreOptionsPopup.MORE_OPTIONS_POPUP_OPTION)
                } else {
                    -1
                }
                when (option) {
                    HomeMoreOptionsPopup.MORE_OPTIONS_POPUP_OPEN_CATEGORIES -> {
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCategoriesListFragment())
                    }
                    HomeMoreOptionsPopup.MORE_OPTIONS_POPUP_DARK_MODE_UPDATED -> {
                        AppCompatDelegate.setDefaultNightMode(if (settings.isDarkModeOn()) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
                    }
                    HomeMoreOptionsPopup.MORE_OPTIONS_POPUP_OPEN_ABOUT_VIEW -> {
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAboutFragment())
                    }
                }
            }
        }

        bindings.btnCalendar.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCalendarFragment())
        }

        bindings.btnNewTask.setOnClickListener{
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToEditTaskFragment())
        }

        bindings.btnImportant.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToTasksListFragment(FilterType.IMPORTANT))
        }

        bindings.btnInSchedule.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToTasksListFragment(FilterType.SCHEDULED))
        }

        bindings.btnToday.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToTasksListFragment(FilterType.TODAY))
        }

        lifecycleScope.launch {
            categoryAdapter.loadStateFlow.collectLatest { loadStates ->
                bindings.progressbar.isVisible = (loadStates.refresh is LoadState.Loading)
            }
        }

        viewModel.message().observe(viewLifecycleOwner, {
            when(it) {
                is Message.Loading -> {
                    bindings.progressbar.isVisible = true
                }
                is Message.Success -> {
                    bindings.progressbar.isVisible = false
                    findNavController().navigateUp()
                }
                is Message.Error -> {
                    bindings.progressbar.isVisible = false
                    Timber.e(it.exception)
                    //Message here
                }
            }
        })

        viewModel.categoriesPaginated().observe(viewLifecycleOwner, {
            lifecycleScope.launch {
                categoryAdapter.submitData(it)
            }
        })

        viewModel.tasksImportantCount().observe(viewLifecycleOwner, { count ->
            Timber.i("Tasks important count received: $count")
            bindings.btnImportant.setDescription(resources.getQuantityString(R.plurals.task_count, count, count))
        })

        viewModel.tasksInScheduleCount().observe(viewLifecycleOwner, { count ->
            Timber.i("Tasks in schedule count received: $count")
            bindings.btnInSchedule.setDescription(resources.getQuantityString(R.plurals.task_count, count, count))
        })

        viewModel.tasksTodayCount().observe(viewLifecycleOwner, { count ->
            Timber.i("Tasks for today count received: $count")
            bindings.btnToday.setDescription(resources.getQuantityString(R.plurals.task_count, count, count))
        })

    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
        setStatusBarForegoundColor(!settings.isDarkModeOn())
    }

}
