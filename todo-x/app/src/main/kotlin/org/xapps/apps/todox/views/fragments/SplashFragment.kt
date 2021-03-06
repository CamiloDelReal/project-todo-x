package org.xapps.apps.todox.views.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import org.xapps.apps.todox.core.utils.info
import org.xapps.apps.todox.databinding.FragmentSplashBinding
import org.xapps.apps.todox.viewmodels.SplashViewModel
import org.xapps.apps.todox.views.utils.Message
import javax.inject.Inject


@AndroidEntryPoint
class SplashFragment @Inject constructor() : Fragment() {

    private lateinit var bindings: FragmentSplashBinding

    private val viewModel: SplashViewModel by viewModels()

    private var messageJob: Job? = null

    private val onBackPressedCallback: OnBackPressedCallback by lazy {
        object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindings = FragmentSplashBinding.inflate(layoutInflater)
        bindings.lifecycleOwner = viewLifecycleOwner
        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        messageJob = lifecycleScope.launchWhenResumed {
            viewModel.messageFlow
                .collect {
                    info<SplashFragment>("Message received $it")
                    if(it is Message.Success) {
                        findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToHomeFragment())
                    } else {

                    }
                }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            context?.let {
                viewModel.prepareApp()
            }
        }, 2000)
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        messageJob?.cancel()
        messageJob = null
    }
}
