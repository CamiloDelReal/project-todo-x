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
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_category_details.*
import kotlinx.android.synthetic.main.fragment_home.progressbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.xapps.apps.todox.core.models.TaskWithItems
import org.xapps.apps.todox.databinding.FragmentCategoryDetailsBinding
import org.xapps.apps.todox.viewmodels.CategoryDetailsViewModel
import org.xapps.apps.todox.views.adapters.TaskWithItemsAdapter
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

    private lateinit var taskAdapter: TaskWithItemsAdapter

    private val tasksItemListener = object: TaskWithItemsAdapter.ItemListener {
        override fun clicked(task: TaskWithItems) {

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

        lstTasks.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        taskAdapter = TaskWithItemsAdapter(tasksItemListener)
        lstTasks.adapter = taskAdapter

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

        lifecycleScope.launch {
            taskAdapter.loadStateFlow.collectLatest { loadStates ->
                progressbar.isVisible = (loadStates.refresh is LoadState.Loading)
            }
        }

        viewModel.tasks().observe(viewLifecycleOwner, Observer {
            lifecycleScope.launch {
                taskAdapter.submitData(it)
            }
        })

        when(viewModel.filter) {
            CategoryDetailsViewModel.FilterType.SCHEDULED -> {
                filterTabsLayout.selectTab(filterTabsLayout.getTabAt(0))
                viewModel.tasksScheduled()
            }
            CategoryDetailsViewModel.FilterType.IMPORTANT -> {
                filterTabsLayout.selectTab(filterTabsLayout.getTabAt(1))
                viewModel.tasksImportant()
            }
            CategoryDetailsViewModel.FilterType.COMPLETED -> {
                filterTabsLayout.selectTab(filterTabsLayout.getTabAt(2))
                viewModel.tasksCompleted()
            }
        }

        filterTabsLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                filterTabsLayout.postDelayed({
                    when(filterTabsLayout.selectedTabPosition) {
                        0 -> {
                            viewModel.tasksScheduled()
                        }
                        1 -> {
                            viewModel.tasksImportant()
                        }
                        2 -> {
                            viewModel.tasksCompleted()
                        }
                    }
                }, 100)
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
