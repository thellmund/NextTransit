package com.hellmund.transport.ui.edit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.hellmund.transport.data.persistence.Destination
import com.hellmund.transport.data.repos.DestinationsRepository
import com.hellmund.transport.data.repos.SuggestionsRepository
import org.jetbrains.anko.doAsync

class EditDestinationActivityViewModel(
        application: Application,
        private val destination: Destination?
) : AndroidViewModel(application) {

    private val suggestionsRepo = SuggestionsRepository()
    private val destinationsRepo = DestinationsRepository(application.applicationContext)

    var titleInput = destination?.title.orEmpty()
    var addressInput = destination?.address.orEmpty()

    val isAddAction: Boolean
        get() = destination == null

    fun getSuggestions(input: String) = suggestionsRepo.getSuggestions(input)

    fun saveDestination() {
        saveDestination(titleInput, addressInput)
    }

    fun handleOnBackPressed() = !hasMadeChanges()

    private fun hasMadeChanges(): Boolean {
        if (isAddAction) {
            // The user has entered at least some information
            return titleInput.isNotBlank() || addressInput.isNotBlank()
        }

        val changedName = destination?.title != titleInput
        val changedAddress = destination?.address != addressInput
        return changedName || changedAddress
    }

    val isSaveButtonEnabled: Boolean
        get() = titleInput.isNotBlank() && addressInput.isNotBlank()

    private fun saveDestination(title: String, address: String) {
        if (destination != null) {
            updateDestination(title, address)
        } else {
            createDestination(title, address)
        }
    }

    private fun updateDestination(title: String, address: String) {
        doAsync {
            destination?.updateWithInput(title, address)
            destination?.let { newDestination ->
                destinationsRepo.updateDestination(newDestination)
            }
        }
    }

    private fun createDestination(title: String, address: String) {
        doAsync {
            val position = destinationsRepo.size()
            val newDestination = Destination.create(position, title, address)
            destinationsRepo.insertDestination(newDestination)
        }
    }

}