package com.hellmund.transport.ui.destinations.di

import com.hellmund.transport.ui.destinations.MainActivity
import dagger.Subcomponent

@Subcomponent(modules = [DestinationsModule::class])
interface DestinationsComponent {

    fun inject(mainActivity: MainActivity)

    @Subcomponent.Builder
    interface Builder {

        fun destinationsModule(destinationsModule: DestinationsModule): DestinationsComponent.Builder

        fun build(): DestinationsComponent

    }

}

