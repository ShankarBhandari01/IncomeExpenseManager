package com.example.incomeexpensemanager.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.incomeexpensemanager.R
import com.example.incomeexpensemanager.databinding.ActivityUserDetailsBinding
import com.example.incomeexpensemanager.model.User
import com.example.incomeexpensemanager.utils.Constants.Companion.key
import com.example.incomeexpensemanager.utils.Encryption
import com.example.incomeexpensemanager.utils.SweetToast
import com.example.incomeexpensemanager.viewmodel.UserLoginVM
import dagger.hilt.android.AndroidEntryPoint
import snack

@AndroidEntryPoint
class UserDetailsActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityUserDetailsBinding.inflate(layoutInflater)
    }
    private val viewModel by viewModels<UserLoginVM>()

    companion object {
        lateinit var loginUser: User
        fun getIntent(context: Context, user: User): Intent {
            loginUser = user
            return Intent(context, UserDetailsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getLogUser()
        clickListener()
    }

    private fun clickListener() = with(binding) {
        btnSave.setOnClickListener {
            try {
                val password = password.text.toString()
                val comfirmPassword = confirmpassword.text.toString()
                if (password.isBlank()) {
                    confirmpassword.error = "Please Enter Password"
                    return@setOnClickListener
                }
                if (comfirmPassword.isBlank()) {
                    confirmpassword.error = "Please Enter confirm Password"
                    return@setOnClickListener
                }
                if (password != comfirmPassword) {
                    confirmpassword.error = "Password does not matched"
                    return@setOnClickListener
                }
                if (phoneNumber.text.toString().isBlank()) {
                    phoneNumber.error = "Please enter Phone Number"
                    return@setOnClickListener
                }
                if (phoneNumber.text.toString().length < 10 || phoneNumber.text.toString()
                        .contains("+")
                ) {
                    phoneNumber.error = "Enter Valid Phone Number"
                    return@setOnClickListener
                }

                userdata?.password = Encryption.encryptAES(password, key)
                if (userdata != null) {
                    viewModel.googleLogin(userdata!!)
                    binding.root.snack(
                        string = R.string.success_saved
                    )
                    binding.userdata = User()
                    binding.password.text?.clear()
                    binding.confirmpassword.text?.clear()
                    onBackPressedDispatcher.onBackPressed()
                }
            } catch (e: Exception) {
                e.localizedMessage?.let { it1 -> SweetToast.error(this@UserDetailsActivity, it1) }
            }


        }
    }

    private fun getLogUser() {
        with(binding) {
            userdata = loginUser
        }

    }


}