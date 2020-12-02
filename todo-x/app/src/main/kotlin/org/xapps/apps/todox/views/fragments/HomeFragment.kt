package org.xapps.apps.todox.views.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import org.xapps.apps.todox.R
import org.xapps.apps.todox.databinding.FragmentHomeBinding
import org.xapps.apps.todox.services.settings.SettingsService
import org.xapps.apps.todox.viewmodels.HomeViewModel
import org.xapps.apps.todox.views.adapters.CategoryAdapter
import org.xapps.apps.todox.views.popups.MoreOptionsPopup
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment @Inject constructor() : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var settings: SettingsService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listCategory.layoutManager = GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false)
        listCategory.adapter = CategoryAdapter(viewModel.categories)

        btnMoreOptions.setOnClickListener {
            val moreOptionPopup = MoreOptionsPopup()
            moreOptionPopup.setTargetFragment(this@HomeFragment, MoreOptionsPopup.MORE_OPTIONS_POPUP_CODE)
            val fragmentManager = parentFragmentManager.beginTransaction()
            moreOptionPopup.show(fragmentManager, "MoreOptionsPopup")
        }

        btnCalendar.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCalendarFragment())
        }

        btnNewTask.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToToDoEditFragment())
        }

        btnHighPriority.setOnClickListener {  }
        btnInSchedule.setOnClickListener {  }
        btnToday.setOnClickListener {  }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == MoreOptionsPopup.MORE_OPTIONS_POPUP_CODE) {
            if(resultCode == MoreOptionsPopup.MORE_OPTIONS_POPUP_ACCEPTED_CODE) {
                val option = data?.getIntExtra(MoreOptionsPopup.MORE_OPTIONS_POPUP_OPTION, -1) ?: -1
                when (option) {
                    MoreOptionsPopup.MORE_OPTIONS_POPUP_DARK_MODE_UPDATED -> {
                        AppCompatDelegate.setDefaultNightMode(if (settings.isDarkModeOn()) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
                    }
                    MoreOptionsPopup.MORE_OPTIONS_POPUP_OPEN_ABOUT_VIEW -> {
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAboutFragment())
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}
