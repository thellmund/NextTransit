package com.hellmund.transport.data.repos

import com.hellmund.transport.data.api.GoogleMapsAPI
import com.hellmund.transport.data.model.Suggestion
import io.reactivex.Maybe
import javax.inject.Inject

class SuggestionsRepository @Inject constructor(
        private val mapsApi: GoogleMapsAPI
) {

    fun getSuggestions(input: String): Maybe<List<Suggestion>> {
        return mapsApi
                .queryAddresses(input)
                .map { it.suggestions }
                .onErrorReturn { emptyList() }
    }

}