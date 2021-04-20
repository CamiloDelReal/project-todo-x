package org.xapps.apps.todox.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import com.skydoves.whatif.whatIf
import dagger.hilt.android.AndroidEntryPoint
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.settings.SettingsService
import org.xapps.apps.todox.databinding.FragmentEditCategoryBinding
import org.xapps.apps.todox.viewmodels.EditCategoryViewModel
import org.xapps.apps.todox.views.adapters.ColorAdapter
import org.xapps.apps.todox.views.utils.Message
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class EditCategoryFragment @Inject constructor(): Fragment() {

    private lateinit var bindings: FragmentEditCategoryBinding

    private val viewModel: EditCategoryViewModel by viewModels ()

    @Inject
    lateinit var settings: SettingsService

    private val onBackPressedCallback: OnBackPressedCallback by lazy {
        object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        }
    }

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

        bindings.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        bindings.btnFinish.setOnClickListener {
            viewModel.saveCategory()
        }

        viewModel.message().observe(viewLifecycleOwner, Observer {
            when(it) {
                is Message.Loading -> {
                    bindings.progressbar.isVisible = true
                }
                is Message.Success -> {
                    bindings.progressbar.isVisible = false
                    // SOme message
                    val isSave = it.data as Boolean
                    if(isSave) {
                        findNavController().navigateUp()
                    }
                }
                is Message.Error -> {
                    bindings.progressbar.isVisible = false
                    Timber.e(it.exception)
                    //Message here
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
        setStatusBarForegoundColor(!settings.isDarkModeOn())
    }
}