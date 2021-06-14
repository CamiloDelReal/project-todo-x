package org.xapps.apps.todox.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.models.Item
import org.xapps.apps.todox.core.utils.debug
import org.xapps.apps.todox.core.utils.error
import org.xapps.apps.todox.core.utils.info
import org.xapps.apps.todox.databinding.FragmentTaskDetailsBinding
import org.xapps.apps.todox.viewmodels.Constants
import org.xapps.apps.todox.viewmodels.TaskDetailsViewModel
import org.xapps.apps.todox.views.adapters.ItemAdapter
import org.xapps.apps.todox.views.extensions.showError
import org.xapps.apps.todox.views.extensions.showSuccess
import org.xapps.apps.todox.views.extensions.showWarning
import org.xapps.apps.todox.views.popups.ConfirmPopup
import org.xapps.apps.todox.views.popups.TaskDetailsMoreOptionsPopup
import org.xapps.apps.todox.views.utils.ColorUtils
import org.xapps.apps.todox.views.utils.Message
import javax.inject.Inject


@AndroidEntryPoint
class TaskDetailsFragment @Inject constructor(): Fragment() {

    private lateinit var bindings: FragmentTaskDetailsBinding

    private val viewModel: TaskDetailsViewModel by viewModels()

    private val onBackPressedCallback: OnBackPressedCallback by lazy {
        object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        }
    }

    private lateinit var itemAdapter: ItemAdapter

    private val itemListener = object: ItemAdapter.ItemListener {
        override fun itemUpdated(item: Item) {
            info<TaskDetailsFragment>("Requesting update $item")
            viewModel.updateItem(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindings = FragmentTaskDetailsBinding.inflate(layoutInflater)
        bindings.lifecycleOwner = viewLifecycleOwner
        bindings.viewModel = viewModel
        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemAdapter = ItemAdapter(viewModel.items, false, itemListener)
        bindings.lstItems.itemAnimator = null
        bindings.lstItems.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        bindings.lstItems.adapter = itemAdapter

        bindings.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        bindings.btnChangePriority.setOnClickListener {
            viewModel.changePriority()
        }

        bindings.btnMoreOptions.setOnClickListener {
            TaskDetailsMoreOptionsPopup.showDialog(
                parentFragmentManager,
                viewModel.taskWithItemsAndCategory.get()!!.task.done
            ) { _, data ->
                val option = if (data.containsKey(TaskDetailsMoreOptionsPopup.MORE_OPTIONS_POPUP_OPTION)) {
                    data.getInt(TaskDetailsMoreOptionsPopup.MORE_OPTIONS_POPUP_OPTION)
                } else {
                    -1
                }
                when (option) {
                    TaskDetailsMoreOptionsPopup.MORE_OPTIONS_POPUP_COMPLETE -> {
                        viewModel.completeTask()
                    }
                    TaskDetailsMoreOptionsPopup.MORE_OPTIONS_POPUP_UNCOMPLETE -> {
                        viewModel.uncompleteTask()
                    }
                    TaskDetailsMoreOptionsPopup.MORE_OPTIONS_POPUP_EDIT -> {
                        findNavController().navigate(TaskDetailsFragmentDirections.actionTaskDetailsFragmentToEditTaskFragment(viewModel.taskId))
                    }
                    TaskDetailsMoreOptionsPopup.MORE_OPTIONS_POPUP_DELETE -> {
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
                                    viewModel.deleteTask()
                                }
                                ConfirmPopup.POPUP_NO -> {
                                    info<TaskDetailsFragment>("User has cancelled the delete operation")
                                }
                            }
                        }
                    }
                }
            }
        }

        bindings.emptyView.setOnActionClickListener {
            val itemsEditSheet = TaskEditItemsBottomSheetFragment()
            val data = Bundle().apply {
                putLong(Constants.TASK_ID, viewModel.taskId)
            }
            itemsEditSheet.arguments = data
            itemsEditSheet.show(parentFragmentManager, TaskEditItemsBottomSheetFragment::class.java.name)
            itemsEditSheet.dialog?.setOnDismissListener {
                Log.i("AppLogger", "Bottom closed")
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.messageFlow
                .collect {
                    when (it) {
                        is Message.Loading -> {
                            bindings.progressbar.isVisible = true
                        }
                        is Message.Loaded -> {
                            bindings.progressbar.isVisible = false
                        }
                        is Message.Success -> {
                            debug<CategoryDetailsFragment>("Success message received with value ${it.data}")
                            bindings.progressbar.isVisible = false
                            val op = it.data as TaskDetailsViewModel.Operation
                            when(op) {
                                TaskDetailsViewModel.Operation.TASK_DELETE -> {
                                    showSuccess(getString(R.string.task_deleted_successfully))
                                    findNavController().navigateUp()
                                }
                                TaskDetailsViewModel.Operation.TASK_COMPLETED -> {
                                    showWarning(getString(R.string.complete_alert_message))
                                }
                                TaskDetailsViewModel.Operation.TASK_UNCOMPLETED -> {
                                    showWarning(getString(R.string.uncomplete_alert_message))
                                }
                            }
                        }
                        is Message.Error -> {
                            bindings.progressbar.isVisible = false
                            error<TaskDetailsFragment>(it.exception, "Exception received")
                            showError(it.exception.message!!)
                        }
                    }
                }
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
        viewModel.taskWithItemsAndCategory.get()?.let {
            setStatusBarForegoundColor(!ColorUtils.isDarkColor(it.category.color))
        }
    }

}
