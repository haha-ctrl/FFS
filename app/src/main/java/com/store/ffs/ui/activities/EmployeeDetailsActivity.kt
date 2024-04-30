package com.store.ffs.ui.activitis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.store.ffs.R
import com.store.ffs.firestore.FirestoreClass
import com.store.ffs.model.CartItem
import com.store.ffs.model.Item
import com.store.ffs.utils.*
import com.google.firebase.firestore.FirebaseFirestore
import com.store.ffs.model.Employee
import com.store.ffs.model.User
import com.store.ffs.ui.activities.AddEditEmployeeActivity
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


class EmployeeDetailsActivity : BaseActivity(), View.OnClickListener {
    private var mEmployeeId: String = ""
    private lateinit var mEmployeeDetails: Employee
    private var mEmployeeOwnerId: String = ""
    private var user: User?= null
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_details)
        setupActionBar()

        user = DashboardActivity.getUser()
        token = intent.getStringExtra(Constants.USER_TOKEN).toString()

        if (intent.hasExtra(Constants.EXTRA_EMPLOYEE_ID)) {
            mEmployeeId = intent.getStringExtra(Constants.EXTRA_EMPLOYEE_ID)!!
            // Log.i("Item Id", mEmployeeId)
        }

        // var itemOwnerId: String = ""

        if (intent.hasExtra(Constants.EXTRA_EMPLOYEE_OWNER_ID)) {
            mEmployeeOwnerId =
                intent.getStringExtra(Constants.EXTRA_EMPLOYEE_OWNER_ID)!!
        }


        val iv_add_update_employee_detail = findViewById<ImageView>(R.id.iv_update_employee_detail)

        if (user?.isAdmin == true) {
            iv_add_update_employee_detail.visibility = View.VISIBLE
        } else {
            iv_add_update_employee_detail.visibility = View.GONE
        }

        getEmployeeDetails()

        iv_add_update_employee_detail.setOnClickListener(this)
    }


    private fun setupActionBar() {
        val toolbar_employee_details_activity = findViewById<Toolbar>(R.id.toolbar_employee_details_activity)

        setSupportActionBar(toolbar_employee_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_employee_details_activity.setNavigationOnClickListener { onBackPressed() }
    }


    fun employeeDetailsSuccess(employee: Employee) {
        val iv_employee_detail_image = findViewById<ImageView>(R.id.iv_employee_detail_image)
        val tv_employee_details_name = findViewById<MSPTextViewBold>(R.id.tv_employee_details_name)
        val tv_employee_phoneNum = findViewById<MSPTextView>(R.id.tv_employee_phoneNum)
        val tv_employee_email = findViewById<MSPTextView>(R.id.tv_employee_email)
        val tv_employee_address = findViewById<MSPTextView>(R.id.tv_employee_address)

        val nf: NumberFormat = NumberFormat.getNumberInstance(Locale.GERMAN)

        mEmployeeDetails = employee

        // Populate the item details in the UI.
        GlideLoader(this@EmployeeDetailsActivity).loadItemPicture(
            employee.employee_image,
            iv_employee_detail_image
        )

        tv_employee_details_name.text = employee.employee_name
        tv_employee_phoneNum.text = employee.employee_phone_number
        tv_employee_email.text = employee.employee_Email
        tv_employee_address.text = employee.employee_Address

        hideProgressDialog()
    }


    fun getEmployeeDetails() {

        // Show the item dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of FirestoreClass to get the item details.
        FirestoreClass().getEmployeeDetails(this@EmployeeDetailsActivity, mEmployeeId)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.iv_update_employee_detail -> {
                    val intent = Intent(this@EmployeeDetailsActivity, AddEditEmployeeActivity::class.java)
                    intent.putExtra(Constants.EXTRA_EMPLOYEE_INFO, mEmployeeDetails)
                    intent.putExtra(Constants.EXTRA_EMPLOYEE_ID, mEmployeeId)
                    startActivityForResult(intent, Constants.UPDATE_EMPLOYEE_REQUEST_CODE)
                }
            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.UPDATE_EMPLOYEE_REQUEST_CODE && resultCode == RESULT_OK) {
            // Reload item details
            getEmployeeDetails()
        }
    }
}