package com.example.incomeexpensemanager.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.incomeexpensemanager.R
import com.example.incomeexpensemanager.databinding.ActivitySettingBinding
import com.example.incomeexpensemanager.viewmodel.UserLoginVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySettingBinding.inflate(layoutInflater)
    }
    private val viewModel by viewModels<UserLoginVM>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        observeThemeMode()
        setTheme()
    }


    private fun setTheme() {
        binding.swThemeSi.setOnCheckedChangeListener { _, b ->
            viewModel.setDarkMode(b)
        }
    }

    private fun observeThemeMode() {
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.getUIMode.collect {
                val mode = when (it) {
                    true -> {
                        binding.swThemeSi.setChecked(true)
                        AppCompatDelegate.MODE_NIGHT_YES
                    }

                    false -> {
                        binding.swThemeSi.setChecked(false)
                        AppCompatDelegate.MODE_NIGHT_NO
                    }
                }

                AppCompatDelegate.setDefaultNightMode(mode)
            }
        }
    }
}