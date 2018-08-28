package com.hellmund.transport.ui.edit

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.hellmund.transport.data.persistence.Destination

class EditDestinationActivityViewModelFactory(
        private val application: Application,
        private val destination: Destination?
) : ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditDestinationActivityViewModel(application, destination) as T
    }

}