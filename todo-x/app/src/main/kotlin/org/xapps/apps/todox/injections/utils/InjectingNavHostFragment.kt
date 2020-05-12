package org.xapps.apps.todox.injections.utils

import android.content.Context
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


@Suppress("ProtectedInFinal")
class InjectingNavHostFragment @Inject constructor() : NavHostFragment() {

    @Inject
    lateinit var daggerFragmentInjectionFactory: InjectingFragmentFactory

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        childFragmentManager.fragmentFactory = daggerFragmentInjectionFactory
        super.onCreate(savedInstanceState)
    }

}