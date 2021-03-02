package com.example.unsplah_app_tutorial.activitys

import android.app.SearchManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unsplah_app_tutorial.Model.Photo
import com.example.unsplah_app_tutorial.Model.SearchData
import com.example.unsplah_app_tutorial.R
import com.example.unsplah_app_tutorial.RecyclerView.ISearchHistoryRecyclerView
import com.example.unsplah_app_tutorial.RecyclerView.PhotoGridRecyclerViewAdpter
import com.example.unsplah_app_tutorial.RecyclerView.SearchHistroyRecyclerViewAdpter
import com.example.unsplah_app_tutorial.Utlis.Constants.TAG
import com.example.unsplah_app_tutorial.Utlis.RESPONSE_STATUS
import com.example.unsplah_app_tutorial.Utlis.SharedPrefManager
import com.example.unsplah_app_tutorial.Utlis.textChangeToFlow
import com.example.unsplah_app_tutorial.Utlis.toSimpleString
import com.example.unsplah_app_tutorial.databinding.ActivityPhotoCollectionBinding
import com.example.unsplah_app_tutorial.retrofit.RetrofitManager
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_photo_collection.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext


class PhotoCollectionActivity : AppCompatActivity(),
                                SearchView.OnQueryTextListener,
                                CompoundButton.OnCheckedChangeListener,
                                View.OnClickListener,
                                ISearchHistoryRecyclerView
{
    //데이터
    lateinit var activityPhotoCollectionBinding : ActivityPhotoCollectionBinding
    private var photoList = ArrayList<Photo>()

    // 검색 기록 배열
    var searchHistroyList = ArrayList<SearchData>()

    //어답터
    private lateinit var photoGridRecyclerViewAdpter: PhotoGridRecyclerViewAdpter
    private lateinit var mySearchHistoryRecyclerViewAdpter: SearchHistroyRecyclerViewAdpter

    // 서치뷰
    private lateinit var mySearchView: SearchView

    //서치뷰 에딧 텍스트
    private lateinit var mySearchViewEditText: EditText


    // Rx적용용
   // 옵저버블 통합 제거를 위한 CompoiteDisposable
    //private var myCompositeDisposable = CompositeDisposable()

    private var myCoroutineJob : Job = Job()
    private val myCoroutineContext: CoroutineContext
        get() = Dispatchers.IO + myCoroutineJob

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityPhotoCollectionBinding = ActivityPhotoCollectionBinding.inflate(layoutInflater)

        setContentView(activityPhotoCollectionBinding.root)

        Log.d(TAG, "PhotoCollectionActivity - onCreate()")
        // 리사이클러 리스트 수신
        val bundle = intent.getBundleExtra("array_bundle")
        // 검색어 수신
        val searchTerm = intent.getStringExtra("search_term")

        photoList = bundle?.getSerializable("photo_array_list") as ArrayList<Photo>
        activityPhotoCollectionBinding.topAppBar.title = searchTerm

        //
        setSupportActionBar(top_app_bar)

        this.photoGridRecyclerViewSetting(photoList)

        // 저장된 검색 기록 가져오기
        this.searchHistroyList = SharedPrefManager.getSearchHistroyLis() as ArrayList<SearchData>
        this.searchHistroyList.forEach {
            Log.d(TAG, "PhotoCollectionActivity 저장된 검색 기록 : ${it.term}, ${it.timestapm}")
        }

        // 검색 기록 리사이클러뷰 준비
        this.searchHistoryRecyclerViewSetting(this.searchHistroyList)

        // 검색 기록 갱신
        if(searchTerm!!.isNotEmpty()){
            val term = searchTerm ?: ""
            this.insertSearchTermHistory(term)
        }

        this.activityPhotoCollectionBinding.searchHistroyModeSwitch.isChecked = SharedPrefManager.checkSearchHistoryMode()

    }

    override fun onDestroy() {
        // Rx적용
        // 모두 삭제
        //this.myCompositeDisposable.clear()

        myCoroutineContext.cancel()
        super.onDestroy()
    }

    private fun searchHistoryRecyclerViewSetting(searchHistoryList: ArrayList<SearchData>){
        Log.d(TAG, "PhotoCollectionActivity - searchHistoryRecyclerViewSetting()")
        this.mySearchHistoryRecyclerViewAdpter = SearchHistroyRecyclerViewAdpter(this)
        this.mySearchHistoryRecyclerViewAdpter.submitList(searchHistoryList)

        val myLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        myLinearLayoutManager.stackFromEnd = true

        activityPhotoCollectionBinding.searchHistoryRecyclerView.apply {
            layoutManager = myLinearLayoutManager
            this.scrollToPosition(mySearchHistoryRecyclerViewAdpter.itemCount - 1)
            adapter = mySearchHistoryRecyclerViewAdpter
        }
    }

    private fun photoGridRecyclerViewSetting(photoList: ArrayList<Photo>){
        activityPhotoCollectionBinding.searchHistroyModeSwitch.setOnCheckedChangeListener(this)
        activityPhotoCollectionBinding.clearSearchHistroyBtn.setOnClickListener(this)

        this.photoGridRecyclerViewAdpter = PhotoGridRecyclerViewAdpter()
        this.photoGridRecyclerViewAdpter.submitList(photoList)

        activityPhotoCollectionBinding.myPhotoRecyclerView.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        activityPhotoCollectionBinding.myPhotoRecyclerView.adapter = this.photoGridRecyclerViewAdpter
    }

    // 메테리얼 툴방에서 서치뷰 접근 방법
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        Log.d(TAG, "PhotoCollectionActivity - onCreateOptionsMenu()")
        val inflater = menuInflater
        inflater.inflate(R.menu.top_app_bar_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        this.mySearchView = menu?.findItem(R.id.search_menu_item)?.actionView as SearchView
        this.mySearchView.apply {
            this.queryHint = "검색어를 입력해주세요"

            this.setOnQueryTextListener(this@PhotoCollectionActivity)

            this.setOnQueryTextFocusChangeListener { _, hasExpaned ->
                when(hasExpaned){
                    true ->{
                        Log.d(TAG, "서치뷰 열림")
                        //activityPhotoCollectionBinding.linearLayoutHistoryView.visibility = View.VISIBLE
                        //handleSearchViewUi()
                    }
                    false->{
                        Log.d(TAG, "서치뷰 닫힘")
                        activityPhotoCollectionBinding.linearLayoutHistoryView.visibility = View.INVISIBLE
                    }
                }
            }
            //서치뷰에서 에뎃텍스트를 가져온다
            mySearchViewEditText = this.findViewById(androidx.appcompat.R.id.search_src_text)

            // 에딧 텍스트 옵저버블
            // Rx적용 부분
            /*val editTextChangObservable = mySearchViewEditText.textChanges()
            val searchEditTextSubscription : Disposable =
            // 옵저버블에 연산자 추가
                editTextChangObservable
                // 글자가 입력되고 나서 0.8 초 후에 onNext 이벤트로 데이터 흘려보내기
                    .debounce(1000, TimeUnit.MILLISECONDS)
                    // IO 쓰레드에서 돌리겠다.
                    // Scheduler instance intended for IO-bound work.
                    // 네트워크 요청, 파일 읽기, 쓰기, 디비처리 등
                    .subscribeOn(Schedulers.io())
                    // 구독을 통해 이벤트 응답 받기
                    .subscribeBy(
                        onNext = {
                            Log.d("Rx", "PhotoCollectionActivity - onNext : $it")
                            // TODO:: 흘러들어온 이벤트 데이터로 api 호출
                            if(it.isNotEmpty()){
                                searchPhotoApiCall(it.toString())
                            }
                        },
                        onComplete = {
                            Log.d("Rx", "PhotoCollectionActivity - onComplete")
                        },
                        onError = {
                            Log.d("Rx", "PhotoCollectionActivity - onError : $it")
                        }
                    )*/
            // Rx 적용
            // compositeDisposable 에 추가
            //myCompositeDisposable.add(searchEditTextSubscription)


            // Rx의 스케쥴러와 비슷
            // IO 스레드에서 돌린다


            GlobalScope.launch(context = myCoroutineContext){
                // editText 가 변경되었을때
                val editTextFlow = mySearchViewEditText.textChangeToFlow()

                editTextFlow
                    .debounce(2000)
                    .filter {
                        it?.length!! > 0
                    }
                    .onEach {
                        Log.d(TAG, "PhotoCollectionActivity - flow로 받는다 / $it")
                    }
                    .launchIn(this)
            }

        }

        this.mySearchViewEditText.apply {
            this.filters = arrayOf(InputFilter.LengthFilter(12))
            this.setTextColor(Color.WHITE)
            this.setHintTextColor(Color.WHITE)
        }
        return true
    }

    // 서치뷰 검색어 입력 이벤트
    // 검색버튼이 클릭 되었으때
    override fun onQueryTextSubmit(query: String?): Boolean {
        Log.d(TAG, "PhotoCollectionActivity - onQueryTextSubmit() / query $query")

        if(!query.isNullOrEmpty()){
            this.activityPhotoCollectionBinding.topAppBar.title = query

            // 검색어 리스트 갱신
            this.insertSearchTermHistory(query)
            searchPhotoApiCall(query)
        }else{
            this.mySearchView.setQuery("", false)
        }
        this.mySearchView.clearFocus()
        this.activityPhotoCollectionBinding.topAppBar.collapseActionView()

        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        Log.d(TAG, "PhotoCollectionActivity - onQueryTextChange() / newText : $newText")

        //val userInputText = newText ?: ""
        val userInputText = newText.let{
            it
        }?: ""

        if(userInputText.count() == 12){
            Toast.makeText(this, "검색어는 12자 까지만 입력 가능 합니다.",Toast.LENGTH_SHORT).show()
        }

//        if(userInputText.length in 1..12){
//            searchPhotoApiCall(userInputText)
//        }

        return true
    }

    override fun onCheckedChanged(switch: CompoundButton?, isChecked: Boolean) {
        when(switch){
            activityPhotoCollectionBinding.searchHistroyModeSwitch->{
                if(isChecked){
                    Log.d(TAG, "검색어 저장 기능 온")
                    SharedPrefManager.setSearchHistoryMode(true)
                }else{
                    Log.d(TAG, "검색어 저장 기능 아웃")
                    SharedPrefManager.setSearchHistoryMode(false)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when(v){
            activityPhotoCollectionBinding.clearSearchHistroyBtn->{
                Log.d(TAG, "검색 삭제 버튼 클릭")
                SharedPrefManager.clearSearchHistoryList()
                this.searchHistroyList.clear()
                // ui처리
                handleSearchViewUi()
            }
        }
    }

    // 검색 아이템 삭제 버튼 이벤트
    override fun onSearchItemDeleteBtnClicked(position: Int) {
        Log.d(TAG, "PhotoCollectionActivity - onSearchItemDeleteBtnClicked() / position : $position")
        //TODO:: 해당 번째의 아이템 삭제 후 저장

        // 해당위치 데이터 삭제제
       this.searchHistroyList.removeAt(position)
        // 데이터 갱신
        SharedPrefManager.storeSearchHistroyList(searchHistroyList)
        // 데이터 변경을 어댑터에 알려줌
        this.mySearchHistoryRecyclerViewAdpter.notifyDataSetChanged()
        handleSearchViewUi()
    }

    // 검색 아이템 버튼 이벤트
    override fun onSearchItemClicked(position: Int) {
        Log.d(TAG, "PhotoCollectionActivity - onSearchItemClicked() / position : $position")
        //TODO:: 해당 부분 검색어를 호출

        val queryString = this.searchHistroyList[position].term

        this.searchPhotoApiCall(queryString)
        this.insertSearchTermHistory(queryString)
        
        this.activityPhotoCollectionBinding.topAppBar.title = queryString
        this.mySearchView.clearFocus()
        this.activityPhotoCollectionBinding.topAppBar.collapseActionView()
    }

    // 사진 검색 API호출
    private fun searchPhotoApiCall(query: String){
        RetrofitManager.instance.searchPhotos(searchTerm = query, completion = {
            status, list ->
            when(status){
                RESPONSE_STATUS.OKAY->{
                    Log.d(TAG, "PhotoCollectionActivity - searchPhotoApiCall() / list.size : ${list?.size}")

                    if(list != null){
                        this.photoList.clear()
                        this.photoList = list
                        this.photoGridRecyclerViewAdpter.submitList(photoList)
                        this.photoGridRecyclerViewAdpter.notifyDataSetChanged()
                    }

                }
                RESPONSE_STATUS.NO_CONTENT ->{
                    Toast.makeText(this, "$query 에 대한 검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun handleSearchViewUi(){
        Log.d(TAG, "PhotoCollectionActivity - handleSearchViewUi() / size : ${this.searchHistroyList.size}")
        if(this.searchHistroyList.size > 0){
            this.activityPhotoCollectionBinding.clearSearchHistroyBtn.visibility = View.VISIBLE
            this.activityPhotoCollectionBinding.searchHistroyLabel.visibility = View.VISIBLE
            this.activityPhotoCollectionBinding.searchHistoryRecyclerView.visibility = View.VISIBLE
        }else{
            this.activityPhotoCollectionBinding.clearSearchHistroyBtn.visibility = View.INVISIBLE
            this.activityPhotoCollectionBinding.searchHistroyLabel.visibility = View.INVISIBLE
            this.activityPhotoCollectionBinding.searchHistoryRecyclerView.visibility = View.INVISIBLE
        }
    }

    // 검색어 저장
    private fun insertSearchTermHistory(searchTerm: String){
        Log.d(TAG, "PhotoCollectionActivity - insertSearchTermHistory()")

        if(SharedPrefManager.checkSearchHistoryMode()){
            val newSearchData = SearchData(term = searchTerm, timestapm = Date().toSimpleString())

            var indexListToRemove = ArrayList<Int>()

            // 아이템 중복 확인
            this.searchHistroyList.forEachIndexed { index, searchDataItem ->
                if(searchDataItem.term == searchTerm){
                    indexListToRemove.add(index)
                }
            }

            // 중복 아이템 삭제
            indexListToRemove.forEach {
                this.searchHistroyList.removeAt(it)
            }

            this.searchHistroyList.add(newSearchData)

            SharedPrefManager.storeSearchHistroyList(searchHistroyList)
            this.mySearchHistoryRecyclerViewAdpter.notifyDataSetChanged()
        }
    }

}