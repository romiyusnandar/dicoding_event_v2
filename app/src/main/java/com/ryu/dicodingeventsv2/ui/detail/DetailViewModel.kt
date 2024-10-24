package com.ryu.dicodingeventsv2.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryu.dicodingeventsv2.data.ApiService
import com.ryu.dicodingeventsv2.data.EventRepository
import com.ryu.dicodingeventsv2.data.ListEventsItem
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: EventRepository) : ViewModel() {

    private val _eventDetail = MutableLiveData<ListEventsItem?>()
    val eventDetail: LiveData<ListEventsItem?> = _eventDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchEventDetail(eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("DetailViewModel", "Fetching event detail for ID: $eventId")
                val response = repository.getEventDetail(eventId)
                Log.d("DetailViewModel", "Received event detail: $response")
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("DetailViewModel", "Response body: $responseBody")
                    if (responseBody?.error == false) {
                        val event = responseBody.listEvents?.firstOrNull()
                        Log.d("DetailViewModel", "Parsed event: $event")
                        _isLoading.value = false
                        _eventDetail.postValue(event)
                    } else {
                        _errorMessage.value = "Error: ${responseBody?.message}"
                        Log.e("DetailViewModel", "API Error: ${responseBody?.message}")
                    }
                } else {
                    _errorMessage.value = "Error: ${response.message()}"
                    Log.e("DetailViewModel", "HTTP Error: ${response.message()}")
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Failure: ${e.message}"
                Log.e("DetailViewModel", "Exception: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

}