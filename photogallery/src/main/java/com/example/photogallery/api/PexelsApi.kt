package com.example.photogallery.api

import com.example.photogallery.model.BasePhotoResult
import com.example.photogallery.model.PhotoDetail
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 由于书中Flickr网站国内无法访问，这里使用pexels替代，api文档参考：https://www.pexels.com/api/documentation/#photos
 * 其它类似网站：https://pixabay.com/zh/
 */
interface PexelsApi {

    @GET("v1/search")
    suspend fun searchPhoto(
        //The search query. Ocean, Tigers, Pears, etc.
        @Query("query") query: String,
        //The page number you are requesting. Default: 1
        @Query("page") page: Int? = null,
        //The number of results you are requesting per page. Default: 15 Max: 80
        @Query("per_page") perPage: Int = 30
    ): BasePhotoResult

    @GET("v1/curated")
    suspend fun loadCuratedPhotos(
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int = 30
    ): BasePhotoResult

    @GET("v1/photos")
    suspend fun loadPhotoDetail(@Query("id") id: Int): PhotoDetail

}