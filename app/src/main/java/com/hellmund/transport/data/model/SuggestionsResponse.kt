package com.hellmund.transport.data.model

import com.google.gson.annotations.SerializedName

data class SuggestionsResponse(
        @SerializedName("predictions") val suggestions: List<Suggestion>
)