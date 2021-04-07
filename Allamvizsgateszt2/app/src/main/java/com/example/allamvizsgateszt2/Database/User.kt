package com.example.allamvizsgateszt2.Database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(@PrimaryKey(autoGenerate = true) val id: Int, val name:String, val password:String, val isLogged: Boolean) {
    override fun toString(): String {
        return "User(id=$id, name='$name', $isLogged)"
    }
}