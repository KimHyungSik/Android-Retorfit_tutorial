package com.example.unsplah_app_tutorial.RecyclerView

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.unsplah_app_tutorial.Model.SearchData
import com.example.unsplah_app_tutorial.R
import com.example.unsplah_app_tutorial.Utlis.Constants.TAG

class SearchHistroyRecyclerViewAdpter(searchHistoryRecyclerViewInterface: ISearchHistoryRecyclerView)
                                    : RecyclerView.Adapter<SearchItemViewHolder>()
{

    private var searchHistoryList: ArrayList<SearchData> = ArrayList()

    private var iSearchHistoryRecyclerView : ISearchHistoryRecyclerView? = null

    init {
        Log.d(TAG, "SearchHistroyRecyclerViewAdpter - init()")
        this.iSearchHistoryRecyclerView = searchHistoryRecyclerViewInterface
    }

    // 뷰홀더가 메모리에 올라갔을때
    // 뷰홀더와 레이아웃을 연결 시켜준다.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemViewHolder {
        return SearchItemViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.layout_search_item, parent, false),
                this.iSearchHistoryRecyclerView!!
        )
    }

    override fun getItemCount(): Int {
        return searchHistoryList.size
    }

    override fun onBindViewHolder(holder: SearchItemViewHolder, position: Int) {
        val dataItem: SearchData = this.searchHistoryList[position]
        holder.bindWithView(dataItem)
    }

    //
    fun submitList(searchHistoryList: ArrayList<SearchData>){
        this.searchHistoryList = searchHistoryList
    }
}