package com.store.ffs.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.store.ffs.R
import com.store.ffs.firestore.FirestoreClass
import com.store.ffs.model.Employee
import com.store.ffs.ui.activitis.BaseActivity
import com.store.ffs.utils.Constants
import com.store.ffs.utils.GlideLoader
import com.store.ffs.utils.MSPButton
import com.store.ffs.utils.MSPEditText
import com.theartofdev.edmodo.cropper.CropImage
import java.io.IOException

class AddEditEmployeeActivity: BaseActivity(), View.OnClickListener {

    private var mSelectedImageFileUri: Uri? = null
    private var mEmployeeImageURL: String = ""
    private var mEmployeeInfo: Employee? = null
    private var extraEmployee: Boolean = false
    private var updateCalledInUpload: Boolean = false
    private var mEmployeeId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_employee)
        setupActionBar()

        val iv_add_update_employee_img = findViewById<ImageView>(R.id.iv_add_update_employee_image)
        iv_add_update_employee_img.setOnClickListener(this)

        val btn_submit_add_employee_info = findViewById<MSPButton>(R.id.btn_submit_employee_info)
        btn_submit_add_employee_info.setOnClickListener(this)

        if (intent.hasExtra(Constants.EXTRA_EMPLOYEE_INFO)) {
            // Get the user details from intent as a ParcelableExtra.
            mEmployeeInfo = intent.getParcelableExtra(Constants.EXTRA_EMPLOYEE_INFO)!!
            extraEmployee = true
            mEmployeeId = intent.getStringExtra(Constants.EXTRA_EMPLOYEE_ID)!!
        }


        val tvTitle = findViewById<TextView>(R.id.tv_add_edit_employee_title)
        val ivEmployeeImage = findViewById<ImageView>(R.id.iv_employee_image)
        val etEmployeeName = findViewById<MSPEditText>(R.id.et_employee_name)
        val etEmployeePhoneNum = findViewById<MSPEditText>(R.id.et_employee_PhoneNum)
        val etEmployeeEmail = findViewById<MSPEditText>(R.id.et_employee_email)
        val etEmployeeAddress = findViewById<MSPEditText>(R.id.et_employee_address)

        if (mEmployeeInfo != null) {
            tvTitle.text = resources.getString(R.string.title_complete_employee_information)
            GlideLoader(this@AddEditEmployeeActivity).loadUserPicture(
                mEmployeeInfo!!.employee_image,
                ivEmployeeImage
            )
            iv_add_update_employee_img.setImageDrawable(
                ContextCompat.getDrawable(
                    this@AddEditEmployeeActivity,
                    R.drawable.ic_vector_edit
                )
            )
            etEmployeeName.setText(mEmployeeInfo!!.employee_name)
            etEmployeePhoneNum.setText(mEmployeeInfo!!.employee_phone_number)
            etEmployeeEmail.setText(mEmployeeInfo!!.employee_Email)
            etEmployeeAddress.setText(mEmployeeInfo!!.employee_Address)
        }

    }

    private fun setupActionBar() {
        val toolbar_add_edit_employee_activity =
            findViewById<Toolbar>(R.id.toolbar_add_edit_employee_activity)
        setSupportActionBar(toolbar_add_edit_employee_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_add_edit_employee_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                // The permission code is similar to the user profile image selection.
                R.id.iv_add_update_employee_image -> {
                    if (ContextCompat.checkSelfPermission(
                            this@AddEditEmployeeActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Constants.showImageChooser(this@AddEditEmployeeActivity)
                    } else {
                        /*Requests permissions to be granted to this application. These permissions
                         must be requested in your manifest, they should not be granted to your app,
                         and they should have protection level*/
                        ActivityCompat.requestPermissions(
                            this@AddEditEmployeeActivity,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                R.id.btn_submit_employee_info -> {
                    if (validateEmployeeInfo()) {
                        uploadEmployeeImage()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@AddEditEmployeeActivity)
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val iv_add_update_employee = findViewById<ImageView>(R.id.iv_add_update_employee_image)
        val iv_employee_image = findViewById<ImageView>(R.id.iv_employee_image)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {

                val resultUri = result.uri
                mSelectedImageFileUri = resultUri

                // Replace the add icon with edit icon once the image is selected.
                iv_add_update_employee.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@AddEditEmployeeActivity,
                        R.drawable.ic_vector_edit
                    )
                )

                try {
                    // Load the item image in the ImageView.
                    GlideLoader(this@AddEditEmployeeActivity).loadUserPicture(
                        mSelectedImageFileUri!!,
                        iv_employee_image
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                // Handle crop error
                Log.e("CropError", error.message ?: "Unknown error")
            }
        }
    }

    private fun validateEmployeeInfo(): Boolean {
        val et_employee_name = findViewById<MSPEditText>(R.id.et_employee_name)
        val et_employee_phoneNum = findViewById<MSPEditText>(R.id.et_employee_PhoneNum)
        val et_employee_email = findViewById<MSPEditText>(R.id.et_employee_email)
        val et_employee_address = findViewById<MSPEditText>(R.id.et_employee_address)

        return when {

            mSelectedImageFileUri == null && !extraEmployee -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_insert_employee_image), true)
                false
            }

            TextUtils.isEmpty(et_employee_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_employee_name), true)
                false
            }

            TextUtils.isEmpty(et_employee_phoneNum.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_employee_email), true)
                false
            }

            TextUtils.isEmpty(et_employee_email.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_employee_email),
                    true
                )
                false
            }

            TextUtils.isEmpty(et_employee_address.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_employee_address),
                    true
                )
                false
            }
            else -> {
                true
            }
        }
    }

    private fun uploadEmployeeImage() {
        showProgressDialog(resources.getString(R.string.please_wait))
        if (mSelectedImageFileUri != null) {
            FirestoreClass().uploadImageToCloudStorage(
                this@AddEditEmployeeActivity,
                mSelectedImageFileUri,
                Constants.EMPLOYEE_IMAGE
            )
        } else {
            if (!extraEmployee) {
                uploadEmployeeInfo()
            } else {
                upDateEmployeeInfo()
            }
        }
    }

    fun imageUploadSuccess(imageURL: String) {

        mEmployeeImageURL = imageURL

        uploadEmployeeInfo()

    }

    private fun uploadEmployeeInfo() {
        val et_employee_name = findViewById<MSPEditText>(R.id.et_employee_name)
        val et_employee_phoneNum = findViewById<MSPEditText>(R.id.et_employee_PhoneNum)
        val et_employee_email = findViewById<MSPEditText>(R.id.et_employee_email)
        val et_employee_address = findViewById<MSPEditText>(R.id.et_employee_address)

        // Get the logged in username from the SharedPreferences that we have stored at a time of login.
        val username =
            this.getSharedPreferences(Constants.MY_EMPLOYEE_LIST, Context.MODE_PRIVATE)
                .getString(Constants.LOGGED_IN_USERNAME, "")!!

        // Here we get the text from editText and trim the space
        val employee = Employee (
            FirestoreClass().getCurrentUserID(),
            et_employee_name.text.toString().trim { it <= ' ' },
            et_employee_phoneNum.text.toString().trim { it <= ' ' },
            et_employee_email.text.toString().trim { it <= ' ' },
            et_employee_address.text.toString().trim { it <= ' ' },

            mEmployeeImageURL
        )
        FirestoreClass().uploadEmployeeInfo(this@AddEditEmployeeActivity, employee)

    }

    private fun upDateEmployeeInfo() {
        val et_employee_name = findViewById<MSPEditText>(R.id.et_employee_name)
        val et_employee_phoneNum = findViewById<MSPEditText>(R.id.et_employee_PhoneNum)
        val et_employee_email = findViewById<MSPEditText>(R.id.et_employee_email)
        val et_employee_address = findViewById<MSPEditText>(R.id.et_employee_address)

        // Get the logged in username from the SharedPreferences that we have stored at a time of login.
        val username =
            this.getSharedPreferences(Constants.MY_EMPLOYEE_LIST, Context.MODE_PRIVATE)
                .getString(Constants.LOGGED_IN_USERNAME, "")!!

        // Create a HashMap to hold item details
        val employeeInfo = HashMap<String, Any>()

        // Populate the HashMap with item details
        employeeInfo[Constants.USER_ID] = FirestoreClass().getCurrentUserID()
        employeeInfo[Constants.USERNAME] = username

        val employeeName = et_employee_name.text.toString().trim()
        if (employeeName != mEmployeeInfo?.employee_name) {
            employeeInfo[Constants.EMPLOYEE_NAME] = employeeName
        }

        val employeePhoneNum = et_employee_phoneNum.text.toString().trim()
        if (employeePhoneNum != mEmployeeInfo?.employee_phone_number) {
            employeeInfo[Constants.EMPLOYEE_PHONE_NUM] = employeePhoneNum
        }

        val employeeEmail = et_employee_email.text.toString().trim()
        if (employeeEmail != mEmployeeInfo?.employee_Email) {
            employeeInfo[Constants.EMPLOYEE_EMAIL] = employeeEmail
        }

        val employeeAddress = et_employee_address.text.toString().trim()
        if (employeeAddress != mEmployeeInfo?.employee_Address) {
            employeeInfo[Constants.EMPLOYEE_ADDRESS] = employeeAddress
        }

        employeeInfo[Constants.EMPLOYEE_ID] = mEmployeeId
        if (mSelectedImageFileUri != null) {
            employeeInfo[Constants.EMPLOYEE_IMAGE_URL] = mEmployeeImageURL
        }

        FirestoreClass().updateEmployeeInfo(this@AddEditEmployeeActivity, mEmployeeId, employeeInfo)
    }

    fun employeeUploadSuccess(employeeId: String) {

        // Hide the progress dialog
        // hideProgressDialog()

        Toast.makeText(
            this@AddEditEmployeeActivity,
            resources.getString(R.string.employee_info_added_successfully),
            Toast.LENGTH_SHORT
        ).show()

        val employeeInfo = HashMap<String, Any>()
        employeeInfo[Constants.EMPLOYEE_ID] = employeeId
        FirestoreClass().updateEmployeeInfo(this@AddEditEmployeeActivity, employeeId, employeeInfo)

        updateCalledInUpload = true
        // finish()
    }

    fun employeeUpdateSuccess() {

        // Hide the progress dialog
        hideProgressDialog()

        if (!updateCalledInUpload) {
            Toast.makeText(
                this@AddEditEmployeeActivity,
                resources.getString(R.string.employee_info_updated_successfully),
                Toast.LENGTH_SHORT
            ).show()
        }

        setResult(Activity.RESULT_OK)
        finish()
    }
}