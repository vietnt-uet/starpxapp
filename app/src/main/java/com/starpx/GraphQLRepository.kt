package com.starpx

import GetImageSetSummariesQuery
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException

class GraphQLRepository(private val apolloClient: ApolloClient) {

    suspend fun getImageSetSummaries(customerId: String, limit: Int, nextToken: String?): GetImageSetSummariesQuery.Data? {
        val response = try {
            apolloClient.query(GetImageSetSummariesQuery(customerId, Optional.presentIfNotNull(limit), Optional.presentIfNotNull(nextToken))).execute()
        } catch (e: ApolloException) {
            // Handle error
            null
        }
        return response?.data
    }
}