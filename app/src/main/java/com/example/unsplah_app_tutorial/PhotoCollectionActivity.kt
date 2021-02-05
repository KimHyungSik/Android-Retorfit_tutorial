package com.example.unsplah_app_tutorial

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.unsplah_app_tutorial.Model.Photo
import com.example.unsplah_app_tutorial.RecyclerView.PhotoGridRecyclerViewAdpter
import com.example.unsplah_app_tutorial.Utlis.Constants.TAG
import com.example.unsplah_app_tutorial.databinding.ActivityPhotoCollectionBinding


class PhotoCollectionActivity : AppCompatActivity() {

    //데이터
    lateinit var activityPhotoCollectionBinding : ActivityPhotoCollectionBinding
    private var photoList = ArrayList<Photo>()

    //어답터
    private lateinit var photoGridRecyclerViewAdpter: PhotoGridRecyclerViewAdpter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityPhotoCollectionBinding = ActivityPhotoCollectionBinding.inflate(layoutInflater)

        setContentView(activityPhotoCollectionBinding.root)

        Log.d(TAG, "PhotoCollectionActivity - onCreate()")

        val bundle = intent.getBundleExtra("array_bundle")

        val searchTerm = intent.getStringExtra("search_term")

        photoList = bundle?.getSerializable("photo_array_list") as ArrayList<Photo>
        activityPhotoCollectionBinding.topAppBar.title = searchTerm

        this.photoGridRecyclerViewAdpter = PhotoGridRecyclerViewAdpter()
        this.photoGridRecyclerViewAdpter.submitList(photoList)

        activityPhotoCollectionBinding.myPhotoRecyclerView.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        activityPhotoCollectionBinding.myPhotoRecyclerView.adapter = this.photoGridRecyclerViewAdpter
    }
}