package org.xapps.apps.todox.views.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.models.Color
import org.xapps.apps.todox.databinding.FragmentBottomSheetEditCategoryBinding
import org.xapps.apps.todox.viewmodels.EditCategoryViewModel
import org.xapps.apps.todox.views.adapters.ColorAdapter
import timber.log.Timber


@AndroidEntryPoint
class EditCategoryBottomSheetDialogFragment: BottomSheetDialogFragment() {

    private lateinit var bindings: FragmentBottomSheetEditCategoryBinding

    private val viewModel: EditCategoryViewModel by viewModels()

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private lateinit var colorAdapter: ColorAdapter

    override fun getTheme(): Int {
        return R.style.Theme_BottomSheet
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheet = super.onCreateDialog(savedInstanceState)
        bindings = FragmentBottomSheetEditCategoryBinding.inflate(layoutInflater)
        bottomSheet.setContentView(bindings.root)

        bottomSheetBehavior = BottomSheetBehavior.from(bindings.root.parent as View)
        bottomSheetBehavior.peekHeight = resources.displayMetrics.heightPixels
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

        colorAdapter = ColorAdapter(viewModel.colors)
        bindings.lstColors.layoutManager = GridLayoutManager(requireContext(), 5, RecyclerView.VERTICAL, false)
        bindings.lstColors.adapter = colorAdapter

        return bottomSheet
    }

}