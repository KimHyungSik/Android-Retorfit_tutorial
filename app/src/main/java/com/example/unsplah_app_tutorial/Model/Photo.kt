package com.example.unsplah_app_tutorial.Model

import java.io.Serializable

data class Photo(var thumbnail: String?,
                 var author: String,
                 var createdAt: String?,
                 var likesCount: Int?) :
    // 번들로 보낼때 직렬화 가능하게 선언언
     Serializable {
}