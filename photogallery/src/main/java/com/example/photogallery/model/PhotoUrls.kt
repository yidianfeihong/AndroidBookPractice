package com.example.photogallery.model

import java.io.Serializable

data class PhotoUrls(
    val original: String,
    val tiny: String,
    val small: String,
    val medium: String,
    val large: String,
) : Serializable