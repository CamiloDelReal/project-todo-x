package org.xapps.apps.todox.views.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
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
    ): View {
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

        bindings.btnLinkMaterialDesignIcons.setOnClickListener {
            launchUri(getString(R.string.material_design_icons_url))
        }

        bindings.btnLinkFlatUIColors.setOnClickListener {
            launchUri(getString(R.string.flatuicolors_url))
        }

        bindings.btnLinkAndroidJetpack.setOnClickListener {
            launchUri(getString(R.string.android_jetpack_url))
        }

        bindings.btnLinkAndroidKotlin.setOnClickListener {
            launchUri(getString(R.string.android_kotlin_url))
        }

        bindings.btnLinkDexterPermissions.setOnClickListener {
            launchUri(getString(R.string.dexter_url))
        }

        bindings.btnLinkDrawableToolbox.setOnClickListener {
            launchUri(getString(R.string.drawabletoolbox_url))
        }

        bindings.btnLinkCircularProgressBar.setOnClickListener {
            launchUri(getString(R.string.circularprogressbar_url))
        }

        bindings.btnLinkMaterialProgressBar.setOnClickListener {
            launchUri(getString(R.string.materialprogressbar_url))
        }

        bindings.btnLinkSwipeLayout.setOnClickListener {
            launchUri(getString(R.string.swipelayout_url))
        }

        bindings.btnLinkFlowLayout.setOnClickListener {
            launchUri(getString(R.string.flowlayout_url))
        }

        bindings.btnLinkCalendarView.setOnClickListener {
            launchUri(getString(R.string.calendarview_url))
        }

        bindings.btnLinkWhatIf.setOnClickListener {
            launchUri(getString(R.string.whatif_url))
        }

        bindings.btnLinkToasty.setOnClickListener {
            launchUri(getString(R.string.toasty_url))
        }

        bindings.btnLinkTimber.setOnClickListener {
            launchUri(getString(R.string.timber_url))
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