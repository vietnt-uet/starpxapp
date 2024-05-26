package com.starpx.datasource

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import com.starpx.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request

class ApolloClientConfig {

    companion object {
        private const val BASE_URL = BuildConfig.API_ENDPOINT
        private const val API_KEY = BuildConfig.API_KEY

        fun getApolloClient(idToken: String): ApolloClient {
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(Interceptor { chain ->
                    val original: Request = chain.request()
                    val requestBuilder: Request.Builder = original.newBuilder()
                        .header("Content-type", "application/json")

                    //Either use JWT token or api key, not both.
                    if (idToken.isNotEmpty()) {
                        requestBuilder.header("Authorization", "Bearer $idToken")
                    } else {
                        requestBuilder.header("x-api-key", API_KEY)
                    }

                    val request: Request = requestBuilder.build()
                    chain.proceed(request)
                })
                .build()

            return ApolloClient.Builder()
                .serverUrl(BASE_URL)
                .okHttpClient(okHttpClient)
                .build()
        }
    }
}