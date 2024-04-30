package com.store.ffs.ui.activitis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import com.store.ffs.R
import com.store.ffs.firestore.FirestoreClass
import com.store.ffs.model.User
import com.store.ffs.utils.*
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mUserDetails: User
    private var isAdmin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setupActionBar()

        val tv_edit = findViewById<MSPTextView>(R.id.tv_edit)
        val btn_logout = findViewById<MSPButton>(R.id.btn_logout)
        val ll_address = findViewById<LinearLayout>(R.id.ll_address)

        tv_edit.setOnClickListener(this@SettingsActivity)

        btn_logout.setOnClickListener(this@SettingsActivity)

        ll_address.setOnClickListener(this@SettingsActivity)

        if (intent.hasExtra(Constants.ADMIN_STATUS)) {
            isAdmin = intent.getBooleanExtra(Constants.ADMIN_STATUS, false)
        }
    }

    private fun setupActionBar() {
        val toolbar_settings_activity = findViewById<Toolbar>(R.id.toolbar_settings_activity)

        setSupportActionBar(toolbar_settings_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_settings_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getUserDetails() {

        // Show the progress dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of Firestore class to get the user details from firestore which is already created.
        FirestoreClass().getUserDetails(this@SettingsActivity)
    }

    fun userDetailsSuccess(user: User) {
        val tv_name = findViewById<MSPTextViewBold>(R.id.tv_name)
        val tv_gender = findViewById<MSPTextView>(R.id.tv_gender)
        val tv_email = findViewById<MSPTextView>(R.id.tv_email)
        val tv_mobile_number = findViewById<MSPTextView>(R.id.tv_mobile_number)
        val iv_user_photo = findViewById<ImageView>(R.id.iv_user_photo)

        mUserDetails = user
        // Set the user details to UI.
        // START
        // Hide the progress dialog

        hideProgressDialog()

        // Load the image using the Glide Loader class.
        GlideLoader(this@SettingsActivity).loadUserPicture(user.image, iv_user_photo)

        tv_name.text = "${user.firstName} ${user.lastName}"
        tv_gender.text = user.gender
        tv_email.text = user.email
        tv_mobile_number.text = "${user.mobile}"
        // END
    }


    override fun onResume() {
        super.onResume()

        getUserDetails()
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                // Call the User Profile Activity to add the Edit Profile feature to the app. Pass the user details through intent.
                // START
                R.id.tv_edit -> {
                    val intent = Intent(this@SettingsActivity, UserProfileActivity::class.java)
                    mUserDetails.isAdmin = isAdmin
                    intent.putExtra(Constants.EXTRA_USER_DETAILS, mUserDetails)
                    startActivity(intent)
                }
                // END

                // Add Logout feature when user clicks on logout button.
                // START
                R.id.btn_logout -> {

                    FirebaseAuth.getInstance().signOut()

                    val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                // END

                R.id.ll_address -> {
                    val intent = Intent(this@SettingsActivity, AddressListActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

}