package com.hellmund.transport.ui.edit.di

import com.hellmund.transport.data.persistence.Destination
import com.hellmund.transport.ui.edit.EditDestinationActivity
import dagger.BindsInstance
import dagger.Subcomponent

@Subcomponent(modules = [EditModule::class])
interface EditComponent {

    fun inject(editActivity: EditDestinationActivity)

    @Subcomponent.Builder
    interface Builder {

        fun editModule(editModule: EditModule): EditComponent.Builder

        @BindsInstance
        fun destination(@EditDestination destination: Destination?): EditComponent.Builder

        fun build(): EditComponent

    }

}
