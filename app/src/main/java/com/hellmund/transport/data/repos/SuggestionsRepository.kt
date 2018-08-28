package com.hellmund.transport.data.repos

import android.util.Log
import com.hellmund.transport.data.model.Suggestion
import com.hellmund.transport.data.model.SuggestionsResponse
import com.hellmund.transport.data.api.GoogleMapsClient
import com.hellmund.transport.util.NonNullLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SuggestionsRepository {

    fun getSuggestions(input: String): NonNullLiveData<List<Suggestion>> {
        val data = NonNullLiveData<List<Suggestion>>()
        GoogleMapsClient
                .mapsApi
                .queryAddresses(input)
                .enqueue(object : Callback<SuggestionsResponse> {
                    override fun onResponse(call: Call<SuggestionsResponse>,
                                            response: Response<SuggestionsResponse>) {
                        val body = response.body() ?: return
                        data.postValue(body.suggestions)
                    }

                    override fun onFailure(call: Call<SuggestionsResponse>, t: Throwable) {
                        Log.d(logTag, "Error while downloading suggestions", t)
                        data.postValue(emptyList())
                    }
                })
        return data
    }

    companion object {
        private val logTag = SuggestionsRepository::class.java.simpleName
    }

}