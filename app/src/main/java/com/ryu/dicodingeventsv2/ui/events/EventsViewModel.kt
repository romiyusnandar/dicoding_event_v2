package com.ryu.dicodingeventsv2.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryu.dicodingeventsv2.data.ApiConfig
import com.ryu.dicodingeventsv2.data.ListEventsItem
import kotlinx.coroutines.launch

class EventViewModel : ViewModel() {

    private val _events = MutableLiveData<List<ListEventsItem>>()
    val events: LiveData<List<ListEventsItem>> = _events

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchEvents() {
        _isLoading.value = true
        val apiService = ApiConfig.getApiService()
        Log.d("EventViewModel", "Fetching events $apiService")
        viewModelScope.launch {
            try {
                val response = apiService.getActiveEvents()
                _isLoading.value = false
                if (response.isSuccessful) {
                    _events.value = response.body()?.listEvents?.filterNotNull()
                } else {
                    _errorMessage.value = "Error: ${response.message()}"
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Failure: ${e.message}"
            }
        }
    }
}
