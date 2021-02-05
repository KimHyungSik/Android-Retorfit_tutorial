package com.example.unsplah_app_tutorial

import android.app.Application

//전역에서 컨스턴스 접근
class App : Application(){
    companion object{
        lateinit var instance : App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

}