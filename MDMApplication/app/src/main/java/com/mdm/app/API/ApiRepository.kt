package com.mdm.app.API

import retrofit2.Response


class ApiRepository {
    suspend fun getApps(name: String, pw: String): Response<List<String>>{
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