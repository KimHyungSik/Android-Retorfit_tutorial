package com.example.unsplah_app_tutorial.Utlis

import android.content.Context
import android.util.Log
import com.example.unsplah_app_tutorial.App
import com.example.unsplah_app_tutorial.Model.SearchData
import com.example.unsplah_app_tutorial.Utlis.Constants.TAG
import com.google.gson.Gson

object SharedPrefManager{
    private const val SHARED_SEARCH_HISTROY = "shared_search_histroy"
    private const val KEY_SEARCH_HISTORY = "key_search_histroy"

    private const val SHARED_SEARCH_HISTORY_MODE = "shared_search_history_mode"
    private const val KEY_SEARCH_SHITORY_MODE = "key_search_history_mode"

    // 검색어 저장 모드 설정
    fun setSearchHistoryMode(isActivityed: Boolean){
        Log.d(TAG, "SharedPrefManager - setSearchHistoryMode()")
        // 쉐어드 가져오기
        val shared = App.instance.getSharedPreferences(SHARED_SEARCH_HISTORY_MODE, Context.MODE_PRIVATE)

        // 쉐어드 에디터 가져오기
        val editor = shared.edit()

        editor.putBoolean(KEY_SEARCH_SHITORY_MODE, isActivityed)

        editor.apply()
    }

    // 검색 저장모드 확인
    fun checkSearchHistoryMode(): Boolean{

        // 쉐어드 가져오기
        val shared = App.instance.getSharedPreferences(SHARED_SEARCH_HISTORY_MODE, Context.MODE_PRIVATE)

        return shared.getBoolean(KEY_SEARCH_SHITORY_MODE, false)
    }

    // 검색 목록 저장
    fun storeSearchHistroyList(searchHistoryList: MutableList<SearchData>){
        Log.d(TAG, "SharedPrefManager - storeSearchHistroyList()")

        // 매개변수로 들어온 배열을 -> 문자열로 면환
        val searchHistoryListString : String = Gson().toJson(searchHistoryList)
        Log.d(TAG, "SharedPrefManager - searchHistroyListString : $searchHistoryListString")


        // 쉐어드 가져오기
        val shared = App.instance.getSharedPreferences(SHARED_SEARCH_HISTROY, Context.MODE_PRIVATE)

        // 쉐어드 에디터 가져오기
        val editor = shared.edit()

        editor.putString(KEY_SEARCH_HISTORY, searchHistoryListString)

        editor.apply()
    }

    // 검색 목록가져오기
    fun getSearchHistroyLis():MutableList<SearchData>{
        // 쉐어드 가져오기
        val shared = App.instance.getSharedPreferences(SHARED_SEARCH_HISTROY, Context.MODE_PRIVATE)

        val storedSearchHistroyListString = shared.getString(KEY_SEARCH_HISTORY, "")!!

        var storedSearchHistryList = ArrayList<SearchData>()
        // 검색 목록의 값이 있다면
        if(storedSearchHistroyListString.isNotEmpty()){
            storedSearchHistryList = Gson().
                                    fromJson(storedSearchHistroyListString, Array<SearchData>::class.java).
                                    toMutableList() as ArrayList<SearchData>
        }

        return storedSearchHistryList
    }

    // 검색어 목록 지우기
    fun clearSearchHistoryList(){
        Log.d(TAG, "SharedPrefManager - clearSearchHistoryList()")

        // 쉐어드 가져오기
        val shared = App.instance.getSharedPreferences(SHARED_SEARCH_HISTROY, Context.MODE_PRIVATE)

        // 쉐어드 에디터 가져오기
        val editor = shared.edit()

        editor.clear()

        editor.apply()
    }
}