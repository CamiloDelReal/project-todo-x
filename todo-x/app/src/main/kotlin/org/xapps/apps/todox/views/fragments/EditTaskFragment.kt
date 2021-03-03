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
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_edit_task.*
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.utils.parseToString
import org.xapps.apps.todox.databinding.FragmentEditTaskBinding
import org.xapps.apps.todox.viewmodels.EditTaskViewModel
import org.xapps.apps.todox.views.popups.DatePickerFragment
import org.xapps.apps.todox.views.popups.TimePickerFragment
import org.xapps.apps.todox.views.utils.Message
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject


@AndroidEntryPoint
class EditTaskFragment @Inject constructor() : Fragment() {

    private lateinit var binding: FragmentEditTaskBinding

    private val viewModel: EditTaskViewModel by viewModels()

    private val onBackPressedCallback: OnBackPressedCallback by lazy {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditTaskBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        tilDate.setEndIconOnClickListener {
            DatePickerFragment.showPicker(
                fragmentManager = requireActivity().supportFragmentManager,
                date = viewModel.task.get()?.date
            ) { _, data ->
                val date = data.getSerializable(DatePickerFragment.ATTR_DATE) as LocalDate
                viewModel.task.get()?.date = date
                tieDate.setText(date.parseToString())
            }
        }

        tilStartDate.setEndIconOnClickListener {
            TimePickerFragment.showPicker(
                fragmentManager = requireActivity().supportFragmentManager,
                time = viewModel.task.get()?.startTime,
                maxTime = viewModel.task.get()?.endTime,
                use24Hours = true   // Hardcode for now
            ) { _, data ->
                val time = data.getSerializable(TimePickerFragment.ATTR_TIME) as LocalTime
                viewModel.task.get()?.startTime = time
                tieStartDate.setText(time.parseToString(use24Hour = true))
            }
        }

        tilEndDate.setEndIconOnClickListener {
            TimePickerFragment.showPicker(
                fragmentManager = requireActivity().supportFragmentManager,
                time = viewModel.task.get()?.endTime,
                minTime = viewModel.task.get()?.startTime,
                use24Hours = true   // Hardcode for now
            ) { _, data ->
                val time = data.getSerializable(TimePickerFragment.ATTR_TIME) as LocalTime
                viewModel.task.get()?.endTime = time
                tieEndDate.setText(time.parseToString(use24Hour = true))
            }
        }

        btnFinish.setOnClickListener {
            tilName.isErrorEnabled = false
            if (tieName.text.isNullOrEmpty() || tieName.text.isNullOrBlank()) {
                tilName.error = getString(R.string.empty_field)
                tilName.isErrorEnabled = true
            } else {
                viewModel.insertTask()
            }
        }

        viewModel.message().observe(viewLifecycleOwner, Observer {
            when(it) {
                is Message.Loading -> {
                    progressbar.isVisible = true
                }
                is Message.Success -> {
                    progressbar.isVisible = false
                    // SOme message
                    findNavController().navigateUp()
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
    }
}