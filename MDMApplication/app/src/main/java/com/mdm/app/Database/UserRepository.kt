package com.mdm.app.Database

import androidx.annotation.WorkerThread
import com.mdm.app.activities.MDMActivity

class UserRepository(private val UserDao: UserDao) {

    fun getAll(): List<String> {
        return UserDao.getAllUsers()
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(user: User) {
        UserDao.insert(user)
    }

    suspend fun deleteAll(){
        UserDao.deleteAll()
    }

    suspend fun updateAll(){
        UserDao.updateAll()
    }

    suspend fun updateAllBut(name: String){
        UserDao.updateAllBut(name)
    }
    suspend fun getCurrent(){
        val x=UserDao.getCurrentUser()
        if(x==null || x.name==""){
            MDMActivity.setAsUser("")
            MDMActivity.setAsPw("")
        }
        else{
            MDMActivity.setAsUser(x.name)
            MDMActivity.setAsPw(x.password)
        }

    }
    suspend fun update(userName: String){
        UserDao.updateLogged(userName)
    }

    //UserApps

    fun getUserApps(userName: String): List<String>{
        return UserDao.getUserApps(userName)
    }

    suspend fun insertUserApp(userApp: UserApps) {
        UserDao.insertUserApp(userApp)
    }

    suspend fun deleteUserApps(userName: String, appName: String){
        UserDao.deleteUserApps(userName, appName)
    }

}