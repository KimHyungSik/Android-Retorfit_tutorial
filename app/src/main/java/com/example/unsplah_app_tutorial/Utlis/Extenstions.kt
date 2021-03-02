package com.example.unsplah_app_tutorial.Utlis

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import com.example.unsplah_app_tutorial.Utlis.Constants.TAG
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import java.text.SimpleDateFormat
import java.util.*

// 문자열이 Json형태인지, Json배열 형태인지
//fun String?.isJsonObject():Boolean{
//    if(this?.startsWith("{") == true && this.endsWith("}")){
//        return true
//    }
//    return false
//}


// 날짜 포맷
fun Date.toSimpleString() : String{
    val format = SimpleDateFormat("HH:mm:ss")
    return format.format(this)
}

fun String?.isJsonObject():Boolean = this?.startsWith("{") == true && this.endsWith("}")

fun String?.isJsonArray():Boolean = this?.startsWith("[") == true && this.endsWith("]")

// 에딧 텍스트에 대한 익스텐션
fun EditText.onMyTextChange(completion: (Editable?) -> Unit){
    this.addTextChangedListener(object : TextWatcher{

        override fun afterTextChanged(s: Editable?) {
            completion(s)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    })
}

// 에딧텍스트 텍스트 변경을 flow로 받기
@ExperimentalCoroutinesApi
fun EditText.textChangeToFlow(): Flow<CharSequence?>{

    // flow 콜백 받기
    return callbackFlow {
        val listener = object : TextWatcher{
            override fun afterTextChanged(s: Editable?) = Unit


            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit


            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d(TAG, " - onTextChanged()")
                // 값 내보내기
                offer(text)
            }
        }

        // 위에서 설정한 리스너 달아주기
        addTextChangedListener(listener)

        // 콜백이 사라질때 실행됨
        awaitClose {
            Log.d(TAG, " - textChangeToFlow() / awaitClose()")
            removeTextChangedListener(listener)
        }

    }.onStart{
        Log.d(TAG, " - textChangeToFlow()")
        // Rx 에서 onNect 와 동일
        // emit 으로 이벤트를 전달
        emit(text)
    }
}