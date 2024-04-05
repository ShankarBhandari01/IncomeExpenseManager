package com.example.incomeexpensemanager.repository

import com.example.incomeexpensemanager.data.local.datastore.UIModeImpl
import com.example.incomeexpensemanager.data.local.room_database.AppDao
import com.example.incomeexpensemanager.model.Transaction
import com.example.incomeexpensemanager.model.User
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class LoginRepo @Inject constructor(
    private val db: AppDao,
    private val uiModeDataStore: UIModeImpl
) {
    val getUIMode get() = uiModeDataStore.uiMode
    suspend fun setDarkMode(isNightMode: Boolean) = uiModeDataStore.saveToDataStore(isNightMode)


    suspend fun insertUser(user: User) = db.insertUser(user = user)

    fun getUserById(id: Int) = db.getUserById(id)

    fun getUser(username: String) = db.getUser(username)

    suspend fun insert(transaction: Transaction) = db.insertTransaction(transaction)

    private fun getAllTransactions() = db.getAllTransactions()

    fun getAllSingleTransaction(transactionType: String) = if (transactionType == "Overall") {
        getAllTransactions()
    } else {
        db.getAllSingleTransaction(transactionType)
    }

    suspend fun delete(transaction: Transaction) = db.deleteTransaction(
        transaction
    )

}