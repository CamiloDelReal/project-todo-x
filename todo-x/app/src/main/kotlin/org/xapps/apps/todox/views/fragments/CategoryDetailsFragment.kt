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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_category_details.*
import org.xapps.apps.todox.databinding.FragmentCategoryDetailsBinding
import org.xapps.apps.todox.viewmodels.CategoryDetailsViewModel
import org.xapps.apps.todox.views.utils.ColorUtils
import org.xapps.apps.todox.views.utils.Message
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class CategoryDetailsFragment @Inject constructor(): Fragment() {

    private lateinit var binding: FragmentCategoryDetailsBinding

    private val viewModel: CategoryDetailsViewModel by viewModels()

    private val onBackPressedCallback: OnBackPressedCallback by lazy {
        object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryDetailsBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.message().observe(viewLifecycleOwner, Observer {
            when(it) {
                is Message.Loading -> {
                    progressbar.isVisible = true
                }
                is Message.Success -> {
                    progressbar.isVisible = false
                    Timber.i("Success received  ${viewModel.category.get()}")
                    setStatusBarForegoundColor(!ColorUtils.isDarkColor(viewModel.category.get()!!.color))
                }
                is Message.Error -> {
                    progressbar.isVisible = false
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
        viewModel.category.get()?.let {
            setStatusBarForegoundColor(!ColorUtils.isDarkColor(it.color))
        }
    }
}
