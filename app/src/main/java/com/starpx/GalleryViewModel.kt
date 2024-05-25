package com.starpx
import GetImageSetSummariesQuery
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.Optional
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GalleryViewModel : ViewModel() {
    val apolloClient = ApolloClientConfig.getApolloClient("eyJraWQiOiJiVlIxSnJtZzlCelwvc0taMmVNcUh4S2Qwc3VxMGNTMG5JVHRVNWtLSTFlbz0iLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIwYWVmZWFjNC1lYzBmLTRiMGQtOTM5ZS04M2Y0MjAzNDU5YWYiLCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAuZXUtY2VudHJhbC0xLmFtYXpvbmF3cy5jb21cL2V1LWNlbnRyYWwtMV9PVzBnNjFrRWsiLCJjbGllbnRfaWQiOiIzNGZiamlldWtwZGFxN202cTM1Z2UxMGVpIiwib3JpZ2luX2p0aSI6ImNjYmE5MmQ2LWM0MzQtNGQ2Ni1hOWFkLTYyMWNmOTRmZTY4OSIsImV2ZW50X2lkIjoiNzMwOThjMWUtNTgyMC00MDJlLTg5MmItYjAwNmRlMTdjYjFiIiwidG9rZW5fdXNlIjoiYWNjZXNzIiwic2NvcGUiOiJhd3MuY29nbml0by5zaWduaW4udXNlci5hZG1pbiIsImF1dGhfdGltZSI6MTcxNjY0MjUwNCwiZXhwIjoxNzE2NjQ2MTA0LCJpYXQiOjE3MTY2NDI1MDQsImp0aSI6ImNlZmUyNTQ1LWI4YWUtNDQ4Ny05YWMyLWU3NTRhM2E2NmRhYiIsInVzZXJuYW1lIjoiMGFlZmVhYzQtZWMwZi00YjBkLTkzOWUtODNmNDIwMzQ1OWFmIn0.BXk8dp_KVz7zLBAyMfHbSo2I9K8NUxg1sLRou2ZYGY43LZXKEZItWhlmk1ccVaTV2gxcPfaBp8b_-MEc-vzq85kHYCjAiutyFL_OSWHdnXCKtbAXD3ipCr-VnMC860g1sfqPo7upqyQaE6kvU3VmcSX7DNW83NYsMrUj4tiUIq43C-eYAfJSWOC0QURsdp2aeqYJ51nOSxCeLA7IznilJYD5exIyxw3iEnS0to6DK2TJnlD-HZXrIuPF46TO2pdR-7QlKNjVt002YP_Xzuf8_oEda-GTxsslyh0uo0I6Yaq5BSJc0cpHLDTUo1qandqgL9t15-tbvEUYINMrn1bVyw")

    private var _imageSets = MutableStateFlow<List<GetImageSetSummariesQuery.Image_set>>(emptyList())
    val imageSets: StateFlow<List<GetImageSetSummariesQuery.Image_set>> = _imageSets

    var nextToken: String? = null

    init {
        fetchImageSets(null)
    }

    fun fetchImageSets(_nextToken: String?) {
        viewModelScope.launch {
            try {
                val response = apolloClient.query(
                    GetImageSetSummariesQuery(
                        customerId = "aabb1234",
                        limit = Optional.Present(30),
                        nextToken = Optional.Present(_nextToken)
                    )
                ).execute()

                response.data?.getImageSetSummaries?.let {
                    nextToken = it.nextToken
                    _imageSets.update { currentImageSets ->
                        currentImageSets + it.image_sets?.filterNotNull().orEmpty()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}