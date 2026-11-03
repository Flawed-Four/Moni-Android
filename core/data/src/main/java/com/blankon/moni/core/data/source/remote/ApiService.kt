package com.blankon.moni.core.data.source.remote

import com.blankon.moni.core.data.model.response.SampleModelResponse
import retrofit2.http.GET

interface ApiService {
    @GET("codingresources/codingResources")
    suspend fun getListData(): List<SampleModelResponse>
}