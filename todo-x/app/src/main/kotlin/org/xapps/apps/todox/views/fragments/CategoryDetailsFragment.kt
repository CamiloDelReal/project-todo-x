package org.xapps.apps.todox.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.xapps.apps.todox.core.models.Task
import org.xapps.apps.todox.core.models.TaskWithItems
import org.xapps.apps.todox.databinding.FragmentCategoryDetailsBinding
import org.xapps.apps.todox.viewmodels.CategoryDetailsViewModel
import org.xapps.apps.todox.views.adapters.DateHeaderDecoration
import org.xapps.apps.todox.views.adapters.TaskWithItemsAdapter
import org.xapps.apps.todox.views.utils.ColorUtils
import org.xapps.apps.todox.views.utils.Message
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class CategoryDetailsFragment @Inject constructor(): Fragment() {

    private lateinit var bindings: FragmentCategoryDetailsBinding

    private val viewModel: CategoryDetailsViewModel by viewModels()

    private val onBackPressedCallback: OnBackPressedCallback by lazy {
        object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        }
    }

    private lateinit var taskAdapter: TaskWithItemsAdapter

    private val tasksItemListener = object: TaskWithItemsAdapter.ItemListener {
        override fun clicked(task: Task) {
            findNavController().navigate(CategoryDetailsFragmentDirections.actionCategoryDetailsFragmentToTaskDetailsFragment(task.id))
        }
        override fun taskUpdated(task: Task) {
            viewModel.updateTask(task)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindings = FragmentCategoryDetailsBinding.inflate(layoutInflater)
        bindings.lifecycleOwner = viewLifecycleOwner
        bindings.viewModel = viewModel
        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindings.lstTasks.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        taskAdapter = TaskWithItemsAdapter(tasksItemListener)
        bindings.lstTasks.adapter = taskAdapter
        val dateHeaderDecoration = DateHeaderDecoration(taskAdapter)
        bindings.lstTasks.addItemDecoration(dateHeaderDecoration)

        bindings.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.message().observe(viewLifecycleOwner, Observer {
            when(it) {
                is Message.Loading -> {
                    bindings.progressbar.isVisible = true
                }
                is Message.Success -> {
                    bindings.progressbar.isVisible = false
                    Timber.i("Success received  ${viewModel.category.get()}")
                    setStatusBarForegoundColor(!ColorUtils.isDarkColor(viewModel.category.get()!!.color))
                }
                is Message.Error -> {
                    bindings.progressbar.isVisible = false
                }
            }
        })

        lifecycleScope.launch {
            taskAdapter.loadStateFlow.collectLatest { loadStates ->
                bindings.progressbar.isVisible = (loadStates.refresh is LoadState.Loading)
            }
        }

        viewModel.tasks().observe(viewLifecycleOwner, Observer {
            lifecycleScope.launch {
                taskAdapter.submitData(it)
            }
        })

        when(viewModel.filter) {
            CategoryDetailsViewModel.FilterType.SCHEDULED -> {
                bindings.filterTabsLayout.selectTab(bindings.filterTabsLayout.getTabAt(0))
                viewModel.tasksScheduled()
            }
            CategoryDetailsViewModel.FilterType.IMPORTANT -> {
                bindings.filterTabsLayout.selectTab(bindings.filterTabsLayout.getTabAt(1))
                viewModel.tasksImportant()
            }
            CategoryDetailsViewModel.FilterType.COMPLETED -> {
                bindings.filterTabsLayout.selectTab(bindings.filterTabsLayout.getTabAt(2))
                viewModel.tasksCompleted()
            }
        }

        bindings.filterTabsLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                bindings.filterTabsLayout.postDelayed({
                    when(bindings.filterTabsLayout.selectedTabPosition) {
                        0 -> {
                            viewModel.tasksScheduled()
                        }
                        1 -> {
                            viewModel.tasksImportant()
                        }
                        2 -> {
                            viewModel.tasksCompleted()
                        }
                    }
                }, 100)
            }

        })

        bindings.btnNewTask.setOnClickListener {
            findNavController().navigate(CategoryDetailsFragmentDirections.actionCategoryDetailsFragmentToEditTaskFragment(categoryId = viewModel.categoryId))
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
        viewModel.category.get()?.let {
            setStatusBarForegoundColor(!ColorUtils.isDarkColor(it.color))
        }
    }
}
