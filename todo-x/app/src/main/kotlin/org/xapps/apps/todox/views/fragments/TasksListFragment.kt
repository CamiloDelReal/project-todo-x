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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.xapps.apps.todox.core.models.Task
import org.xapps.apps.todox.databinding.FragmentTasksListBinding
import org.xapps.apps.todox.viewmodels.FilterType
import org.xapps.apps.todox.viewmodels.TasksListViewModel
import org.xapps.apps.todox.views.adapters.DateHeaderDecoration
import org.xapps.apps.todox.views.adapters.TaskWithItemsAndCategoryAdapter
import org.xapps.apps.todox.views.popups.HomeMoreOptionsPopup
import org.xapps.apps.todox.views.popups.TasksListMoreOptionsPopup
import org.xapps.apps.todox.views.utils.Message
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class TasksListFragment @Inject constructor() : Fragment() {

    private lateinit var bindings: FragmentTasksListBinding

    private val viewModel: TasksListViewModel by viewModels()

    private val onBackPressedCallback: OnBackPressedCallback by lazy {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        }
    }

    private lateinit var taskAdapter: TaskWithItemsAndCategoryAdapter

    private val tasksItemListener = object: TaskWithItemsAndCategoryAdapter.ItemListener {
        override fun clicked(task: Task) {
            findNavController().navigate(TasksListFragmentDirections.actionTasksListFragmentToTaskDetailsFragment(task.id))
        }
        override fun taskUpdated(task: Task) {
            viewModel.updateTask(task)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        bindings = FragmentTasksListBinding.inflate(layoutInflater)
        bindings.lifecycleOwner = viewLifecycleOwner
        bindings.viewModel = viewModel
        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindings.lstTasks.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        taskAdapter = TaskWithItemsAndCategoryAdapter(tasksItemListener)
        bindings.lstTasks.adapter = taskAdapter
        val dateHeaderDecoration = DateHeaderDecoration(taskAdapter)
        bindings.lstTasks.addItemDecoration(dateHeaderDecoration)

        bindings.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        bindings.btnMoreOptions.setOnClickListener {
            TasksListMoreOptionsPopup.showDialog(
                    parentFragmentManager,
                    viewModel.filter
            ) { _, data ->
                val option = if (data.containsKey(TasksListMoreOptionsPopup.MORE_OPTIONS_FILTER_OPTION)) {
                    data.getSerializable(TasksListMoreOptionsPopup.MORE_OPTIONS_FILTER_OPTION) as FilterType
                } else {
                    FilterType.ALL
                }
                viewModel.filter = option
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

        lifecycleScope.launchWhenResumed {
            viewModel.messageFlow.collect {
                when (it) {
                    is Message.Loading -> {
                        bindings.progressbar.isVisible = true
                    }
                    is Message.Success -> {
                        bindings.progressbar.isVisible = false
                    }
                    is Message.Error -> {
                        bindings.progressbar.isVisible = false
                        Timber.e(it.exception)
                        // Show some message here
                    }
                }
            }
        }

        lifecycleScope.launch {
            taskAdapter.loadStateFlow.collectLatest { loadStates ->
                bindings.progressbar.isVisible = (loadStates.refresh is LoadState.Loading)
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.tasksFlow
                .collectLatest {
                    taskAdapter.submitData(it)
                }
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }
}