package com.example.incomeexpensemanager.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable
import java.text.DateFormat

@Entity(tableName = "User", indices = [Index(value = ["username"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Int = 0,
    var name: String? = "",
    var email: String? = "",
    var username: String? = "",
    var password: String? = "",
    var address: String? = "",
    var createdAt: Long = System.currentTimeMillis(),
    var profile: String? = "",
    var phoneNumber: String? = ""
) : Serializable {
    val createdAtDateFormat: String
        get() = DateFormat.getDateTimeInstance()
            .format(createdAt) // Date Format: Jan 11, 2021, 11:30 AM
}