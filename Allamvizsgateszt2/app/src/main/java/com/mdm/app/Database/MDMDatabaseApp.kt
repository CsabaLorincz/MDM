package com.mdm.app.Database

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MDMDatabaseApp: Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())


    val database by lazy { MDMDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { UserRepository(database.UserDao()) }

}