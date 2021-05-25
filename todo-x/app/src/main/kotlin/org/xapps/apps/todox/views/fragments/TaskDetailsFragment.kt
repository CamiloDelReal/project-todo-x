package org.xapps.apps.todox.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import org.xapps.apps.todox.core.models.Item
import org.xapps.apps.todox.core.utils.info
import org.xapps.apps.todox.databinding.FragmentTaskDetailsBinding
import org.xapps.apps.todox.viewmodels.TaskDetailsViewModel
import org.xapps.apps.todox.views.adapters.ItemAdapter
import org.xapps.apps.todox.views.utils.ColorUtils
import javax.inject.Inject


@AndroidEntryPoint
class TaskDetailsFragment @Inject constructor(): Fragment() {

    private lateinit var bindings: FragmentTaskDetailsBinding

    private val viewModel: TaskDetailsViewModel by viewModels()

    private val onBackPressedCallback: OnBackPressedCallback by lazy {
        object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        }
    }

    private lateinit var itemAdapter: ItemAdapter

    private val itemListener = object: ItemAdapter.ItemListener {
        override fun itemUpdated(item: Item) {
            info<TaskDetailsFragment>("Requesting update $item")
            viewModel.updateItem(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindings = FragmentTaskDetailsBinding.inflate(layoutInflater)
        bindings.lifecycleOwner = viewLifecycleOwner
        bindings.viewModel = viewModel
        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemAdapter = ItemAdapter(viewModel.items, false, itemListener)
        bindings.lstItems.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        bindings.lstItems.adapter = itemAdapter

        bindings.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
        viewModel.taskWithItemsAndCategory.get()?.let {
            setStatusBarForegoundColor(!ColorUtils.isDarkColor(it.category.color))
        }
    }

}
