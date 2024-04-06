package com.example.incomeexpensemanager.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.incomeexpensemanager.R
import com.example.incomeexpensemanager.databinding.ActivityMainBinding
import com.example.incomeexpensemanager.model.User
import com.example.incomeexpensemanager.utils.SweetToast
import com.example.incomeexpensemanager.utils.UiState
import com.example.incomeexpensemanager.utils.Utils
import com.example.incomeexpensemanager.viewmodel.UserLoginVM
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<UserLoginVM>()

    lateinit var user: User
    lateinit var number: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        binding.googleSignInButton.setOnClickListener { signInGoogle() }

        observeThemeMode()
        binding.themeChanger.setOnClickListener {
            themeChanger()
        }

        observeGetUser()
        loginUser()
        if (observeGetUser().isCompleted) observeGetUser().cancel()

    }

    private fun loginUser() {
        binding.login.setOnClickListener {
            viewModel.getUser(
                binding.username.text.toString().trim(),
                binding.password.text.toString().trim()
            )
        }
    }

    private fun themeChanger() {
        if (Utils.themeChanger(this)) {
            viewModel.setDarkMode(false)
        } else {
            viewModel.setDarkMode(true)
        }
    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        } else {
            SweetToast.error(this, task.exception.toString())
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        Utils.showProgressDialog("Signing in ", this)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val useranme = account.email.toString().split("@")[0]
                Utils.dismissProgressDialog()
                val user = User(
                    name = account.displayName.toString(),
                    email = account.email.toString(),
                    username = useranme,
                    password = "",
                    address = "",
                    profile = account.photoUrl.toString(),
                    phoneNumber = ""
                )
                startActivity(UserDetailsActivity.getIntent(this, user))

            } else {
                Utils.dismissProgressDialog()
                SweetToast.error(this, it.exception?.message.toString())
            }
        }
    }


    private fun observeThemeMode() {
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.getUIMode.collect {
                val mode = when (it) {
                    true -> {
                        binding.themeChanger.setImageDrawable(
                            AppCompatResources.getDrawable(
                                this@MainActivity,
                                R.drawable.ic_night
                            )
                        )
                        AppCompatDelegate.MODE_NIGHT_YES
                    }

                    false -> {
                        binding.themeChanger.setImageDrawable(
                            AppCompatResources.getDrawable(
                                this@MainActivity,
                                R.drawable.ic_day
                            )
                        )
                        AppCompatDelegate.MODE_NIGHT_NO
                    }
                }

                AppCompatDelegate.setDefaultNightMode(mode)
            }
        }
    }

    private fun observeGetUser() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.CREATED) {
            viewModel.userDetailsState.collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> {
                    }

                    is UiState.Success -> {
                        Utils.showProgressDialog(
                            "Sending OTP verification, Please wait ",
                            this@MainActivity
                        )
                        Timber.tag("User").e(Gson().toJson(uiState.data))
                        sendOpt(uiState.data)
                    }

                    is UiState.Error -> {
                        SweetToast.error(this@MainActivity, uiState.message)
                    }

                    is UiState.Empty -> {

                    }
                }
            }
        }
    }


    private fun sendOpt(user: User) {
        this.user = user
        if (user.phoneNumber?.isNotEmpty() == true) {
            if (user.phoneNumber!!.length == 10) {
                number = "+977${user.phoneNumber}"
                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(number)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(this@MainActivity)
                    .setCallbacks(callbacks)
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)

            } else {
                SweetToast.error(this@MainActivity, "Please Enter correct Number")

            }
        } else {
            SweetToast.error(this@MainActivity, "Please Enter Number")
        }
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            if (e is FirebaseAuthInvalidCredentialsException) {
                Utils.dismissProgressDialog()
                // Invalid request
                Timber.tag("tag").d(" onVerificationFailed: $e")
            } else if (e is FirebaseTooManyRequestsException) {
                Utils.dismissProgressDialog()
                // The SMS quota for the project has been exceeded
                e.localizedMessage?.let { SweetToast.error(this@MainActivity, it) }
                Timber.tag("tag").d(" onVerificationFailed: $e")
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            Utils.dismissProgressDialog()
            val intent = Intent(this@MainActivity, OTPActivity::class.java)
            intent.putExtra("OTP", verificationId)
            intent.putExtra("resendToken", token)
            intent.putExtra("phoneNumber", number)
            intent.putExtra("user", user)
            startActivity(intent)
            this@MainActivity.finishAffinity()
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            Utils.dismissProgressDialog()
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                SweetToast.success(this@MainActivity, "Authenticate Successfully")
                sendToMain()
            } else {
                // Sign in failed, display a message and update the UI
                Timber.tag("tad").d("signInWithPhoneAuthCredential: ${task.exception.toString()}")
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    // The verification code entered was invalid
                    SweetToast.error(this@MainActivity, "Invalid Code entered")
                }
            }

        }
    }

    private fun sendToMain() {
        Utils.dismissProgressDialog()
        startActivity(DashboardActivity.getIntent(this, user))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        this.finish()
    }


}