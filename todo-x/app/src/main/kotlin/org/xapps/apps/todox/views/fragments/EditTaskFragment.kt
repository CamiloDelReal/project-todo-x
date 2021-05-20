package org.xapps.apps.todox.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.repositories.SettingsRepository
import org.xapps.apps.todox.core.utils.parseToString
import org.xapps.apps.todox.databinding.FragmentEditTaskBinding
import org.xapps.apps.todox.viewmodels.EditTaskViewModel
import org.xapps.apps.todox.views.popups.DatePickerFragment
import org.xapps.apps.todox.views.popups.TimePickerFragment
import org.xapps.apps.todox.views.utils.Message
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject


@AndroidEntryPoint
class EditTaskFragment @Inject constructor() : Fragment() {

    private lateinit var bindings: FragmentEditTaskBinding

    private val viewModel: EditTaskViewModel by viewModels()

    private val onBackPressedCallback: OnBackPressedCallback by lazy {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        }
    }

    @Inject
    lateinit var settings: SettingsRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindings = FragmentEditTaskBinding.inflate(layoutInflater)
        bindings.lifecycleOwner = viewLifecycleOwner
        bindings.viewModel = viewModel
        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindings.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        bindings.tilDate.setEndIconOnClickListener {
            DatePickerFragment.showPicker(
                fragmentManager = requireActivity().supportFragmentManager,
                date = viewModel.taskWithItems.get()?.task?.date
            ) { _, data ->
                val date = data.getSerializable(DatePickerFragment.ATTR_DATE) as LocalDate
                viewModel.taskWithItems.get()?.task?.date = date
                bindings.tieDate.setText(date.parseToString())
            }
        }

        bindings.tilStartDate.setEndIconOnClickListener {
            TimePickerFragment.showPicker(
                fragmentManager = requireActivity().supportFragmentManager,
                time = viewModel.taskWithItems.get()?.task?.startTime,
                maxTime = viewModel.taskWithItems.get()?.task?.endTime,
                use24Hours = true   // Hardcode for now
            ) { _, data ->
                val time = data.getSerializable(TimePickerFragment.ATTR_TIME) as LocalTime
                viewModel.taskWithItems.get()?.task?.startTime = time
                bindings.tieStartDate.setText(time.parseToString(use24Hour = true))
            }
        }

        bindings.tilEndDate.setEndIconOnClickListener {
            TimePickerFragment.showPicker(
                fragmentManager = requireActivity().supportFragmentManager,
                time = viewModel.taskWithItems.get()?.task?.endTime,
                minTime = viewModel.taskWithItems.get()?.task?.startTime,
                use24Hours = true   // Hardcode for now
            ) { _, data ->
                val time = data.getSerializable(TimePickerFragment.ATTR_TIME) as LocalTime
                viewModel.taskWithItems.get()?.task?.endTime = time
                bindings.tieEndDate.setText(time.parseToString(use24Hour = true))
            }
        }

        bindings.btnFinish.setOnClickListener {
            bindings.tilName.isErrorEnabled = false
            if (bindings.tieName.text.isNullOrEmpty() || bindings.tieName.text.isNullOrBlank()) {
                bindings.tilName.error = getString(R.string.empty_field)
                bindings.tilName.isErrorEnabled = true
            } else {
                viewModel.insertTask()
            }
        }

        viewModel.message().observe(viewLifecycleOwner, Observer {
            when(it) {
                is Message.Loading -> {
                    bindings.progressbar.isVisible = true
                }
                is Message.Success -> {
                    bindings.progressbar.isVisible = false
                    // SOme message
                    findNavController().navigateUp()
                }
                is Message.Error -> {
                    bindings.progressbar.isVisible = false
                    Timber.e(it.exception)
                    //Message here
                }
            }
        })

        lifecycleScope.launchWhenResumed {
            setStatusBarForegoundColor(!settings.isDarkModeOnValue())
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }
}