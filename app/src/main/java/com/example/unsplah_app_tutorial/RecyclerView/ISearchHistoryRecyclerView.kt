package com.example.unsplah_app_tutorial.RecyclerView

interface ISearchHistoryRecyclerView {

    // 검색 아이템 삭제 버튼 클릭
    fun onSearchItemDeleteBtnClicked(position: Int)

    // 검색 버튼 클릭
    fun onSearchItemClicked(position: Int)
}