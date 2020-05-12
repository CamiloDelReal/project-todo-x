package org.xapps.apps.todox.injections.modules

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import org.xapps.apps.todox.injections.utils.FragmentKey
import org.xapps.apps.todox.injections.utils.InjectingFragmentFactory
import org.xapps.apps.todox.injections.utils.InjectingNavHostFragment
import org.xapps.apps.todox.views.activities.MainActivity
import org.xapps.apps.todox.views.fragments.SplashFragment


@Module
abstract class FragmentBindingModule {

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun navHostFragmentInjector(): InjectingNavHostFragment

    @Binds
    abstract fun bindFragmentFactory(factory: InjectingFragmentFactory): FragmentFactory

    @Binds
    @IntoMap
    @FragmentKey(SplashFragment::class)
    abstract fun bindSplashFragment(splashFragment: SplashFragment): Fragment

}