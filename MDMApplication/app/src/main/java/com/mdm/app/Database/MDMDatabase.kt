package com.mdm.app.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope


@Database(entities = [User::class, UserApps::class], version = 3, exportSchema = false)
abstract class MDMDatabase : RoomDatabase() {

    abstract fun UserDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: MDMDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): MDMDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MDMDatabase::class.java,
                    "project_database"
                ).addCallback(MDMDatabaseCallback(scope)).fallbackToDestructiveMigration().allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
    private class MDMDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
    }
}