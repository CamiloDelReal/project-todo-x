package org.xapps.apps.todox.views.fragments

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import org.xapps.apps.todox.R
import org.xapps.apps.todox.viewmodels.SplashViewModel
import org.xapps.apps.todox.viewmodels.ViewModelFactory
import javax.inject.Inject


class SplashFragment @Inject constructor() : Fragment() {

    private lateinit var currentView: View

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: SplashViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        currentView = inflater.inflate(R.layout.fragment_splash, container, false)
        viewModel = viewModelFactory.create(SplashViewModel::class.java)
        return currentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler().postDelayed({
            context?.let {
 //               findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToHomeFragment())
            }
        }, 2000)
    }
}
