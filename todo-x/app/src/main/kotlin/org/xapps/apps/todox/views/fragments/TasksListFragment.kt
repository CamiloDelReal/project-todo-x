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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.models.Task
import org.xapps.apps.todox.core.utils.info
import org.xapps.apps.todox.databinding.FragmentTasksListBinding
import org.xapps.apps.todox.viewmodels.FilterType
import org.xapps.apps.todox.viewmodels.TasksListViewModel
import org.xapps.apps.todox.views.adapters.DateHeaderDecoration
import org.xapps.apps.todox.views.adapters.TaskWithItemsAndCategoryAdapter
import org.xapps.apps.todox.views.popups.ConfirmPopup
import org.xapps.apps.todox.views.popups.HomeMoreOptionsPopup
import org.xapps.apps.todox.views.popups.TasksListMoreOptionsPopup
import org.xapps.apps.todox.views.utils.Message
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class TasksListFragment @Inject constructor() : Fragment() {

    private lateinit var bindings: FragmentTasksListBinding

    private val viewModel: TasksListViewModel by viewModels()

    private var messageJob: Job? = null
    private var paginationStatesJob: Job? = null
    private var paginationJob: Job? = null

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

        override fun requestEdit(task: Task) {
            findNavController().navigate(TasksListFragmentDirections.actionTasksListFragmentToEditTaskFragment(task.id))
        }

        override fun requestDelete(task: Task) {
            ConfirmPopup.showDialog(
                parentFragmentManager,
                getString(R.string.confirm_delete_task)
            ) { _, data ->
                val option = if (data.containsKey(ConfirmPopup.POPUP_OPTION)) {
                    data.getInt(ConfirmPopup.POPUP_OPTION)
                } else {
                    -1
                }
                when(option) {
                    ConfirmPopup.POPUP_YES -> {
                        viewModel.deleteTask(task)
                    }
                    ConfirmPopup.POPUP_NO -> {
                        info<CategoryDetailsFragment>("User has cancelled the delete operation")
                    }
                }
            }
        }

        override fun requestComplete(task: Task) {
            viewModel.completeTask(task)
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

        bindings.btnNewTask.setOnClickListener {
            findNavController().navigate(TasksListFragmentDirections.actionTasksListFragmentToEditTaskFragment())
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

        messageJob = lifecycleScope.launchWhenResumed {
            viewModel.messageFlow.collect {
                when (it) {
                    is Message.Loading -> {
                        bindings.progressbar.isVisible = true
                    }
                    is Message.Loaded -> {
                        bindings.progressbar.isVisible = false
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

        paginationStatesJob = lifecycleScope.launch {
            taskAdapter.loadStateFlow.collectLatest { loadStates ->
                bindings.progressbar.isVisible = (loadStates.refresh is LoadState.Loading)
            }
        }

        paginationJob = lifecycleScope.launchWhenResumed {
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

    override fun onPause() {
        super.onPause()
        messageJob?.cancel()
        messageJob = null
        paginationStatesJob?.cancel()
        paginationStatesJob = null
        paginationJob?.cancel()
        paginationJob = null
    }
}