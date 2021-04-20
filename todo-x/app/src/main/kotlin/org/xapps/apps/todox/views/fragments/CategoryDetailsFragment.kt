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
import org.xapps.apps.todox.viewmodels.FilterType
import org.xapps.apps.todox.views.adapters.DateHeaderDecoration
import org.xapps.apps.todox.views.adapters.TaskWithItemsAdapter
import org.xapps.apps.todox.views.popups.CategoryDetailsMoreOptionsPopup
import org.xapps.apps.todox.views.popups.HomeMoreOptionsPopup
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
            findNavController().navigate(
                    CategoryDetailsFragmentDirections.actionCategoryDetailsFragmentToTaskDetailsFragment(
                            task.id
                    )
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
                    CategoryDetailsMoreOptionsPopup.MORE_OPTIONS_POPUP_EDIT -> {
                        findNavController().navigate(CategoryDetailsFragmentDirections.actionCategoryDetailsFragmentToEditCategoryFragment(viewModel.categoryId))
                    }
                    CategoryDetailsMoreOptionsPopup.MORE_OPTIONS_POPUP_COMPLETE_ALL -> {
                        Timber.w("ToDo")
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
                }, 100)
            }

        })

        viewModel.message().observe(viewLifecycleOwner, Observer {
            when (it) {
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
                    Timber.e(it.exception)
                    // Show some message here
                }
            }
        })

        lifecycleScope.launch {
            taskAdapter.loadStateFlow.collectLatest { loadStates ->
                bindings.progressbar.isVisible = (loadStates.refresh is LoadState.Loading)
            }
        }

        viewModel.tasksPaginated().observe(viewLifecycleOwner, { data ->
            lifecycleScope.launch {
                data?.let { taskAdapter.submitData(it) }
            }
        })

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
