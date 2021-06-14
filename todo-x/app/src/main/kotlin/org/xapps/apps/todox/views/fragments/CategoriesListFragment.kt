package org.xapps.apps.todox.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.xapps.apps.todox.core.models.Category
import org.xapps.apps.todox.core.repositories.SettingsRepository
import org.xapps.apps.todox.databinding.FragmentCategoriesListBinding
import org.xapps.apps.todox.viewmodels.CategoriesListViewModel
import org.xapps.apps.todox.views.adapters.CategoryListAdapter
import org.xapps.apps.todox.views.utils.Message
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class CategoriesListFragment @Inject constructor(): Fragment() {

    private lateinit var bindings: FragmentCategoriesListBinding

    private val viewModel: CategoriesListViewModel by viewModels()

    private var messageJob: Job? = null
    private var paginationStatesJob: Job? = null
    private var paginationJob: Job? = null
    private var statusBarForegroundJob: Job? = null

    private val onBackPressedCallback: OnBackPressedCallback by lazy {
        object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        }
    }

    @Inject
    lateinit var settings: SettingsRepository

    private lateinit var categoryAdapter: CategoryListAdapter

    private val categoriesItemListener = object : CategoryListAdapter.ItemListener {
        override fun clicked(category: Category) {
            findNavController().navigate(CategoriesListFragmentDirections.actionCategoriesListFragmentToEditCategoryFragment(category.id))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindings = FragmentCategoriesListBinding.inflate(layoutInflater)
        bindings.lifecycleOwner = viewLifecycleOwner
        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindings.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        bindings.btnNew.setOnClickListener {
            findNavController().navigate(CategoriesListFragmentDirections.actionCategoriesListFragmentToEditCategoryFragment())
        }

        bindings.lstCategories.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        categoryAdapter = CategoryListAdapter(categoriesItemListener)
        bindings.lstCategories.adapter = categoryAdapter

        categoryAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                Timber.i("CategoryAdapter data onChanged")
                bindings.noCategoriesView.isVisible = (categoryAdapter.itemCount == 0)
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                Timber.i("CategoryAdapter data onItemRangeRemoved")
                bindings.noCategoriesView.isVisible = (categoryAdapter.itemCount == 0)
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                Timber.i("CategoryAdapter data onItemRangeMoved")
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                Timber.i("CategoryAdapter data onItemRangeInserted")
                bindings.noCategoriesView.isVisible = (categoryAdapter.itemCount == 0)
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                Timber.i("CategoryAdapter data onItemRangeChanged")
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                Timber.i("CategoryAdapter data onItemRangeChanged")
            }
        })

        messageJob = lifecycleScope.launchWhenResumed {
            viewModel.messageFlow
                .collect {
                    when(it) {
                        is Message.Loading -> {
                            bindings.progressbar.isVisible = true
                        }
                        is Message.Loaded -> {
                            bindings.progressbar.isVisible = false
                        }
                        is Message.Success -> {
                            bindings.progressbar.isVisible = false
                            findNavController().navigateUp()
                        }
                        is Message.Error -> {
                            bindings.progressbar.isVisible = false
                            Timber.e(it.exception)
                            //Message here
                        }
                    }
                }
        }

        paginationStatesJob = lifecycleScope.launchWhenResumed {
            categoryAdapter.loadStateFlow.collectLatest { loadStates ->
                bindings.progressbar.isVisible = (loadStates.refresh is LoadState.Loading)
            }
        }

        paginationJob = lifecycleScope.launchWhenResumed {
            viewModel.categoriesPaginatedFlow
                .collect {
                    categoryAdapter.submitData(it)
                }
        }

        statusBarForegroundJob = lifecycleScope.launchWhenResumed {
            setStatusBarForegoundColor(!settings.isDarkModeOnValue())
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    override fun onPause() {
        super.onPause()
        messageJob?.cancel()
        messageJob = null
        paginationStatesJob?.cancel()
        paginationStatesJob = null
        paginationJob?.cancel()
        paginationJob = null
        statusBarForegroundJob?.cancel()
        statusBarForegroundJob = null
    }
}