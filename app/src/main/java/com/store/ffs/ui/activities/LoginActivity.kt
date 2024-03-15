package com.store.ffs.ui.activitis

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.store.ffs.R
import com.store.ffs.firestore.FirestoreClass
import com.store.ffs.model.User
import com.store.ffs.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.store.ffs.firestore.RealtimeClass

class LoginActivity : BaseActivity(), View.OnClickListener {


    private val database = Firebase.database
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        val tv_forgot_password = findViewById<MSPTextView>(R.id.tv_forgot_password)
        val btn_login = findViewById<MSPButton>(R.id.btn_login)
        val tv_register = findViewById<MSPTextViewBold>(R.id.tv_register)

        tv_forgot_password.setOnClickListener(this)
        btn_login.setOnClickListener(this)
        tv_register.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.tv_forgot_password -> {
                    val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                    startActivity(intent)
                }

                R.id.btn_login -> {
                    logInRegisteredUser()
                }

                R.id.tv_register -> {
                    val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }


    private fun validateLoginDetails(): Boolean {
        val et_email = findViewById<MSPEditText>(R.id.et_email)
        val et_password = findViewById<MSPEditText>(R.id.et_password)
        return when {

            TextUtils.isEmpty(et_email.text.toString().trim { it <= ' ' }) -> {

                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(et_password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                true
            }

        }
    }


    private fun logInRegisteredUser() {
        val et_email = findViewById<MSPEditText>(R.id.et_email)
        val et_password = findViewById<MSPEditText>(R.id.et_password)

        if (validateLoginDetails()) {

            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            // Get the text from editText and trim the space
            val email = et_email.text.toString().trim { it <= ' ' }
            val password = et_password.text.toString().trim { it <= ' ' }

            // Log-In using FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        FirestoreClass().getUserDetails(this@LoginActivity)
                    } else {

                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }


    /**
     * A function to notify user that logged in success and get the user details from the FireStore database after authentication.
     */
    fun userLoggedInSuccess(user: User, isAdmin: Boolean) {
        // Hide the progress dialog.

        // Print the user details in the log as of now.
        // Log.i("First Name: ", user.firstName)
        // Log.i("Last Name: ", user.lastName)
        // Log.i("Email: ", user.email)

        // Redirect the user to the UserProfile screen if it is incomplete otherwise to the Main screen.
        // START
        user.isAdmin = isAdmin
        if (user.profileCompleted == 0) {
            hideProgressDialog()
            // If the user profile is incomplete then launch the UserProfileActivity.
            val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)

            RealtimeClass().saveUser(this@LoginActivity, user)
            startActivity(intent)
        } else {

            // Redirect the user to Main Screen after log in.
            val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)


            hideProgressDialog()
            startActivity(intent)
        }
        finish()
    }

    fun saveUserSuccess(user: User) {
        Toast.makeText(this@LoginActivity, "Welcome ${user.firstName} ${user.lastName}", Toast.LENGTH_SHORT ).show()
        // hideProgressDialog()
    }
}