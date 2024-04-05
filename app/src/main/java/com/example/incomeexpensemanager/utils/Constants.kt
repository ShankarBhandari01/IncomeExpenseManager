package com.example.incomeexpensemanager.utils

class Constants {

    companion object {
        val key = "asdadsadssdaaaar".toByteArray()

        /**
         * Notification channel details
         */
        const val NOTIFICATION_CHANNEL_ID = "MyForegroundServiceChannel"
        const val NOTIFICATION_ID = 1
        const val REQUEST_CODE = 0x01


        /**
         *
         * Error Messages
         */
        const val API_INTERNET_MESSAGE = "No Internet Connection"
        const val API_SOMETHING_WENT_WRONG_MESSAGE = "Something went wrong"
        const val API_FAILED_CODE = "500"
        const val API_SUCCESS_CODE = "9999"
        const val API_FAILURE_CODE = "5555"
        const val API_INTERNET_CODE = "500"


        /**
         * Offline Values Lists
         * */
        const val USER_PREFERENCES = "USER_LOGIN"
        const val OFFLINE_DATABASE = "app_database"
        const val NOTE_TABLE = "NOTE_TABLE"
        const val PREF_NAME = "Shared_pef"
        const val THEME_KEY = "theme_key"

        val transactionType = listOf("Income", "Expense")

        val transactionTags = listOf(
            "Housing",
            "Transportation",
            "Food",
            "Utilities",
            "Insurance",
            "Healthcare",
            "Saving & Debts",
            "Personal Spending",
            "Entertainment",
            "Miscellaneous"
        )
    }
}