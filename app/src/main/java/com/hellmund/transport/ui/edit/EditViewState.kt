package com.hellmund.transport.ui.edit

import com.hellmund.transport.data.persistence.Destination

sealed class Action {
    data class TitleChanged(val title: String) : Action()
    data class AddressChanged(val address: String) : Action()
    object HideSuggestions : Action()
    object SaveButtonClicked : Action()
}

sealed class Result {
    object EnableSave : Result()
    object DisableSave : Result()
    object ShowLoading : Result()
    data class SuggestionsLoaded(val suggestions: List<String>) : Result()
    object SuggestionsLoadingError : Result()
    object HideSuggestions : Result()
    object None : Result()
}

sealed class NavigationResult {
    object Close : NavigationResult()
    object ShowDialog : NavigationResult()
}

data class EditViewState(
        val title: String? = null,
        val address: String? = null,
        val isLoading: Boolean = false,
        val isSaveEnabled: Boolean = false,
        val suggestions: List<String> = emptyList(),
        val showSuggestions: Boolean = false,
        val showNameSuggestions: Boolean = false
) {

    fun toLoading(): EditViewState {
        return copy(isLoading = true)
    }

    fun toData(suggestions: List<String>): EditViewState {
        return copy(suggestions = suggestions, showSuggestions = true, isLoading = false)
    }

    fun toNoData(): EditViewState {
        return copy(showSuggestions = false)
    }

    fun toSaveEnabled(isEnabled: Boolean): EditViewState {
        return copy(isSaveEnabled = isEnabled)
    }

    fun toError(): EditViewState {
        return copy(isLoading = false)
    }

    companion object {

        fun initial(destination: Destination?): EditViewState {
            return EditViewState(
                    title = destination?.title,
                    address = destination?.address,
                    showNameSuggestions = destination == null
            )
        }

    }

}
