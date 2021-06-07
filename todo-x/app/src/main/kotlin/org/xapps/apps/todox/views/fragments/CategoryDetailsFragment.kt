package org.xapps.apps.todox.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.models.Task
import org.xapps.apps.todox.core.utils.debug
import org.xapps.apps.todox.core.utils.error
import org.xapps.apps.todox.core.utils.info
import org.xapps.apps.todox.core.utils.warning
import org.xapps.apps.todox.databinding.FragmentCategoryDetailsBinding
import org.xapps.apps.todox.viewmodels.CategoryDetailsViewModel
import org.xapps.apps.todox.viewmodels.FilterType
import org.xapps.apps.todox.views.adapters.DateHeaderDecoration
import org.xapps.apps.todox.views.adapters.TaskWithItemsAdapter
import org.xapps.apps.todox.views.popups.CategoryDetailsMoreOptionsPopup
import org.xapps.apps.todox.views.popups.ConfirmDeleteCategoryPopup
import org.xapps.apps.todox.views.popups.ConfirmPopup
import org.xapps.apps.todox.views.utils.ColorUtils
import org.xapps.apps.todox.views.utils.Message
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class CategoryDetailsFragment @Inject constructor() : Fragment() {

    private lateinit var bindings: FragmentCategoryDetailsBinding

    private val viewModel: CategoryDetailsViewModel by viewModels()

    private val onBackPressedCallback: OnBackPressedCallback by lazy {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        }
    }

    private lateinit var taskAdapter: TaskWithItemsAdapter

    private val tasksItemListener = object : TaskWithItemsAdapter.ItemListener {
        override fun clicked(task: Task) {
            Timber.i("Requesting opening details for $task")
            findNavController().navigate(
                    CategoryDetailsFragmentDirections.actionCategoryDetailsFragmentToTaskDetailsFragment(task.id)
            )
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

        bindings.lstTasks.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        taskAdapter = TaskWithItemsAdapter(tasksItemListener)
        bindings.lstTasks.adapter = taskAdapter
        val dateHeaderDecoration = DateHeaderDecoration(taskAdapter)
        bindings.lstTasks.addItemDecoration(dateHeaderDecoration)

        bindings.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        bindings.btnNewTask.setOnClickListener {
            findNavController().navigate(
                CategoryDetailsFragmentDirections.actionCategoryDetailsFragmentToEditTaskFragment(
                    categoryId = viewModel.categoryId
                )
            )
        }

        bindings.btnMoreOptions.setOnClickListener {
            CategoryDetailsMoreOptionsPopup.showDialog(
                    parentFragmentManager
            ) { _, data ->
                val option = if (data.containsKey(CategoryDetailsMoreOptionsPopup.MORE_OPTIONS_POPUP_OPTION)) {
                    data.getInt(CategoryDetailsMoreOptionsPopup.MORE_OPTIONS_POPUP_OPTION)
                } else {
                    -1
                }
                when (option) {
                    CategoryDetailsMoreOptionsPopup.MORE_OPTIONS_POPUP_COMPLETE_ALL -> {
                        viewModel.completeAllTasks()
                    }
                    CategoryDetailsMoreOptionsPopup.MORE_OPTIONS_POPUP_DELETE_ALL -> {
                        confirmDeleteAllTasks()
                    }
                    CategoryDetailsMoreOptionsPopup.MORE_OPTIONS_POPUP_EDIT -> {
                        findNavController().navigate(CategoryDetailsFragmentDirections.actionCategoryDetailsFragmentToEditCategoryFragment(viewModel.categoryId))
                    }
                    CategoryDetailsMoreOptionsPopup.MORE_OPTIONS_POPUP_DELETE -> {
                        if(viewModel.canCategoryBeDeleted()) {
                            confirmDeleteCategory()
                        } else {
                            categoryCannotBeDeleted()
                        }
                    }
                }
            }
        }

        taskAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                bindings.noTasksView.isVisible = (taskAdapter.itemCount == 0)
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                bindings.noTasksView.isVisible = (taskAdapter.itemCount == 0)
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {}

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                bindings.noTasksView.isVisible = (taskAdapter.itemCount == 0)
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {}

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {}
        })

        bindings.filterTabsLayout.addOnTabSelectedListener(object :
                TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                bindings.filterTabsLayout.postDelayed({
                    lifecycleScope.launch {
                        when (bindings.filterTabsLayout.selectedTabPosition) {
                            0 -> {
                                viewModel.filter = FilterType.SCHEDULED
                            }
                            1 -> {
                                viewModel.filter = FilterType.IMPORTANT
                            }
                            2 -> {
                                viewModel.filter = FilterType.COMPLETED
                            }
                        }
                    }
                }, 100)
            }

        })

        lifecycleScope.launchWhenResumed {
           viewModel.messageFlow
               .collect {
                   when (it) {
                       is Message.Loading -> {
                           debug<CategoryDetailsFragment>("Loading message recieved")
                           bindings.progressbar.isVisible = true
                       }
                       is Message.Loaded -> {
                           bindings.progressbar.isVisible = false
                           setStatusBarForegoundColor(!ColorUtils.isDarkColor(viewModel.category.get()!!.color))
                       }
                       is Message.Success -> {
                           bindings.progressbar.isVisible = false
                       }
                       is Message.Error -> {
                           bindings.progressbar.isVisible = false
                           error<CategoryDetailsFragment>(it.exception, "Exception captured")
                           // TODO Show some message here
                       }
                   }
               }
        }

        lifecycleScope.launchWhenResumed {
            taskAdapter.loadStateFlow.collectLatest { loadStates ->
                bindings.progressbar.isVisible = (loadStates.refresh is LoadState.Loading)
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.tasksPaginatedFlow
                .collectLatest { data ->
                    info<CategoryDetailsFragment>("Task paging data received $data")
                    data?.let { taskAdapter.submitData(it) }
                }
        }

        when (viewModel.filter) {
            FilterType.SCHEDULED -> {
                bindings.filterTabsLayout.selectTab(bindings.filterTabsLayout.getTabAt(0))
            }
            FilterType.IMPORTANT -> {
                bindings.filterTabsLayout.selectTab(bindings.filterTabsLayout.getTabAt(1))
            }
            FilterType.COMPLETED -> {
                bindings.filterTabsLayout.selectTab(bindings.filterTabsLayout.getTabAt(2))
            }
            else -> {
                warning<CategoryDetailsFragment>("Task filter ${viewModel.filter} wrong for this view")
            }
        }
    }

    private fun confirmDeleteAllTasks() {
        ConfirmPopup.showDialog(
                parentFragmentManager,
                getString(R.string.confirm_delete_all_tasks_in_category)
        ) { _, data ->
            val option = if (data.containsKey(ConfirmPopup.POPUP_OPTION)) {
                data.getInt(ConfirmPopup.POPUP_OPTION)
            } else {
                -1
            }
            when(option) {
                ConfirmPopup.POPUP_YES -> {
                    viewModel.deleteAllTasks()
                }
                ConfirmPopup.POPUP_NO -> {
                    info<CategoryDetailsViewModel>("User has cancelled the delete operation")
                }
            }
        }
    }

    private fun categoryCannotBeDeleted() {
        ConfirmPopup.showDialog(
            parentFragmentManager,
            getString(R.string.unclassified_cannot_be_deleted),
            confirmMode = false
        ) { _, _ -> }
    }

    private fun confirmDeleteCategory() {
        ConfirmDeleteCategoryPopup.showDialog(
            parentFragmentManager,
        ) { _, data ->
            val option = if (data.containsKey(ConfirmDeleteCategoryPopup.POPUP_OPTION)) {
                data.getInt(ConfirmDeleteCategoryPopup.POPUP_OPTION)
            } else {
                -1
            }
            when(option) {
                ConfirmDeleteCategoryPopup.POPUP_YES -> {
                    val deleteTasks = data.getBoolean(ConfirmDeleteCategoryPopup.POPUP_OPTION_DELETE_ALL_TASKS, false)
                    viewModel.deleteCategory(deleteTasks = deleteTasks)
                }
                ConfirmDeleteCategoryPopup.POPUP_NO -> {
                    info<CategoryDetailsViewModel>("User has cancelled the delete operation")
                }
            }
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
