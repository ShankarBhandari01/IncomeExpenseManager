package com.example.incomeexpensemanager.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.incomeexpensemanager.base.BaseViewModel
import com.example.incomeexpensemanager.model.Transaction
import com.example.incomeexpensemanager.model.User
import com.example.incomeexpensemanager.repository.LoginRepo
import com.example.incomeexpensemanager.utils.Constants.Companion.key
import com.example.incomeexpensemanager.utils.Encryption
import com.example.incomeexpensemanager.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UserLoginVM @Inject constructor(
    private val loginRepo: LoginRepo,
    application: Application
) : BaseViewModel(application) {
    private val _userDetailsState: MutableStateFlow<UiState<User>> =
        MutableStateFlow(UiState.Loading)
    val userDetailsState: StateFlow<UiState<User>> = _userDetailsState

    private val _transactionFilter = MutableStateFlow("Overall")
    val transactionFilter: StateFlow<String> = _transactionFilter

    private val _uiState = MutableStateFlow<UiState<List<Transaction>>>(UiState.Loading)

    // UI collect from this stateFlow to get the state updates
    val uiState: StateFlow<UiState<List<Transaction>>> = _uiState


    // get ui mode
    val getUIMode = loginRepo.getUIMode
    fun setDarkMode(isNightMode: Boolean) {
        viewModelScope.launch {
            loginRepo.setDarkMode(isNightMode)
        }
    }

    fun googleLogin(user: User) {
        viewModelScope.launch {
            loginRepo.insertUser(user)
        }
    }

    fun getUser(id: Int) = viewModelScope.launch {
        _userDetailsState.value = UiState.Loading
        loginRepo.getUserById(id).collect { user: User? ->
            if (user != null) {
                _userDetailsState.value = UiState.Success(user)
            }
        }
    }


    fun getUser(username: String, password: String) = viewModelScope.launch {
        _userDetailsState.value = UiState.Loading
        loginRepo.getUser(username).collect { user: User? ->
            if (user != null) {
                val decrypted = Encryption.decryptAES(user.password!!, key)
                if (password.equals(decrypted, true)) {
                    _userDetailsState.value = UiState.Success(user)
                } else {
                    _userDetailsState.value = UiState.Error("Invalid Credentials ")
                }

            } else {
                _userDetailsState.value = UiState.Error("User not Found")
            }
        }
    }


    fun insertTransaction(transaction: Transaction) = viewModelScope.launch {
        loginRepo.insert(transaction)
    }

    fun getAllTransaction(type: String) = viewModelScope.launch {
        loginRepo.getAllSingleTransaction(type).collect { result ->
            if (result.isEmpty()) {
                _uiState.value = UiState.Empty
            } else {
                _uiState.value = UiState.Success(result)
                Timber.i("Filter", "Transaction filter is ${transactionFilter.value}")
            }
        }
    }

    fun deleteTransaction(transaction: Transaction) = viewModelScope.launch {
        loginRepo.delete(transaction)
    }


    fun allIncome() {
        _transactionFilter.value = "Income"
    }

    fun allExpense() {
        _transactionFilter.value = "Expense"
    }

    fun overall() {
        _transactionFilter.value = "Overall"
    }
}