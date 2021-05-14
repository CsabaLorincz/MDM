package com.mdm.app.API

import com.google.gson.GsonBuilder
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface ApiService{

    @GET("items/name={name}&pw={pw}")
    suspend fun getApps(@Path("name") name: String, @Path("pw") pw: String):
            Response<List<String>>

    @POST("users")
    suspend fun addUser(@Body userData: UserUpload): Response<Boolean>

    @POST("items/name={name}&pw={pw}")
    suspend fun createPolicy(@Path("name") name: String, @Path("pw") pw: String, @Body uploadApp: AppUpload): Response<Boolean>

    @DELETE("items/name={name}&pw={pw}&appName={appName}")
    suspend fun deletePolicy(@Path("name") name: String, @Path("pw") pw: String, @Path("appName") appName: String): Response<Boolean>
}
object Api {
    private const val BASE_URL= "http://192.168.0.10:5000/"

    var gson = GsonBuilder()
        .setLenient()
        .create()
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val retrofitService : ApiService by lazy {
        retrofit.create(ApiService::class.java) }
}