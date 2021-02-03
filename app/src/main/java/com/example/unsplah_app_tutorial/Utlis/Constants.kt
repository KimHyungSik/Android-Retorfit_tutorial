package com.example.unsplah_app_tutorial.Utlis

object  Constants{
    const val TAG: String = "로그"
}

object API{
    const val BASE_URL = "https://api.unsplash.com/"

    const val CLIENT_ID = "hGHA3Gg2icnyjDKRxioRcvGicPyBjEM0e_WpduQzxUM"

    const val  SEARCH_PHOTO = "search/photos/"
    const val  SEARCH_USER = "/search/users"
}

enum class RESPONSE_STATE{
    OKAY,
    FAIL
}

enum class SEARCH_TYPE{
    PHOTO,
    USER
}