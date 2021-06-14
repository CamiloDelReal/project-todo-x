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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.utils.error
import org.xapps.apps.todox.core.utils.info
import org.xapps.apps.todox.core.models.Category
import org.xapps.apps.todox.core.repositories.SettingsRepository
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

    private var messageJob: Job? = null
    private var paginationStatesJob: Job? = null
    private var paginationJob: Job? = null
    private var statusBarForegroundJob: Job? = null
    private var tasksImportantJob: Job? = null
    private var tasksInScheduleJob: Job? = null
    private var tasksTodayJob: Job? = null
    private var darkModeJob: Job? = null

    private val onBackPressedCallback: OnBackPressedCallback by lazy {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Timber.i("Pending to do")
            }
        }
    }

    @Inject
    lateinit var settings: SettingsRepository

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
                        info<HomeFragment>("Open Categories view request received")
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCategoriesListFragment())
                    }
                    HomeMoreOptionsPopup.MORE_OPTIONS_POPUP_DARK_MODE_UPDATED -> {
                        info<HomeFragment>("Dark mode configuration has changed. Flow collector will handle it")
                    }
                    HomeMoreOptionsPopup.MORE_OPTIONS_POPUP_OPEN_ABOUT_VIEW -> {
                        info<HomeFragment>("Open About view request received")
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

        paginationStatesJob = lifecycleScope.launch {
            categoryAdapter.loadStateFlow.collectLatest { loadStates ->
                bindings.progressbar.isVisible = (loadStates.refresh is LoadState.Loading)
            }
        }

        messageJob = lifecycleScope.launchWhenResumed {
            viewModel.messageFlow
                .collect {
                    when (it) {
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
                }
        }

        paginationJob = lifecycleScope.launchWhenResumed {
            viewModel.categoriesPaginatedFlow
                .collect {
                    categoryAdapter.submitData(it)
                }
        }

        tasksImportantJob = lifecycleScope.launchWhenResumed {
            viewModel.tasksImportantCount()
                .collect { count ->
                    info<HomeFragment>("Tasks important count received: $count")
                    bindings.btnImportant.setDescription(resources.getQuantityString(R.plurals.task_count, count, count))
                }
        }

        tasksInScheduleJob = lifecycleScope.launchWhenResumed {
            viewModel.tasksInScheduleCount()
                .collect { count ->
                    info<HomeFragment>("Tasks in schedule count received: $count")
                    bindings.btnInSchedule.setDescription(resources.getQuantityString(R.plurals.task_count, count, count))
                }
        }

        tasksTodayJob = lifecycleScope.launchWhenResumed {
            viewModel.tasksTodayCount()
                .collect { count ->
                    info<HomeFragment>("Tasks for today count received: $count")
                    bindings.btnToday.setDescription(resources.getQuantityString(R.plurals.task_count, count, count))
                }
        }

        darkModeJob = lifecycleScope.launchWhenResumed {
            viewModel.isDarkModeOn()
                .catch { ex->
                    error<HomeFragment>(ex)
                }
                .collect { isDarkModeOn ->
                    info<HomeFragment>("Using dark mode flow collector")
                    AppCompatDelegate.setDefaultNightMode(if (isDarkModeOn) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
                }
        }

        statusBarForegroundJob = lifecycleScope.launchWhenResumed {
            setStatusBarForegoundColor(!viewModel.isDarkModeOnValue())
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    override fun onPause() {
        super.onPause()
        messageJob?.cancel()
        messageJob = null
        paginationStatesJob?.cancel()
        paginationStatesJob = null
        paginationJob?.cancel()
        paginationJob = null
        statusBarForegroundJob?.cancel()
        statusBarForegroundJob = null
        tasksImportantJob?.cancel()
        tasksImportantJob = null
        tasksInScheduleJob?.cancel()
        tasksInScheduleJob = null
        tasksTodayJob?.cancel()
        tasksTodayJob = null
        darkModeJob?.cancel()
        darkModeJob = null
    }
}
