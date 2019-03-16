package com.hellmund.transport.di

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import javax.inject.Provider

class ViewModelFactory<T : ViewModel>(
        private val provider: Provider<T>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return provider.get() as T
    }

}

fun <T : ViewModel> AppCompatActivity.lazyViewModel(block: () -> Provider<T>): Lazy<T> = lazy {
    val factory = ViewModelFactory(block())
    ViewModelProviders.of(this, factory).get(block().get().javaClass)
}
