package org.xapps.apps.todox.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import com.skydoves.whatif.whatIf
import dagger.hilt.android.AndroidEntryPoint
import org.xapps.apps.todox.R
import org.xapps.apps.todox.databinding.FragmentEditCategoryBinding
import org.xapps.apps.todox.viewmodels.EditCategoryViewModel
import org.xapps.apps.todox.views.adapters.ColorAdapter
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class EditCategoryFragment @Inject constructor(): Fragment() {

    private lateinit var bindings: FragmentEditCategoryBinding

    private val viewModel: EditCategoryViewModel by viewModels ()

    private lateinit var colorAdapter: ColorAdapter
    private val colorItemListener = object: ColorAdapter.ItemListener {
        override fun clicked(colorHex: String) {
            viewModel.setColor(colorHex)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        bindings = FragmentEditCategoryBinding.inflate(layoutInflater)
        bindings.lifecycleOwner = viewLifecycleOwner
        bindings.viewModel = viewModel
        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        colorAdapter = ColorAdapter(viewModel.colors, viewModel.chosenColor, colorItemListener)
        bindings.lstColors.layoutManager = GridLayoutManager(requireContext(), 5, RecyclerView.VERTICAL, false)
        bindings.lstColors.adapter = colorAdapter

        bindings.btnFinish.setOnClickListener {
            viewModel.saveCategory()
        }
    }
}