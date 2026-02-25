package com.joystick.app.data.remote

import com.joystick.app.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * OkHttp interceptor that appends the RAWG API key as a "key" query parameter
 * to every outgoing request.
 *
 * Injected via Hilt — provided to OkHttpClient in [com.joystick.app.di.NetworkModule].
 */
class ApiKeyInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val url = original.url.newBuilder()
            .addQueryParameter("key", BuildConfig.RAWG_API_KEY)
            .build()
        val request = original.newBuilder()
            .url(url)
            .build()
        return chain.proceed(request)
    }
}
