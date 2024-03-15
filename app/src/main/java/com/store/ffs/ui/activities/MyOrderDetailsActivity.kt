package com.store.ffs.ui.activitis

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.store.ffs.R
import com.store.ffs.databinding.ActivityMyOrderDetailsBinding
import com.store.ffs.firestore.FirestoreClass
import com.store.ffs.model.Order
import com.store.ffs.model.User
import com.store.ffs.ui.adapters.CartItemsListAdapter
import com.store.ffs.utils.Constants
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MyOrderDetailsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMyOrderDetailsBinding
    private var user: User?= null
    private var myOrderDetails: Order = Order()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyOrderDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupActionBar()


        if (intent.hasExtra(Constants.EXTRA_MY_ORDER_DETAILS)) {
            myOrderDetails =
                intent.getParcelableExtra<Order>(Constants.EXTRA_MY_ORDER_DETAILS)!!
        }

        user = DashboardActivity.getUser()

        setupUI(myOrderDetails)

        binding.btnCancel.setOnClickListener(this)
        binding.btnAccept.setOnClickListener(this)
        binding.btnReject.setOnClickListener(this)
        binding.btnDelivered.setOnClickListener(this)
        binding.btnConfirmed.setOnClickListener(this)
        binding.btnReturn.setOnClickListener(this)
    }

    private fun setupActionBar() {
        val toolbar_my_order_details_activity = findViewById<Toolbar>(R.id.toolbar_my_order_details_activity)

        setSupportActionBar(toolbar_my_order_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_my_order_details_activity.setNavigationOnClickListener { onBackPressed() }
    }


    private fun setupUI(orderDetails: Order) {
        val nf: NumberFormat = NumberFormat.getNumberInstance(Locale.GERMAN)
        val df = nf as DecimalFormat

        binding.tvOrderDetailsId.text = orderDetails.title

        // Set the Date in the UI.
        // START
        // Date Format in which the date will be displayed in the UI.
        val dateFormat = "dd MMM yyyy HH:mm"
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = orderDetails.order_datetime

        val orderDateTime = formatter.format(calendar.time)
        binding.tvOrderDetailsDate.text = orderDateTime
        // END

        // Set the order status based on the time for now.
        // START
        // Get the difference between the order date time and current date time in hours.
        // If the difference in hours is 1 or less then the order status will be PENDING.
        // If the difference in hours is 2 or greater then 1 then the order status will be PROCESSING.
        // And, if the difference in hours is 3 or greater then the order status will be DELIVERED.

        val diffInMilliSeconds: Long = System.currentTimeMillis() - orderDetails.order_datetime
        val diffInHours: Long = TimeUnit.MILLISECONDS.toHours(diffInMilliSeconds)
        Log.e("Difference in Hours", "$diffInHours")

        if (orderDetails.status == Constants.ORDER_IN_PROCESS) {
            binding.tvOrderStatus.text = resources.getString(R.string.order_status_in_process)
            binding.tvOrderStatus.setTextColor(
                ContextCompat.getColor(
                    this@MyOrderDetailsActivity,
                    R.color.colorOrderStatusInProcess
                )
            )
        } else if (orderDetails.status == Constants.ORDER_REJECT) {
            binding.tvOrderStatus.text = resources.getString(R.string.order_status_reject)
        }
        else if (orderDetails.status == Constants.ORDER_DELIVERED) {
            binding.tvOrderStatus.text = resources.getString(R.string.order_status_delivered)
            binding.tvOrderStatus.setTextColor(
                ContextCompat.getColor(
                    this@MyOrderDetailsActivity,
                    R.color.colorOrderStatusDelivered
                )
            )
        } else if (orderDetails.status == Constants.ORDER_CONFIRMED) {
            binding.tvOrderStatus.text = resources.getString(R.string.order_status_confirmed)
            binding.tvOrderStatus.setTextColor(
                ContextCompat.getColor(
                    this@MyOrderDetailsActivity,
                    R.color.colorOrderStatusConfirmed
                )
            )
        } else if (orderDetails.status == Constants.ORDER_RETURN) {
            binding.tvOrderStatus.text = resources.getString(R.string.order_status_return)
            binding.tvOrderStatus.setTextColor(
                ContextCompat.getColor(
                    this@MyOrderDetailsActivity,
                    R.color.colorOrderStatusCancel
                )
            )
        } else if (orderDetails.status == Constants.ORDER_CANCEL) {
            binding.tvOrderStatus.text = resources.getString(R.string.order_status_cancel)
            binding.tvOrderStatus.setTextColor(
                ContextCompat.getColor(
                    this@MyOrderDetailsActivity,
                    R.color.colorOrderStatusCancel
                )
            )
        }


        binding.rvMyOrderItemsList.layoutManager = LinearLayoutManager(this@MyOrderDetailsActivity)
        binding.rvMyOrderItemsList.setHasFixedSize(true)

        val cartListAdapter =
            CartItemsListAdapter(this@MyOrderDetailsActivity, orderDetails.items, false)
        binding.rvMyOrderItemsList.adapter = cartListAdapter


        binding.tvMyOrderDetailsAddressType.text = orderDetails.address.type
        binding.tvMyOrderDetailsFullName.text = orderDetails.address.name
        binding.tvMyOrderDetailsAddress.text =
            "${orderDetails.address.address}, ${orderDetails.address.zipCode}"
        binding.tvMyOrderDetailsAdditionalNote.text = orderDetails.address.additionalNote

        if (orderDetails.address.otherDetails.isNotEmpty()) {
            binding.tvMyOrderDetailsOtherDetails.visibility = View.VISIBLE
            binding.tvMyOrderDetailsOtherDetails.text = orderDetails.address.otherDetails
        } else {
            binding.tvMyOrderDetailsOtherDetails.visibility = View.GONE
        }
        binding.tvMyOrderDetailsMobileNumber.text = orderDetails.address.mobileNumber

        val orderDetailsSubTotalAmountNumeric = orderDetails.sub_total_amount.toDoubleOrNull() ?: 0.0
        val formattedOrderDetailsSubTotalAmount = df.format(orderDetailsSubTotalAmountNumeric)
        val orderDetailsShippingChargeNumeric = orderDetails.shipping_charge.toDoubleOrNull() ?: 0.0
        val formattedOrderDetailsShippingCharge = df.format(orderDetailsShippingChargeNumeric)
        val orderDetailsTotalAmountNumeric = orderDetails.total_amount.toDoubleOrNull() ?: 0.0
        val formattedOrderDetailsTotalAmount = df.format(orderDetailsTotalAmountNumeric)
        binding.tvOrderDetailsSubTotal.text = "${formattedOrderDetailsSubTotalAmount}đ"
        binding.tvOrderDetailsShippingCharge.text = "${formattedOrderDetailsShippingCharge}đ"
        binding.tvOrderDetailsTotalAmount.text = "${formattedOrderDetailsTotalAmount}đ"


        if (user?.isAdmin == true) {
            binding.btnCancel.visibility = View.GONE
            if (orderDetails.status == Constants.ORDER_PENDING) {
                binding.btnAccept.visibility = View.VISIBLE
                binding.btnReject.visibility = View.VISIBLE
                binding.btnDelivered.visibility = View.GONE
                binding.btnConfirmed.visibility = View.GONE
                binding.btnReturn.visibility = View.GONE
            } else if (orderDetails.status == Constants.ORDER_IN_PROCESS){
                binding.btnAccept.visibility = View.GONE
                binding.btnReject.visibility = View.GONE
                binding.btnDelivered.visibility = View.VISIBLE
                binding.btnConfirmed.visibility = View.GONE
                binding.btnReturn.visibility = View.GONE
            } else {
                binding.btnAccept.visibility = View.GONE
                binding.btnReject.visibility = View.GONE
                binding.btnDelivered.visibility = View.GONE
                binding.btnConfirmed.visibility = View.GONE
                binding.btnReturn.visibility = View.GONE
            }
        } else {
            if (orderDetails.status == Constants.ORDER_PENDING) {
                binding.btnCancel.visibility = View.VISIBLE
                binding.btnAccept.visibility = View.GONE
                binding.btnReject.visibility = View.GONE
                binding.btnDelivered.visibility = View.GONE
                binding.btnConfirmed.visibility = View.GONE
                binding.btnReturn.visibility = View.GONE
            }
            else if (orderDetails.status == Constants.ORDER_DELIVERED) {
                binding.btnCancel.visibility = View.GONE
                binding.btnAccept.visibility = View.GONE
                binding.btnReject.visibility = View.GONE
                binding.btnDelivered.visibility = View.GONE
                binding.btnConfirmed.visibility = View.VISIBLE
                binding.btnReturn.visibility = View.VISIBLE
            } else {
                binding.btnCancel.visibility = View.GONE
                binding.btnAccept.visibility = View.GONE
                binding.btnReject.visibility = View.GONE
                binding.btnDelivered.visibility = View.GONE
                binding.btnConfirmed.visibility = View.GONE
                binding.btnReturn.visibility = View.GONE
            }
        }
    }


    private fun cancelOrder(orderDetails: Order) {
        orderDetails.status = Constants.ORDER_CANCEL
        showProgressDialog(resources.getString(R.string.please_wait))

        val updateFields = mapOf(Constants.ORDER_STATUS to orderDetails.status)
        FirestoreClass().updateOrderStatus(this, orderDetails, updateFields)
    }

    private fun acceptOrder(orderDetails: Order) {
        orderDetails.status = Constants.ORDER_IN_PROCESS
        showProgressDialog(resources.getString(R.string.please_wait))

        val updateFields = mapOf(Constants.ORDER_STATUS to orderDetails.status)
        FirestoreClass().updateOrderStatus(this, orderDetails, updateFields)
    }

    private fun rejectOrder(orderDetails: Order) {
        orderDetails.status = Constants.ORDER_REJECT
        showProgressDialog(resources.getString(R.string.please_wait))

        val updateFields = mapOf(Constants.ORDER_STATUS to orderDetails.status)
        FirestoreClass().updateOrderStatus(this, orderDetails, updateFields)
    }

    private fun deliveredOrder(orderDetails: Order) {
        orderDetails.status = Constants.ORDER_DELIVERED
        showProgressDialog(resources.getString(R.string.please_wait))

        val updateFields = mapOf(Constants.ORDER_STATUS to orderDetails.status)
        FirestoreClass().updateOrderStatus(this, orderDetails, updateFields)
    }


    private fun confirmedOrder(orderDetails: Order) {
        orderDetails.status = Constants.ORDER_CONFIRMED
        showProgressDialog(resources.getString(R.string.please_wait))

        val updateFields = mapOf(Constants.ORDER_STATUS to orderDetails.status)
        FirestoreClass().updateOrderStatus(this, orderDetails, updateFields)
        FirestoreClass().setSoldItems(orderDetails)
    }

    private fun returnOrder(orderDetails: Order) {
        orderDetails.status = Constants.ORDER_RETURN
        showProgressDialog(resources.getString(R.string.please_wait))

        val updateFields = mapOf(Constants.ORDER_STATUS to orderDetails.status)
        FirestoreClass().updateOrderStatus(this, orderDetails, updateFields)
        FirestoreClass().setSoldItems(orderDetails)
    }
    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.btn_cancel -> {
                    cancelOrder(myOrderDetails)
                }

                R.id.btn_accept -> {
                    acceptOrder(myOrderDetails)
                }

                R.id.btn_reject -> {
                    rejectOrder(myOrderDetails)
                }

                R.id.btn_delivered -> {
                    deliveredOrder(myOrderDetails)
                }

                R.id.btn_confirmed -> {
                    confirmedOrder(myOrderDetails)
                }

                R.id.btn_return -> {
                    returnOrder(myOrderDetails)
                }
            }
        }
    }




    fun orderStatusUpdateSuccess(orderDetails: Order) {
        hideProgressDialog()
        Toast.makeText(this@MyOrderDetailsActivity, "Order status updated successfully", Toast.LENGTH_SHORT).show()
        // Update UI or perform any additional actions after the order status is successfully updated.
        setupUI(orderDetails)
    }
}