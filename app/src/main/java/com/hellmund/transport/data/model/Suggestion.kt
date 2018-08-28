package com.hellmund.transport.data.model

import com.google.gson.annotations.SerializedName

data class Suggestion(
        @SerializedName("description") val address: String?,
        @SerializedName("place_id") val placeId: String?
)