package com.pharmatech.morocco.core.network

import com.pharmatech.morocco.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

class AuthInterceptor : Interceptor {
    private var authToken: String? = null

    fun setAuthToken(token: String?) {
        this.authToken = token
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        authToken?.let { token ->
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        requestBuilder
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")

        if (BuildConfig.AI_API_KEY.isNotBlank()) {
            // Provide header required by symptom checker backend when key configured
            requestBuilder.addHeader("x-api-key", BuildConfig.AI_API_KEY)
        }

        val request = requestBuilder.build()
        Timber.d("API Request: ${request.method} ${request.url}")

        return chain.proceed(request)
    }
}

