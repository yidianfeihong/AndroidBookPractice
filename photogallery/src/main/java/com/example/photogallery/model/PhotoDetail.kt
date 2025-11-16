package com.example.photogallery.model

import com.google.gson.annotations.SerializedName

data class PhotoDetail(
    val id: Long,
    val width: String,
    val height: String,
    //这个是图片的整体介绍页
    @SerializedName("url")
    val linkPageUrl: String,
    //这里是图片的下载链接
    @SerializedName("src")
    val downloadUrls: PhotoUrls
)