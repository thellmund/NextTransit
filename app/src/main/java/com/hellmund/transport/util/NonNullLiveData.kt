package com.hellmund.transport.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class NonNullLiveData<T> : MutableLiveData<T>() {

    fun observe(owner: LifecycleOwner, callback: (T) -> Unit) {
        observe(owner, Observer<T> { result ->
            result?.let { callback(it) }
        })
    }

}
