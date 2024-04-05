package com.example.incomeexpensemanager.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Tbl_category")
data class Category(
    var title: String? = null,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}