package com.example.unsplah_app_tutorial.retrofit

import android.annotation.SuppressLint
import android.util.Log
import com.example.unsplah_app_tutorial.Model.Photo
import com.example.unsplah_app_tutorial.Utlis.API.BASE_URL
import com.example.unsplah_app_tutorial.Utlis.Constants.TAG
import com.example.unsplah_app_tutorial.Utlis.RESPONSE_STATUS
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat

class RetrofitManager {

    companion object{
        val instance = RetrofitManager()

    }

    // 레트로 핏 인터페이스 가져오기
    private val inRetrofit : InRetrofit? = RetrofitClient.getClient(BASE_URL)?.create(InRetrofit::class.java)

    //사진 검색 api 호출
    fun searchPhotos(searchTerm: String?, completion:(RESPONSE_STATUS, ArrayList<Photo>?) -> Unit){
        // searchTerm 비어있다면 ""를 반환
        val term = searchTerm ?: ""

        //가져온 결과가 비어있다면 return
        val call = inRetrofit?.searchPhotos(searchTerm = term) ?: return

        call.enqueue(object: retrofit2.Callback<JsonElement>{
            // 응답 실패 시
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(TAG, "RetrofitManager - onFailure() / t: $t")
                completion(RESPONSE_STATUS.FAIL, null)
            }

            // 응답 성공 시
            @SuppressLint("SimpleDateFormat")
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                Log.d(TAG, "RetrofitManager - onResponse() / response : ${response.raw()}")

                when(response.code()){
                    200->{
                        response.body()?.let{

                            val parsedPhotoDataArray = ArrayList<Photo>()

                            val body = it.asJsonObject
                            // 가져오는 Json 결과 배열
                            val results : JsonArray = body.getAsJsonArray("results")
                            // 가져온 Json 결과 총 개수
                            val toatl = body.get("total").asInt

                            if(toatl == 0){
                                completion(RESPONSE_STATUS.NO_CONTENT,null)
                            }else {

                                //데이터 파싱
                                results.forEach { resultItem ->
                                    val resultItemObject = resultItem.asJsonObject
                                    val user = resultItemObject.get("user").asJsonObject
                                    val userName: String = user.get("username").asString
                                    val likeCounts = resultItemObject.get("likes").asInt
                                    val thumbnailLink =
                                        resultItemObject.get("urls").asJsonObject.get("thumb").asString
                                    val createdAt = resultItemObject.get("created_at").asString

                                    val parser = SimpleDateFormat("yyy-MM-dd'T'HH:mm:ss")
                                    val formatter = SimpleDateFormat("yyyy년\nMM월 dd일")

                                    val fromDateStirng: String? =
                                        formatter.format(parser.parse(createdAt))

                                    val photoItem = Photo(
                                        author = userName,
                                        likesCount = likeCounts,
                                        thumbnail = thumbnailLink,
                                        createdAt = fromDateStirng
                                    )
                                    //배열 추가
                                    parsedPhotoDataArray.add(photoItem)
                                }
                                completion(RESPONSE_STATUS.OKAY, parsedPhotoDataArray)
                            }
                        }
                    }
                }

            }

        })
    }

}