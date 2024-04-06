package com.example.incomeexpensemanager.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cleanTextContent
import com.example.incomeexpensemanager.databinding.ActivityTransactionDetailsBinding
import com.example.incomeexpensemanager.model.Transaction
import dagger.hilt.android.AndroidEntryPoint
import nepaliRupee
@AndroidEntryPoint
class TransactionDetailsActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityTransactionDetailsBinding.inflate(layoutInflater)
    }

    companion object {
        lateinit var transaction: Transaction
        fun getIntent(context: Context, transaction: Transaction): Intent {
            this.transaction = transaction
            return Intent(context, TransactionDetailsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        onDetailsLoaded()
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun onDetailsLoaded() = with(binding.transactionDetails) {
        title.text = transaction.title
        amount.text = nepaliRupee(transaction.amount).cleanTextContent
        type.text = transaction.transactionType
        tag.text = transaction.tag
        date.text = transaction.date
        note.text = transaction.note
        createdAt.text = transaction.createdAtDateFormat
    }
}