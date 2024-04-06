package com.example.incomeexpensemanager.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.incomeexpensemanager.R
import com.example.incomeexpensemanager.databinding.ActivityProfileBinding
import com.example.incomeexpensemanager.model.User
import com.example.incomeexpensemanager.utils.SweetToast
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Profile : AppCompatActivity() {
    private val binding by lazy {
        ActivityProfileBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth

    companion object {
        lateinit var user: User
        fun getIntent(context: Context, user: User): Intent {
            this.user = user
            return Intent(context, Profile::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUpPic()
        auth = FirebaseAuth.getInstance()
        binding.tvName.text = user.name
        binding.tvEmail.text = user.email
        binding.backBtn.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.logoutLayout.setOnClickListener {
            logout()
        }
        binding.settingBtn.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
    }


    private fun logout() {
        try {
            auth.signOut()
            val intent = Intent(this@Profile, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            return this@Profile.finish()
        }catch (e:Exception){
            e.localizedMessage?.let { SweetToast.error(this, it) }
        }

    }

    private fun setUpPic() = with(binding) {
        Glide.with(this@Profile).load(DashboardActivity.user.profile).centerCrop()
            .placeholder(R.drawable.profile).into(image)
    }
}