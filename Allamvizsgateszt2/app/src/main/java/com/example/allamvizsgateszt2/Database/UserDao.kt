package com.example.allamvizsgateszt2.Database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(word: User)

    @Query("DELETE FROM user_table")
    fun deleteAll()

    @Query("UPDATE user_table SET isLogged=0")
    fun updateAll()

    @Query("UPDATE user_table SET isLogged=0 WHERE name!=:userName")
    fun updateAllBut(userName: String)

    @Query("SELECT * from user_table where isLogged=1")
    fun getCurrentUser(): User

    @Query("Select name from user_table")
    fun getAllUsers(): List<String>

    @Query("UPDATE user_table SET isLogged=1 WHERE name=:userName")
    fun updateLogged(userName: String)

    //UserApps

    @Query("SELECT appName from user_apps_table where userName=:userName")
    fun getUserApps(userName: String): List<String>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUserApp(word: UserApps)

    @Query("DELETE FROM user_apps_table where userName=:userName and appName=:appName")
    fun deleteUserApps(userName: String, appName: String)


}