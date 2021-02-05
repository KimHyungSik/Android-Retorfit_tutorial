package com.example.unsplah_app_tutorial.RecyclerView

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.unsplah_app_tutorial.App
import com.example.unsplah_app_tutorial.Model.Photo
import com.example.unsplah_app_tutorial.R
import kotlinx.android.synthetic.main.layout_photo_item.view.*

class PhotoItemViewHoler(itemView: View) : RecyclerView.ViewHolder(itemView) {

    // 뷰를 가져온다
    private val photoImageView = itemView.photo_image
    private val photoCreateAtText = itemView.create_at_text
    private val photoLikesCountText = itemView.likes_count_text

    // 데이터와 뷰를 묶는다
    fun bindWithView(photoItem: Photo){
        photoCreateAtText.text = photoItem.createdAt
        photoLikesCountText.text = photoItem.likesCount.toString()

        //Glide.with(this).load("http://goo.gl/gEgYUd").into(imageView);
        Glide.with(App.instance)
            .load(photoItem.thumbnail)
            .placeholder(R.drawable.ic_baseline_image_search_24)
            .into(photoImageView)
    }

}