package com.starpx.data

data class ImageSetSummaryCursor(
    val nextToken: String?,
    val image_sets: List<ImageSetSummary>
)