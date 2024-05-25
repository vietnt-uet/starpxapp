package com.starpx

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request

class ApolloClientConfig {

    companion object {
        private const val BASE_URL = "https://api-dev.starpx.com/graphql"
        private const val API_KEY = "da2-65pvpzhg45clljvctl22t37nvq"

        fun getApolloClient(idToken: String): ApolloClient {
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(Interceptor { chain ->
                    val original: Request = chain.request()
                    val requestBuilder: Request.Builder = original.newBuilder()
                        .header("Content-type", "application/json")
//                        .header("x-api-key", API_KEY)
                        .header("Authorization", "Bearer $idToken")
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