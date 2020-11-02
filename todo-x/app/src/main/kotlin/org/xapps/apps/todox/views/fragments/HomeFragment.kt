package org.xapps.apps.todox.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import org.xapps.apps.todox.databinding.FragmentHomeBinding
import org.xapps.apps.todox.viewmodels.HomeViewModel
import org.xapps.apps.todox.views.adapters.CategoryAdapter
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment @Inject constructor() : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listCategory.layoutManager = GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false)
        listCategory.adapter = CategoryAdapter(viewModel.categories)

        btnHighPriority.setOnClickListener {  }
        btnInSchedule.setOnClickListener {  }
        btnToday.setOnClickListener {  }
    }
}
