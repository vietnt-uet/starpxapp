package com.starpx.model

data class ImageSetSummary(
    val caption: String,
    val set_id: String,
    val state: String,
    val image_detail: ImageDetail
)