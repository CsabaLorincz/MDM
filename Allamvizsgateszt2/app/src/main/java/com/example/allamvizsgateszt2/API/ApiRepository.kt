package com.example.allamvizsgateszt2.API

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ApiRepository {
    suspend fun getApps(name: String, pw: String): Response<List<String>>{
        Log.d("Siker0", name)
        return Api.retrofitService.getApps(name, pw)
    }

    suspend fun createUser(userData: UserUpload): Response<Boolean>{
        return Api.retrofitService.addUser(userData)
    }

    suspend fun createPolicy(name: String, pw: String, appName: String): Response<Boolean>{
        return Api.retrofitService.createPolicy(name, pw, AppUpload(0, appName))
    }

    suspend fun deletePolicy(name: String, pw: String, appName: String): Response<Boolean>{
        return Api.retrofitService.deletePolicy(name, pw, appName)
    }
}