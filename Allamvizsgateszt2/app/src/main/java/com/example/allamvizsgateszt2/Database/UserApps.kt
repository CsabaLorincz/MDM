package com.example.allamvizsgateszt2.Database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_apps_table")
data class UserApps(@PrimaryKey(autoGenerate = true) val id: Int, val userName:String, val appName: String) {
    override fun toString(): String {
        return "User(id=$id, name='$userName', $appName)"
    }
}