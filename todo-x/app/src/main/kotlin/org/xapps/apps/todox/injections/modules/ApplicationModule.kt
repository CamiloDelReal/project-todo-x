package org.xapps.apps.todox.injections.modules

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module(
    includes = [SettingsModule::class, LocalStorageServiceModule::class, FragmentBindingModule::class, ViewModelModule::class]
)
class ApplicationModule {

    @Singleton
    @Provides
    fun provideContext(application: Application): Context =
        application.applicationContext

}