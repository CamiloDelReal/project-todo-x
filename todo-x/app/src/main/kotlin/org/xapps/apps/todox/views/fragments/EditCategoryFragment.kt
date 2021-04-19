package org.xapps.apps.todox.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import com.skydoves.whatif.whatIf
import dagger.hilt.android.AndroidEntryPoint
import org.xapps.apps.todox.R
import org.xapps.apps.todox.databinding.FragmentEditCategoryBinding
import org.xapps.apps.todox.viewmodels.EditCategoryViewModel
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class EditCategoryFragment @Inject constructor(): Fragment() {

    private lateinit var bindings: FragmentEditCategoryBinding

    private val viewModel: EditCategoryViewModel by viewModels ()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        bindings = FragmentEditCategoryBinding.inflate(layoutInflater)
        bindings.lifecycleOwner = viewLifecycleOwner
        bindings.viewModel = viewModel
        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindings.btnChooseColor.setOnClickListener {
            MaterialColorPickerDialog
                .Builder(requireActivity())
                .setTitle(R.string.pick_color)
                .setColorShape(ColorShape.CIRCLE)
                .setColors(resources.getStringArray(R.array.colorPicker))
                .setDefaultColor(if(viewModel.categoryId == -1L) "#${Integer.toHexString(requireContext().resources.getColor(R.color.concrete, null))}" else viewModel.category.get()?.color!!)
                .setColorListener { color, colorHex ->
                    Timber.i("Color chosen $colorHex")
                    viewModel.setColor(colorHex)
                }
                .show()
        }

        bindings.btnFinish.setOnClickListener {
            viewModel.saveCategory()
        }
    }
}