package com.mdm.app.Database

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class UserViewModel(private val repository: UserRepository) : ViewModel() {

    fun getAll(): List<String> {
        return repository.getAll()
    }

    fun insert(word: User) = viewModelScope.launch {
        repository.insert(word)
    }

    fun deleteAll()=viewModelScope.launch { repository.deleteAll() }

    fun updateAll()=viewModelScope.launch { repository.updateAll() }

    fun updateAllBut(name: String)=viewModelScope.launch { repository.updateAllBut(name) }

    fun getCurrent()=viewModelScope.launch { repository.getCurrent()}

    fun update(name: String)=viewModelScope.launch{repository.update(name) }

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