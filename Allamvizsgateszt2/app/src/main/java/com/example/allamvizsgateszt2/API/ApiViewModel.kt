package com.example.allamvizsgateszt2.API

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Response

class ApiViewModel(private val repository: ApiRepository): ViewModel() {

    suspend fun getApps(name: String, pw: String): Response<List<String>> {
       return repository.getApps(name, pw)
    }

    suspend fun createUser(userUpload: UserUpload): Response<Boolean>{
        return repository.createUser(userUpload)
    }

    suspend fun createPolicy(name: String, pw: String, appName: String): Response<Boolean>{
        return repository.createPolicy(name, pw, appName)
    }
    suspend fun deletePolicy(name: String, pw: String, appName: String): Response<Boolean>{
        return repository.deletePolicy(name, pw, appName)
    }

}