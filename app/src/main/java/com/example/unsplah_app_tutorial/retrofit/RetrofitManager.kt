package com.example.unsplah_app_tutorial.retrofit

import android.util.Log
import com.example.unsplah_app_tutorial.Utlis.API.BASE_URL
import com.example.unsplah_app_tutorial.Utlis.Constants.TAG
import com.example.unsplah_app_tutorial.Utlis.RESPONSE_STATE
import com.example.unsplah_app_tutorial.retrofit.InRetrofit
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.Response
import retrofit2.create

class RetrofitManager {

    companion object{
        val instance = RetrofitManager()

    }

    // 레트로 핏 인터페이스 가져오기
    private val inRetrofit : InRetrofit? = RetrofitClient.getClient(BASE_URL)?.create(InRetrofit::class.java)

    //사진 검색 api 호출
    fun searchPhotos(searchTerm: String?, completrion:(RESPONSE_STATE,String) -> Unit){
        // searchTerm 비어있다면 ""를 반환
        val term = searchTerm ?: ""

        //가져온 결과가 비어있다면 return
        val call = inRetrofit?.searchPhotos(searchTerm = term) ?: return

        call.enqueue(object: retrofit2.Callback<JsonElement>{
            // 응답 실패 시
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "RetrofitManager - onFailure() / t: $t")
                completrion(RESPONSE_STATE.FAIL,t.toString())
            }

            // 응답 성공 시
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                Log.d(TAG, "RetrofitManager - onResponse() / response : ${response.raw()}")

                completrion(RESPONSE_STATE.OKAY,response.body().toString())
            }

        })
    }

}