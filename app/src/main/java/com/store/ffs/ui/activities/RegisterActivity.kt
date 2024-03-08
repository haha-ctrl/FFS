package com.store.ffs.ui.activitis

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.widget.AppCompatCheckBox
import com.store.ffs.R
import com.store.ffs.utils.MSPButton
import com.store.ffs.utils.MSPEditText
import com.store.ffs.utils.MSPTextViewBold
import com.store.ffs.firestore.FirestoreClass
import com.store.ffs.model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class RegisterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        setupActionBar()

        val tv_login = findViewById<MSPTextViewBold>(R.id.tv_login)
        tv_login.setOnClickListener {
            onBackPressed()
        }

        val btn_register = findViewById<MSPButton>(R.id.btn_register)
        btn_register.setOnClickListener {
            registerUser()
        }
    }

    private fun setupActionBar() {
        val toolbar_register_activity = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_register_activity)
        setSupportActionBar(toolbar_register_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbar_register_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun validateRegisterDetails(): Boolean {
        val et_first_name = findViewById<MSPEditText>(R.id.et_first_name)
        val et_last_name = findViewById<MSPEditText>(R.id.et_last_name)
        val et_email = findViewById<MSPEditText>(R.id.et_email)
        val et_password = findViewById<MSPEditText>(R.id.et_password)
        val et_confirm_password = findViewById<MSPEditText>(R.id.et_confirm_password)
        val cb_terms_and_condition = findViewById<AppCompatCheckBox>(R.id.cb_terms_and_condition)


        return when {
            TextUtils.isEmpty(et_first_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }

            TextUtils.isEmpty(et_last_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }

            TextUtils.isEmpty(et_email.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(et_password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }

            et_password.length() < 6 -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_password_too_short), true)
                false
            }

            TextUtils.isEmpty(et_confirm_password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_confirm_password), true)
                false
            }

            et_password.text.toString().trim { it <= ' ' } != et_confirm_password.text.toString()
                .trim { it <= ' ' } -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_password_and_confirm_password_mismatch), true)
                false
            }
            !cb_terms_and_condition.isChecked -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_agree_terms_and_condition), true)
                false
            }
            else -> {
                // showErrorSnackBar(resources.getString(R.string.register_successful), false)
                true
            }
        }
    }

    private fun registerUser() {
        val et_email = findViewById<MSPEditText>(R.id.et_email)
        val et_password = findViewById<MSPEditText>(R.id.et_password)
        val et_first_name = findViewById<MSPEditText>(R.id.et_first_name)
        val et_last_name = findViewById<MSPEditText>(R.id.et_last_name)

        if (validateRegisterDetails()) {

            showProgressDialog(resources.getString(R.string.please_wait))


            val email: String = et_email.text.toString().trim { it <= ' ' }
            val password: String = et_password.text.toString().trim { it <= ' ' }

            // Create an instance and create a register a user with email and password.
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->

                        // If the registration is successfully done
                        if (task.isSuccessful) {

                            // Firebase registered user
                            val firebaseUser: FirebaseUser = task.result!!.user!!

                            val user = User(
                                firebaseUser.uid,
                                et_first_name.text.toString().trim { it <= ' ' },
                                et_last_name.text.toString().trim { it <= ' ' },
                                et_email.text.toString().trim { it <= ' ' }
                            )

                            FirestoreClass().registerUser(this@RegisterActivity, user)

//                            Handler(Looper.getMainLooper()).postDelayed({
//                                FirebaseAuth.getInstance().signOut()
//                                finish()
//                            }, 1000)

                        } else {
                            hideProgressDialog()
                            // If the registering is not successful then show error message.
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    })
        }
    }

    fun userRegistrationSuccess() {
        hideProgressDialog()

        Toast.makeText(
            this@RegisterActivity,
            resources.getString(R.string.register_success),
            Toast.LENGTH_SHORT
        ).show()


        FirebaseAuth.getInstance().signOut()
        finish()
    }
}