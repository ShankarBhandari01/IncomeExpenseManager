package com.example.incomeexpensemanager.data.local.room_database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.incomeexpensemanager.model.Transaction
import com.example.incomeexpensemanager.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("select*from User WHERE id = :id")
    fun getUserById(id: Int): Flow<User>

    @Query("SELECT * FROM USER WHERE username = :username")
    fun getUser(username: String): Flow<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Query("SELECT * FROM all_transactions ORDER by createdAt DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM all_transactions WHERE transactionType == :transactionType ORDER by createdAt DESC")
    fun getAllSingleTransaction(transactionType: String): Flow<List<Transaction>>

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)
}