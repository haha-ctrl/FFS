package com.store.ffs.ui.activitis

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.store.ffs.R
import com.store.ffs.databinding.ActivityCheckoutBinding
import com.store.ffs.firestore.FirestoreClass
import com.store.ffs.model.Address
import com.store.ffs.model.CartItem
import com.store.ffs.model.Item
import com.store.ffs.model.Order
import com.store.ffs.ui.adapters.CartItemsListAdapter
import com.store.ffs.utils.Constants
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class CheckoutActivity : BaseActivity() {

    private var mAddressDetails: Address? = null
    private lateinit var binding:ActivityCheckoutBinding
    private lateinit var mItemsList: ArrayList<Item>
    private lateinit var mCartItemsList: ArrayList<CartItem>
    private var mSubTotal: Double = 0.0
    private var mTotalAmount: Double = 0.0
    private lateinit var mOrderDetails: Order
    private lateinit var token: String




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupActionBar()

        if(intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)) {
            mAddressDetails = intent.getParcelableExtra<Address>(Constants.EXTRA_SELECTED_ADDRESS)
        }

        if (mAddressDetails != null) {
            binding.tvCheckoutAddressType.text = mAddressDetails?.type
            binding.tvCheckoutFullName.text = mAddressDetails?.name
            binding.tvCheckoutAddress.text = "${mAddressDetails!!.address}, ${mAddressDetails!!.zipCode}"
            binding.tvCheckoutAdditionalNote.text = mAddressDetails?.additionalNote

            if (mAddressDetails?.otherDetails!!.isNotEmpty()) {
                binding.tvCheckoutOtherDetails.text = mAddressDetails?.otherDetails
            }
            binding.tvCheckoutMobileNumber.text = mAddressDetails?.mobileNumber
        }

        getItemList()

        binding.btnPlaceOrder.setOnClickListener {
            placeAnOrder()
        }

        token = intent.getStringExtra(Constants.USER_TOKEN).toString()
        Log.e("token.CheckoutActivity", token)
    }


    private fun setupActionBar() {
        val toolbar_checkout_activity = findViewById<Toolbar>(R.id.toolbar_checkout_activity)

        setSupportActionBar(toolbar_checkout_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_checkout_activity.setNavigationOnClickListener { onBackPressed() }
    }


    private fun getItemList() {

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getAllItemsList(this@CheckoutActivity)
    }


    fun successItemsListFromFireStore(itemsList: ArrayList<Item>) {

        // Initialize the global variable of all item list.
        // START
        mItemsList = itemsList
        // END

        // Call the function to get the latest cart items.
        // START
        getCartItemsList()
        // END
    }


    private fun getCartItemsList() {

        FirestoreClass().getCartList(this@CheckoutActivity)
    }


    fun successCartItemsList(cartList: ArrayList<CartItem>) {

        // Hide progress dialog.
        hideProgressDialog()

        val nf: NumberFormat = NumberFormat.getNumberInstance(Locale.GERMAN)
        val df = nf as DecimalFormat

        for (item in mItemsList) {
            for (cartItem in cartList) {
                if (item.item_id == cartItem.item_id) {
                    cartItem.stock_quantity = item.stock_quantity
                }
            }
        }

        // Initialize the cart list.
        // START
        mCartItemsList = cartList
        // END

        binding.rvCartListItems.layoutManager = LinearLayoutManager(this@CheckoutActivity)
        binding.rvCartListItems.setHasFixedSize(true)

        val cartListAdapter = CartItemsListAdapter(this@CheckoutActivity, mCartItemsList, false)
        binding.rvCartListItems.adapter = cartListAdapter

        // Replace the subTotal and totalAmount variables with the global variables.
        // START
        for (item in mCartItemsList) {

            val availableQuantity = item.stock_quantity.toInt()

            if (availableQuantity > 0) {
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()

                mSubTotal += (price * quantity)
            }
        }

        val formattedmSubTotal = df.format(mSubTotal)
        binding.tvCheckoutSubTotal.text = "${formattedmSubTotal}đ"
        // Here we have kept Shipping Charge is fixed as $10 but in your case it may cary. Also, it depends on the location and total amount.
        binding.tvCheckoutShippingCharge.text = "10.000đ"

        if (mSubTotal > 0) {
            binding.llCheckoutPlaceOrder.visibility = View.VISIBLE

            mTotalAmount = mSubTotal + 10000
            val formattedmTotalAmount = df.format(mTotalAmount)
            binding.tvCheckoutTotalAmount.text = "${formattedmTotalAmount}đ"
        } else {
            binding.llCheckoutPlaceOrder.visibility = View.GONE
        }
        // END
    }


    private fun placeAnOrder() {

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        // Now prepare the order details based on all the required details.
        // START
        mOrderDetails = Order(
            FirestoreClass().getCurrentUserID(),
            mCartItemsList,
            mAddressDetails!!,
            "Order ${System.currentTimeMillis()}",
            mCartItemsList[0].image,
            mSubTotal.toString(),
            "10000", // The Shipping Charge is fixed as 10.000 dong for now in our case.
            mTotalAmount.toString(),
            System.currentTimeMillis(),
            token
        )
        // END

        // Call the function to place the order in the cloud firestore.
        // START
        FirestoreClass().placeOrder(this@CheckoutActivity, mOrderDetails)
        // END
    }


    fun orderPlacedSuccess() {
        FirestoreClass().updateAllDetails(this@CheckoutActivity, mCartItemsList, mOrderDetails)

        val myTask = MyTask(this@CheckoutActivity, "You have an order", "Food order", Constants.ADMIN_TOKEN)
        myTask.execute()
    }


    fun allDetailsUpdatedSuccessfully() {

        // Move the piece of code from OrderPlaceSuccess to here.
        // START
        // Hide the progress dialog.
        hideProgressDialog()

        Toast.makeText(this@CheckoutActivity, "Your order placed successfully.", Toast.LENGTH_SHORT)
            .show()

        val intent = Intent(this@CheckoutActivity, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
        // END
    }
}