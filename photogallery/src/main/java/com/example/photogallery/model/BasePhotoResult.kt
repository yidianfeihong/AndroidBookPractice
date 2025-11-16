package com.example.photogallery.model

import com.google.gson.annotations.SerializedName

class BasePhotoResult(
    val photos: List<PhotoDetail>,
    val page: Int,
    @SerializedName("per_page")
    val perPage: Int,
    @SerializedName("next_page")
    val nextPage: String,
)