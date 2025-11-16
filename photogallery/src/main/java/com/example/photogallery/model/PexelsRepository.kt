package com.example.photogallery.model

import com.example.photogallery.api.PexelsApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PexelsRepository {
    private val pexelsApi: PexelsApi

    companion object {
        private const val BASE_URL = "https://api.pexels.com/"
        private const val API_KEY = "L4Q5VqJa3jgi1ThOQkpyik6HENp2t0AEd4JWBCrFeNmE8YUhDjsHPFne"
    }

    init {
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val request = chain.request()
            val newRequest = request.newBuilder()
                .header("Authorization", API_KEY)
                .build()
            chain.proceed(newRequest)
        }.build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        pexelsApi = retrofit.create(PexelsApi::class.java)
    }

    suspend fun searchPhotos(query: String): BasePhotoResult {
        return pexelsApi.searchPhoto(query)
    }

    suspend fun fetchPhotos(): BasePhotoResult {
        return pexelsApi.loadCuratedPhotos()
    }

    suspend fun loadPhotoDetail(id: Int): PhotoDetail {
        return pexelsApi.loadPhotoDetail(id)
    }
}