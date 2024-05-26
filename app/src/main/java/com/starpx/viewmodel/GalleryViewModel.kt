package com.starpx.viewmodel
import GetImageSetSummariesQuery
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.starpx.datasource.ApolloClientConfig
import com.starpx.utils.PreferenceUtil
import com.starpx.utils.KEY_CUSTOMER_ID
import com.starpx.utils.LifecycleHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val GALLERY_PAGE_SIZE = 30

class GalleryViewModel : ViewModel() {
    private var apolloClient: ApolloClient? = null

    private var _imageSets = MutableStateFlow<List<GetImageSetSummariesQuery.Image_set>>(emptyList())
    val imageSets: StateFlow<List<GetImageSetSummariesQuery.Image_set>> = _imageSets

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    var nextToken: String? = null

    fun initializeApolloClient(idToken: String) {
        apolloClient = ApolloClientConfig.getApolloClient(idToken)
        fetchImageSets(null)
    }

    fun fetchImageSets(nextToken: String?) {
        val customerId = PreferenceUtil.getInstance(LifecycleHelper.getInstance().appContext).getString(
            KEY_CUSTOMER_ID) ?: ""
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apolloClient?.query(
                    GetImageSetSummariesQuery(
                        customerId = customerId,
                        limit = Optional.Present(GALLERY_PAGE_SIZE),
                        nextToken = Optional.Present(nextToken)
                    )
                )?.execute()

                response?.data?.getImageSetSummaries?.let {
                    this@GalleryViewModel.nextToken = it.nextToken
                    _imageSets.update { currentImageSets ->
                        currentImageSets + it.image_sets?.filterNotNull().orEmpty()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}