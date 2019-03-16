package com.hellmund.transport.ui.edit.di

import android.content.Context
import com.hellmund.transport.data.api.GoogleMapsAPI
import com.hellmund.transport.data.repos.SuggestionsRepository
import dagger.Module
import dagger.Provides

@Module
class EditModule(private val context: Context) {

    @Provides
    fun provideSuggestionsRepository(
            mapsApi: GoogleMapsAPI
    ): SuggestionsRepository {
        return SuggestionsRepository(mapsApi)
    }

}
