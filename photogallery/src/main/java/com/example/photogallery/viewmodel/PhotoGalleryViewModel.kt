package com.example.photogallery.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.photogallery.QueryPreferences
import com.example.photogallery.model.PexelsRepository
import com.example.photogallery.model.PhotoDetail
import kotlinx.coroutines.launch

class PhotoGalleryViewModel(private val app: Application) : AndroidViewModel(app) {

    private val repository = PexelsRepository()
    private val _galleryItemLiveData: MutableLiveData<List<PhotoDetail>> = MutableLiveData()
    val galleryItemLiveData: LiveData<List<PhotoDetail>> = _galleryItemLiveData

    private val mutableSearchTerm = MutableLiveData<String>()

    val searchTerm: String
        get() = mutableSearchTerm.value ?: ""

    init {
        val query = QueryPreferences.getStoredQuery(app)
        fetchPhotos(query)
    }

    fun fetchPhotos(query: String = "") {
        QueryPreferences.setStoredQuery(app, query)
        mutableSearchTerm.value = query
        viewModelScope.launch {
            val basePhotoResult = if (searchTerm.isBlank()) {
                repository.fetchPhotos()
            } else {
                repository.searchPhotos(searchTerm)
            }
            _galleryItemLiveData.value = basePhotoResult.photos
        }
    }
}