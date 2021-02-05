package com.example.unsplah_app_tutorial.retrofit

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.unsplah_app_tutorial.App
import com.example.unsplah_app_tutorial.Utlis.API
import com.example.unsplah_app_tutorial.Utlis.Constants.TAG
import com.example.unsplah_app_tutorial.Utlis.isJsonArray
import com.example.unsplah_app_tutorial.Utlis.isJsonObject
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.concurrent.TimeUnit

// 싱글턴
object RetrofitClient {
    // 래트로 핏 클라이언트 선언
    private var retrofitClient : Retrofit? = null

    // 레트로 핏 클라이언트 가져오기
    fun getClient(baseUrl : String): Retrofit?{
        Log.d(TAG, "RetrofitClient - getClient()")

        // okhttp 인스턴스 생성
        val client = OkHttpClient.Builder()

        // 로그를 찍기 위해
        // 로깅 인터셉터 추가
        val loggingInterceptor = HttpLoggingInterceptor(object: HttpLoggingInterceptor.Logger{
            override fun log(message: String) {
                Log.d(TAG, "RetrofitClient - log() / message: $message")

                when{
                    message.isJsonObject()->{
                        Log.d(TAG, JSONObject(message).toString(4))
                    }
                    message.isJsonArray()->{
                        Log.d(TAG, JSONObject(message).toString(4))
                    }
                    else->{
                        try{
                            Log.d(TAG, JSONObject(message).toString(4))
                        }catch (e:Exception){
                            Log.d(TAG, message)
                        }
                    }
                }
            }
        })

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        // 위에서 설정한 로깅 인터셉터를 okhttp 클라이언트에 추가한다
        client.addInterceptor(loggingInterceptor)
        // 기본 파라미터 추가

        val baseParameterInterceptor : Interceptor = (object : Interceptor{
            override fun intercept(chain: Interceptor.Chain): Response {
                Log.d(TAG, "RetrofitClient - intercept()")
                // 오리지날 리퀘스트
                val originalRequest = chain.request()

                //클라이언트 아이디 쿼리 추가가
                //쿼리 파미터 추가하기
                val addedUrl = originalRequest.url.newBuilder()
                    .addQueryParameter("client_id", API.CLIENT_ID)
                    .build()

                val finalRequest = originalRequest.newBuilder()
                                                        .url(addedUrl)
                                                        .method(originalRequest.method, originalRequest.body)
                                                        .build()
//                return chain.proceed(finalRequest)
                val response = chain.proceed(finalRequest)
                if(response.code != 200){
                    //Http통신은 메인 UI스레드가 아니라서 메이스레드를 불러서 Toast메시지 사용
                    Handler(Looper.getMainLooper()).post{
                        Toast.makeText(App.instance,"${response.code}에러입니다", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                return response
            }
        })

        client.addInterceptor(baseParameterInterceptor)

        //커넥션 타임 아웃
        client.connectTimeout(10, TimeUnit.SECONDS)
        client.readTimeout(10, TimeUnit.SECONDS)
        client.writeTimeout(10, TimeUnit.SECONDS)
        client.retryOnConnectionFailure(true)

        if(retrofitClient == null){
            // 레트로 핏 빌더를 통해 인스턴스 생성
            retrofitClient = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                    // 위에서 설정한 클라이언트로 레트로핏 클라리언트를 설정한다
                .client(client.build())
                .build()
        }
        return retrofitClient
    }
}