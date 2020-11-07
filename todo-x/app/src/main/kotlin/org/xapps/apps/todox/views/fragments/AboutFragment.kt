package org.xapps.apps.todox.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import dagger.hilt.android.AndroidEntryPoint
import org.xapps.apps.todox.BuildConfig
import org.xapps.apps.todox.databinding.FragmentAboutBinding
import javax.inject.Inject


@AndroidEntryPoint
class AboutFragment @Inject constructor(): Fragment() {

    private lateinit var binding: FragmentAboutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAboutBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.version = BuildConfig.VERSION_NAME
        ViewCompat.setTranslationZ(binding.root, 2f)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}