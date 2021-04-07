package com.example.allamvizsgateszt2.Database

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class UserViewModel(private val repository: UserRepository) : ViewModel() {

    //val allUsers: List<String> = getAll()
    fun getAll(): List<String> {
        return repository.getAll()
    }

    fun insert(word: User) = viewModelScope.launch {
        repository.insert(word)
        Log.d("as error", "insert")
    }

    fun deleteAll()=viewModelScope.launch { repository.deleteAll()
        Log.d("as error", "deletall")
    }

    fun updateAll()=viewModelScope.launch { repository.updateAll()
        Log.d("as error", "updateall")
    }

    fun updateAllBut(name: String)=viewModelScope.launch { repository.updateAllBut(name)
        Log.d("as error", "updateallbut")
    }

    fun getCurrent()=viewModelScope.launch { repository.getCurrent()}

    fun update(name: String)=viewModelScope.launch{repository.update(name)
        Log.d("as error", "update")
    }

    //UserApps
    fun getUserApps(userName: String):List<String>{
        return repository.getUserApps(userName)
    }

    fun insertUserApp(userApps: UserApps)=viewModelScope.launch { repository.insertUserApp(userApps) }

    fun deleteUserApps(userName: String, appName: String)=viewModelScope.launch { repository.deleteUserApps(userName, appName) }
}

class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}