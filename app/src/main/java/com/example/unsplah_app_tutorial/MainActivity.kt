package com.example.unsplah_app_tutorial

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.unsplah_app_tutorial.Utlis.Constants
import com.example.unsplah_app_tutorial.Utlis.RESPONSE_STATE
import com.example.unsplah_app_tutorial.Utlis.SEARCH_TYPE
import com.example.unsplah_app_tutorial.Utlis.onMyTextChange
import com.example.unsplah_app_tutorial.databinding.ActivityMainBinding
import com.example.unsplah_app_tutorial.retrofit.RetrofitManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_button_search.*

class MainActivity : AppCompatActivity() {

    companion object{
        const val TAG = Constants.TAG
    }

    private var currentSearchType : SEARCH_TYPE = SEARCH_TYPE.PHOTO
    private var activityMainBinding : ActivityMainBinding? = null

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        activityMainBinding = binding

        setContentView(activityMainBinding!!.root)

        Log.d(Constants.TAG, "MainActivity - onCreate()")

        //라디오 그룹 가져오기
        activityMainBinding!!.searchTermRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId){
                R.id.photo_search_radio_btn ->{
                    Log.d(TAG, "사진 검색 버튼 클릭")
                    activityMainBinding!!.searchThemeTextLayout.hint = "사진검색"
                    activityMainBinding!!.searchThemeTextLayout.startIconDrawable = resources.getDrawable(R.drawable.ic_image_black, resources.newTheme())
                    this.currentSearchType = SEARCH_TYPE.PHOTO
                }
                R.id.user_search_radio_btn->{
                    Log.d(TAG, "사용자 검색 버튼 클릭")
                    activityMainBinding!!.searchThemeTextLayout.hint = "사용자검색"
                    activityMainBinding!!.searchThemeTextLayout.startIconDrawable = resources.getDrawable(R.drawable.ic_person_black, resources.newTheme())
                    this.currentSearchType = SEARCH_TYPE.USER
                }
            }
            Log.d(TAG, "MainActivity - OnCheckedChanged()")
        }

        //텍스트가 변경이 되었을때
        activityMainBinding!!.searchThermEditText.onMyTextChange {
            //입력된 글자가 하나라도 있다면
            if(it.toString().count() > 0){
                //검색 버튼을 보여준다
                activityMainBinding!!.includeSerarchFrameLyout.frameSearchBtn.visibility = View.VISIBLE
                activityMainBinding!!.searchThemeTextLayout.helperText = ""
                //스크롤뷰를 올린다
                activityMainBinding!!.mainScrollView.scrollTo(0, 200)
            }else{
                activityMainBinding!!.includeSerarchFrameLyout.frameSearchBtn.visibility = View.INVISIBLE
            }

            if(it.toString().count() == 12){
                Log.d(TAG, "MainActivity - 글자 수 초과")
                Toast.makeText(this, "검색어는 12자 까지만 입력 가능 합니다", Toast.LENGTH_SHORT).show()
            }
        }

        activityMainBinding!!.includeSerarchFrameLyout.searchBtn.setOnClickListener {

            // 검색 api 호출
            RetrofitManager.instance.searchPhotos(searchTerm = activityMainBinding!!.searchThermEditText.text.toString(), completrion = {
                responsState, result ->
                when(responsState){
                    RESPONSE_STATE.OKAY->{
                        Log.d(TAG, "API 호출 성공 $result")
                    }
                    RESPONSE_STATE.FAIL->{
                        Toast.makeText(this, "api 호출 에러입니다", Toast.LENGTH_SHORT).show()
                    }
                }
            })

            this.handleSearchButtonUi()
        }

    }

    private fun handleSearchButtonUi(){
        activityMainBinding!!.includeSerarchFrameLyout.searchProgressBar.visibility = View.VISIBLE

        activityMainBinding!!.includeSerarchFrameLyout.searchBtn.text = ""

        Handler().postDelayed({
            activityMainBinding!!.includeSerarchFrameLyout.searchProgressBar.visibility = View.INVISIBLE
            activityMainBinding!!.includeSerarchFrameLyout.searchBtn.text = "검색"
        }, 1500)
    }

}