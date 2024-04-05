package com.example.incomeexpensemanager.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.incomeexpensemanager.R
import com.example.incomeexpensemanager.databinding.ActivityOtpactivityBinding
import com.example.incomeexpensemanager.model.User
import com.example.incomeexpensemanager.utils.SweetToast
import com.example.incomeexpensemanager.utils.Utils
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class OTPActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityOtpactivityBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var OTP: String
    lateinit var user: User
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var phoneNumber: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        addTextChangeListener()
        resendOTPTvVisibility()
        binding.resendTextView.setOnClickListener {
            resendVerificationCode()
            resendOTPTvVisibility()
        }
        OTP = intent.getStringExtra("OTP").toString()
        resendToken = intent.getParcelableExtra("resendToken")!!
        phoneNumber = intent.getStringExtra("phoneNumber")!!
        user = intent.getSerializableExtra("user") as User


        binding.verifyOTPBtn.setOnClickListener {
            //collect otp from all the edit texts
            val typedOTP =
                (binding.otpEditText1.text.toString() + binding.otpEditText2.text.toString() + binding.otpEditText3.text.toString()
                        + binding.otpEditText4.text.toString() + binding.otpEditText5.text.toString() + binding.otpEditText6.text.toString())

            if (typedOTP.isNotEmpty()) {
                if (typedOTP.length == 6) {
                    val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                        OTP, typedOTP
                    )
                    Utils.showProgressDialog("Verifying OTP,Please wait", this)
                    signInWithPhoneAuthCredential(credential)
                } else {
                    Toast.makeText(this, "Please Enter Correct OTP", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please Enter OTP", Toast.LENGTH_SHORT).show()
            }


        }


    }

    private fun resendVerificationCode() {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)
            .setForceResendingToken(resendToken)// OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {

            if (e is FirebaseAuthInvalidCredentialsException) {
                Utils.dismissProgressDialog()
                // Invalid request
                Timber.tag("tad").d("signInWithPhoneAuthCredential: $e")
            } else if (e is FirebaseTooManyRequestsException) {
                Utils.dismissProgressDialog()
                // The SMS quota for the project has been exceeded
                Timber.tag("tad").d("signInWithPhoneAuthCredential: $e")
            }
            binding.otpProgressBar.visibility = View.VISIBLE
            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            OTP = verificationId
            resendToken = token
        }
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Utils.dismissProgressDialog()
                    Toast.makeText(this, "Authenticate Successfully", Toast.LENGTH_SHORT).show()
                    sendToMain()
                } else {
                    Utils.dismissProgressDialog()
                    Timber.tag("tad")
                        .d("signInWithPhoneAuthCredential: ${task.exception.toString()}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        SweetToast.error(this@OTPActivity, "Invalid Code entered")
                    }
                    // Update UI
                }
            }
    }

    private fun resendOTPTvVisibility() = with(binding) {
        otpEditText1.setText("")
        otpEditText2.setText("")
        otpEditText3.setText("")
        otpEditText4.setText("")
        otpEditText5.setText("")
        otpEditText6.setText("")
        resendTextView.visibility = View.INVISIBLE
        resendTextView.isEnabled = false

        Handler(Looper.myLooper()!!).postDelayed({
            resendTextView.visibility = View.VISIBLE
            resendTextView.isEnabled = true
        }, 60000)


    }

    private fun addTextChangeListener() = with(binding) {
        otpEditText1.addTextChangedListener(EditTextWatcher(otpEditText1))
        otpEditText2.addTextChangedListener(EditTextWatcher(otpEditText2))
        otpEditText3.addTextChangedListener(EditTextWatcher(otpEditText3))
        otpEditText4.addTextChangedListener(EditTextWatcher(otpEditText4))
        otpEditText5.addTextChangedListener(EditTextWatcher(otpEditText5))
        otpEditText6.addTextChangedListener(EditTextWatcher(otpEditText6))
    }


    inner class EditTextWatcher(private val view: View) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            val text = p0.toString()
            when (view.id) {
                R.id.otpEditText1 -> if (text.length == 1) binding.otpEditText2.requestFocus()
                R.id.otpEditText2 -> if (text.length == 1) binding.otpEditText3.requestFocus() else if (text.isEmpty()) binding.otpEditText1.requestFocus()
                R.id.otpEditText3 -> if (text.length == 1) binding.otpEditText4.requestFocus() else if (text.isEmpty()) binding.otpEditText2.requestFocus()
                R.id.otpEditText4 -> if (text.length == 1) binding.otpEditText5.requestFocus() else if (text.isEmpty()) binding.otpEditText3.requestFocus()
                R.id.otpEditText5 -> if (text.length == 1) binding.otpEditText6.requestFocus() else if (text.isEmpty()) binding.otpEditText4.requestFocus()
                R.id.otpEditText6 -> if (text.isEmpty()) binding.otpEditText5.requestFocus()

            }
        }

    }


    private fun sendToMain() {
        startActivity(DashboardActivity.getIntent(this, user))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        this.finish()
    }
}