package com.ryu.dicodingeventsv2.data

import android.util.Log
import retrofit2.Response

class EventRepository(private val apiService: ApiService) {

    suspend fun getEventDetail(eventId: String): Response<EventResponse> {
        Log.d("EventRepository", "Fetching event detail for ID: $eventId")
        val response = apiService.getEventDetail(eventId)
        Log.d("EventRepository", "Received response: $response")
        Log.d("EventRepository", "Response body: ${response.body()}")
        return response
    }

}
