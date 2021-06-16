package org.xapps.apps.todox.views.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import org.xapps.apps.todox.R
import org.xapps.apps.todox.databinding.FragmentTaskEditItemsBinding
import org.xapps.apps.todox.viewmodels.TaskEditItemsViewModel
import org.xapps.apps.todox.views.adapters.ItemAdapter
import org.xapps.apps.todox.views.extensions.showError
import org.xapps.apps.todox.views.extensions.showSuccess
import org.xapps.apps.todox.views.utils.Message
import javax.inject.Inject


@AndroidEntryPoint
class TaskEditItemsBottomSheetFragment @Inject constructor(): BottomSheetDialogFragment() {

    private lateinit var bindings: FragmentTaskEditItemsBinding

    private val viewModel: TaskEditItemsViewModel by viewModels()

    private var messageJob: Job? = null

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private lateinit var itemsAdapter: ItemAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheet = super.onCreateDialog(savedInstanceState)

        bindings = FragmentTaskEditItemsBinding.inflate(layoutInflater)
        bottomSheet.setContentView(bindings.root)

        itemsAdapter = ItemAdapter(viewModel.items, true)
        bindings.lstTasks.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        bindings.lstTasks.adapter = itemsAdapter

        val parentLayout = bottomSheet.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val layoutParams = parentLayout.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        parentLayout.layoutParams = layoutParams

        bottomSheetBehavior = BottomSheetBehavior.from(bindings.root.parent as View)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        bottomSheetBehavior.peekHeight = resources.displayMetrics.heightPixels / 3


        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
//                    BottomSheetBehavior.STATE_EXPANDED ->
//                    BottomSheetBehavior.STATE_COLLAPSED ->
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        dismiss()
                    }
                }
            }

        })

        bindings.btnBack.setOnClickListener {
            dismiss()
        }

        bindings.btnFinish.setOnClickListener {
            viewModel.saveItems()
        }

        messageJob = lifecycleScope.launchWhenResumed {
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
                            bindings.progressbar.isVisible = false
                            val op = it.data as TaskEditItemsViewModel.Operation
                            if(op == TaskEditItemsViewModel.Operation.ITEMS_SAVED) {
                                showSuccess(getString(R.string.items_saved))
                            }
                            dismiss()
                        }
                        is Message.Error -> {
                            bindings.progressbar.isVisible = false
                            org.xapps.apps.todox.core.utils.error<EditTaskFragment>(it.exception)
                            showError(it.exception.message!!)
                        }
                    }
                }
        }

        return bottomSheet
    }

    override fun onDestroyView() {
        super.onDestroyView()
        messageJob?.cancel()
        messageJob = null
    }
}