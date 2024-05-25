package com.starpx.model

data class ImageSetSummaryCursor(
    val nextToken: String?,
    val image_sets: List<ImageSetSummary>
)