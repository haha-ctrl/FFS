package com.store.ffs.ui.activitis

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.store.ffs.R
import com.store.ffs.firestore.FirestoreClass
import com.store.ffs.model.CartItem
import com.store.ffs.model.Item
import com.store.ffs.ui.adapters.CartItemsListAdapter
import com.store.ffs.utils.Constants
import com.store.ffs.utils.MSPButton
import com.store.ffs.utils.MSPTextView
import com.store.ffs.utils.MSPTextViewBold
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class CartListActivity : BaseActivity() {

    private lateinit var mItemsList: ArrayList<Item>
    private lateinit var mCartListItems: ArrayList<CartItem>
    private lateinit var token: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_list)

        setupActionBar()

        token = intent.getStringExtra(Constants.USER_TOKEN).toString()
        Log.e("token.CartListActivity", token)
        val btn_checkout = findViewById<MSPButton>(R.id.btn_checkout)
        btn_checkout.setOnClickListener {
            val intent = Intent(this@CartListActivity, AddressListActivity::class.java)
            intent.putExtra(Constants.EXTRA_SELECT_ADDRESS, true)
            intent.putExtra(Constants.USER_TOKEN, token)
            startActivity(intent)
        }
    }


    private fun setupActionBar() {
        val toolbar_cart_list_activity = findViewById<Toolbar>(R.id.toolbar_cart_list_activity)

        setSupportActionBar(toolbar_cart_list_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_cart_list_activity.setNavigationOnClickListener { onBackPressed() }
    }


    fun successCartItemsList(cartList: ArrayList<CartItem>) {
        val rv_cart_items_list = findViewById<RecyclerView>(R.id.rv_cart_items_list)
        val ll_checkout = findViewById<LinearLayout>(R.id.ll_checkout)
        val tv_no_cart_item_found = findViewById<MSPTextView>(R.id.tv_no_cart_item_found)
        val tv_sub_total = findViewById<MSPTextView>(R.id.tv_sub_total)
        val tv_shipping_charge = findViewById<MSPTextView>(R.id.tv_shipping_charge)
        val tv_total_amount = findViewById<MSPTextViewBold>(R.id.tv_total_amount)

        val nf: NumberFormat = NumberFormat.getNumberInstance(Locale.GERMAN)
        val df = nf as DecimalFormat
        // Hide progress dialog.
        hideProgressDialog()

        for (item in mItemsList) {
            for (cartItem in cartList) {
                if (item.item_id == cartItem.item_id) {

                    cartItem.stock_quantity = item.stock_quantity

                    if (item.stock_quantity.toInt() == 0) {
                        cartItem.cart_quantity = item.stock_quantity
                    }
                }
            }
        }

        mCartListItems = cartList

        if(mCartListItems.size > 0 ) {
            rv_cart_items_list.visibility = View.VISIBLE
            ll_checkout.visibility = View.VISIBLE
            tv_no_cart_item_found.visibility = View.GONE

            rv_cart_items_list.layoutManager = LinearLayoutManager(this@CartListActivity)
            rv_cart_items_list.setHasFixedSize(true)

            val cartListAdapter = CartItemsListAdapter(this@CartListActivity, mCartListItems, true)
            rv_cart_items_list.adapter = cartListAdapter
            var subTotal: Double = 0.0

            for (item in mCartListItems) {

                val availableQuantity = item.stock_quantity.toInt()

                if (availableQuantity > 0) {
                    val price = item.price.toDouble()
                    val quantity = item.cart_quantity.toInt()

                    subTotal += (price * quantity)
                }

            }

            val formattedSubTotal = df.format(subTotal)
            tv_sub_total.text = "${formattedSubTotal}đ"
            tv_shipping_charge.text = "10.000đ" // TODO change shipping logic here

            if (subTotal > 0) {
                ll_checkout.visibility = View.VISIBLE

                val total = subTotal + 10000 // TODO change shipping logic here

                val formattedTotal = df.format(total)
                tv_total_amount.text = "${formattedTotal}đ"
            } else {
                ll_checkout.visibility = View.GONE
            }
        }
        else {
            rv_cart_items_list.visibility = View.GONE
            ll_checkout.visibility = View.GONE
            tv_no_cart_item_found.visibility = View.VISIBLE
        }
    }


    private fun getCartItemsList() {
        // showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getCartList(this@CartListActivity)
    }


    override fun onResume() {
        super.onResume()

        getItemList()
    }


    fun successItemsListFromFireStore(itemsList: ArrayList<Item>) {
        mItemsList = itemsList

        getCartItemsList()
    }


    private fun getItemList() {

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getAllItemsList(this@CartListActivity)
    }


    fun itemRemovedSuccess() {

        hideProgressDialog()

        Toast.makeText(
            this@CartListActivity,
            resources.getString(R.string.msg_item_removed_successfully),
            Toast.LENGTH_SHORT
        ).show()

        getCartItemsList()
    }


    fun itemUpdateSuccess() {

        hideProgressDialog()

        getCartItemsList()
    }
}