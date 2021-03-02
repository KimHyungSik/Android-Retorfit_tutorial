package com.example.unsplah_app_tutorial.RecyclerView

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.unsplah_app_tutorial.Model.SearchData
import com.example.unsplah_app_tutorial.Utlis.Constants.TAG
import kotlinx.android.synthetic.main.layout_search_item.view.*

class SearchItemViewHolder(itemView: View, searchRecyclerViewInterface: ISearchHistoryRecyclerView)
                        : RecyclerView.ViewHolder(itemView),
                            View.OnClickListener
{

    // 뷰 가져오기
    private val searchItemTextView = itemView.search_term_text
    private val searchItemWhenTextView = itemView.when_search_text
    private val deleteSearchBtn = itemView.search_history_delete_btn
    private val constraintSearchItem = itemView.constraint_search_item

    private var mySearchRecyclerViewInterface : ISearchHistoryRecyclerView? = null

    init {
        Log.d(TAG, "SearchItemViewHolder - init")
        // 리스너 연결
        deleteSearchBtn.setOnClickListener(this)
        constraintSearchItem.setOnClickListener(this)
        this.mySearchRecyclerViewInterface = searchRecyclerViewInterface
    }

    // 데이터와 뷰를 묶는다
    fun bindWithView(searchItem: SearchData){
        Log.d(TAG, "SearchItemViewHolder - bindWithView()")
        searchItemWhenTextView.text = searchItem.timestapm
        searchItemTextView.text = searchItem.term
    }

    override fun onClick(v: View?) {
        when(v){
            deleteSearchBtn->{
                Log.d(TAG, "SearchItemViewHolder - 검색 삭제 버튼 ")
                this.mySearchRecyclerViewInterface!!.onSearchItemDeleteBtnClicked(adapterPosition)
            }
            constraintSearchItem->{
                Log.d(TAG, "SearchItemViewHolder - 검색 아이템 클릭")
                this.mySearchRecyclerViewInterface!!.onSearchItemClicked(adapterPosition)
            }
        }
    }
}