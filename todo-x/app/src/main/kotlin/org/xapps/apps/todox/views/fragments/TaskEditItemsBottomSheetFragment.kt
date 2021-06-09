package org.xapps.apps.todox.views.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import org.xapps.apps.todox.databinding.FragmentTaskEditItemsBinding
import org.xapps.apps.todox.viewmodels.TaskEditItemsViewModel
import javax.inject.Inject


@AndroidEntryPoint
class TaskEditItemsBottomSheetFragment @Inject constructor(): BottomSheetDialogFragment() {

    private lateinit var bindings: FragmentTaskEditItemsBinding

    private val viewModel: TaskEditItemsViewModel by viewModels()

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheet = super.onCreateDialog(savedInstanceState)

        bindings = FragmentTaskEditItemsBinding.inflate(layoutInflater)
        bottomSheet.setContentView(bindings.root)

        bottomSheetBehavior = BottomSheetBehavior.from(bindings.root.parent as View)
        bottomSheetBehavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO
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

        return bottomSheet
    }

    override fun onStart() {
        super.onStart()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

}