package com.hellmund.transport.data.api

import com.hellmund.transport.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.util.*

class GoogleMapsInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val originalUrl = original.url()

        val url = originalUrl.newBuilder()
                .addQueryParameter("key", BuildConfig.API_KEY)
                .addQueryParameter("language", Locale.getDefault().language)
                .build()

        val request = original.newBuilder()
                .url(url)
                .build()
        return chain.proceed(request)
    }

}