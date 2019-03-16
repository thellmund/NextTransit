package com.hellmund.transport.di

import com.hellmund.transport.ui.destinations.di.DestinationsComponent
import com.hellmund.transport.ui.edit.di.EditComponent
import com.hellmund.transport.ui.onboarding.IntroductionActivity
import com.hellmund.transport.ui.route.RouteActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun destinationsComponent(): DestinationsComponent.Builder
    fun editComponent(): EditComponent.Builder
    fun inject(introductionActivity: IntroductionActivity)
    fun inject(routeActivity: RouteActivity)
}
