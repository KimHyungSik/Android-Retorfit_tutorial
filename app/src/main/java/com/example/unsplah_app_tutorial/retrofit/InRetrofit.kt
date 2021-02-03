package com.example.unsplah_app_tutorial.retrofit


import com.example.unsplah_app_tutorial.Utlis.API
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface InRetrofit {
    //"baseUrl/search/photos/?query='searchTerm'"
    @GET(API.SEARCH_PHOTO)
    fun searchPhotos(@Query("query") searchTerm: String) : Call<JsonElement>

    @GET(API.SEARCH_USER)
    fun searchUsers(@Query("query") searchTerm: String) : Call<JsonElement>

}