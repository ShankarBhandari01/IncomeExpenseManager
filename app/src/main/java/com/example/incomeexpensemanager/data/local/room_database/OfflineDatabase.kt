package com.example.incomeexpensemanager.data.local.room_database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.incomeexpensemanager.model.Transaction
import com.example.incomeexpensemanager.model.User

@Database(entities = [Transaction::class, User::class], version = 6)
abstract class OfflineDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
}