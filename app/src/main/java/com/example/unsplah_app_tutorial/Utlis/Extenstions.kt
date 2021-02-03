package com.example.unsplah_app_tutorial.Utlis

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

// 문자열이 Json형태인지, Json배열 형태인지
//fun String?.isJsonObject():Boolean{
//    if(this?.startsWith("{") == true && this.endsWith("}")){
//        return true
//    }
//    return false
//}

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