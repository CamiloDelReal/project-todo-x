package org.xapps.apps.todox.views.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.xapps.apps.todox.BuildConfig
import org.xapps.apps.todox.R
import org.xapps.apps.todox.databinding.FragmentAboutBinding
import javax.inject.Inject


@AndroidEntryPoint
class AboutFragment @Inject constructor(): Fragment() {

    private lateinit var bindings: FragmentAboutBinding

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
        bindings = FragmentAboutBinding.inflate(layoutInflater)
        bindings.lifecycleOwner = viewLifecycleOwner
        bindings.version = BuildConfig.VERSION_NAME
        ViewCompat.setTranslationZ(bindings.root, 2f)
        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindings.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        bindings.btnAboutLink.setOnClickListener {
            launchUri(getString(R.string.project_github))
        }

        bindings.btnLinkGoogleFonts.setOnClickListener {
            launchUri(getString(R.string.poppins_url))
        }
    }

    private fun launchUri(uri: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }
}