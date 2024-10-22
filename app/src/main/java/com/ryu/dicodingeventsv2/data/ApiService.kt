package com.ryu.dicodingeventsv2.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    suspend fun getActiveEvents(
        @Query("active") active: Int = -1
    ): Response<EventResponse>

    @GET("events/{id}")
    suspend fun getEventDetail(
        @Path("id") id: String
    ): Response<EventResponse>
}