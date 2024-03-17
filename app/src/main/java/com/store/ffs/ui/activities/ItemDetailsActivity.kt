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
import com.store.ffs.model.User
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


class ItemDetailsActivity : BaseActivity(), View.OnClickListener {
    private var mItemId: String = ""
    private lateinit var mItemDetails: Item
    private var mItemOwnerId: String = ""
    private var user: User?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_details)
        setupActionBar()

        user = DashboardActivity.getUser()

        if (intent.hasExtra(Constants.EXTRA_ITEM_ID)) {
            mItemId =
                intent.getStringExtra(Constants.EXTRA_ITEM_ID)!!
            Log.i("Item Id", mItemId)
        }

        // var itemOwnerId: String = ""

        if (intent.hasExtra(Constants.EXTRA_ITEM_OWNER_ID)) {
            mItemOwnerId =
                intent.getStringExtra(Constants.EXTRA_ITEM_OWNER_ID)!!
        }

        val btn_add_to_cart = findViewById<MSPButton>(R.id.btn_add_to_cart)
        val btn_go_to_cart = findViewById<MSPButton>(R.id.btn_go_to_cart)
        if (FirestoreClass().getCurrentUserID() == mItemOwnerId) {
            btn_add_to_cart.visibility = View.GONE
            btn_go_to_cart.visibility = View.GONE
        } else {
            btn_add_to_cart.visibility = View.VISIBLE
        }

        val btn_add_stock_quantity = findViewById<MSPButton>(R.id.btn_add_stock_quantity)
        val btn_reduce_stock_quantity = findViewById<MSPButton>(R.id.btn_reduce_stock_quantity)
        val ll_item_details_change_quantity = findViewById<LinearLayout>(R.id.ll_item_details_change_quantity)
        val iv_add_update_item_detail = findViewById<ImageView>(R.id.iv_update_item_detail)

        if (user?.isAdmin == true) {
            ll_item_details_change_quantity.visibility = View.VISIBLE
        } else {
            ll_item_details_change_quantity.visibility = View.GONE
        }

        getItemDetails()

        btn_add_to_cart.setOnClickListener(this)
        btn_go_to_cart.setOnClickListener(this)
        btn_add_stock_quantity.setOnClickListener(this)
        btn_reduce_stock_quantity.setOnClickListener(this)
        iv_add_update_item_detail.setOnClickListener(this)
    }


    private fun setupActionBar() {
        val toolbar_item_details_activity = findViewById<Toolbar>(R.id.toolbar_item_details_activity)

        setSupportActionBar(toolbar_item_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_item_details_activity.setNavigationOnClickListener { onBackPressed() }
    }


    fun itemDetailsSuccess(item: Item) {
        val iv_item_detail_image = findViewById<ImageView>(R.id.iv_item_detail_image)
        val tv_item_details_title = findViewById<MSPTextViewBold>(R.id.tv_item_details_title)
        val tv_item_details_price = findViewById<MSPTextView>(R.id.tv_item_details_price)
        val tv_item_details_description = findViewById<MSPTextView>(R.id.tv_item_details_description)
        val tv_item_details_stock_quantity = findViewById<MSPTextView>(R.id.tv_item_details_stock_quantity)
        val btn_add_to_cart = findViewById<MSPButton>(R.id.btn_add_to_cart)

        val nf: NumberFormat = NumberFormat.getNumberInstance(Locale.GERMAN)
        val df = nf as DecimalFormat

        mItemDetails = item

        // Populate the item details in the UI.
        GlideLoader(this@ItemDetailsActivity).loadItemPicture(
            item.image,
            iv_item_detail_image
        )

        tv_item_details_title.text = item.title
        val itemPriceNumeric = item.price.toDoubleOrNull() ?: 0.0
        val formatteditemPrice = df.format(itemPriceNumeric)
        tv_item_details_price.text = "${formatteditemPrice}đ"
        tv_item_details_description.text = item.description
        tv_item_details_stock_quantity.text = item.stock_quantity




        if(item.stock_quantity.toInt() == 0){

            // Hide Progress dialog.
            hideProgressDialog()

            // Hide the AddToCart button if the item is already in the cart.
            btn_add_to_cart.visibility = View.GONE

            tv_item_details_stock_quantity.text =
                resources.getString(R.string.lbl_out_of_stock)

            tv_item_details_stock_quantity.setTextColor(
                ContextCompat.getColor(
                    this@ItemDetailsActivity,
                    R.color.colorSnackBarError
                )
            )
        }else{
            tv_item_details_stock_quantity.setTextColor(
                ContextCompat.getColor(
                    this@ItemDetailsActivity,
                    R.color.black
                )
            )

            // There is no need to check the cart list if the item owner himself is seeing the item details.
            if (FirestoreClass().getCurrentUserID() == item.user_id) {
                // Hide Progress dialog.
                hideProgressDialog()
            } else {
                FirestoreClass().checkIfItemExistInCart(this@ItemDetailsActivity, mItemId)
            }
        }

    }


    fun getItemDetails() {

        // Show the item dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of FirestoreClass to get the item details.
        FirestoreClass().getItemDetails(this@ItemDetailsActivity, mItemId)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.btn_add_to_cart -> {
                    addToCart()
                }

                R.id.btn_go_to_cart->{
                    startActivity(Intent(this@ItemDetailsActivity, CartListActivity::class.java))
                }

                R.id.btn_add_stock_quantity -> {
                    showEditTextDialog("Enter quantity you want to add") { quantityString ->
                        val quantityToAdd = quantityString.toIntOrNull()
                        if (quantityToAdd != null && quantityToAdd > 0) {
                            val updatedQuantity = (mItemDetails.stock_quantity.toInt() + quantityToAdd).coerceAtLeast(0).toString()
                            FirestoreClass().updateItemQuantity(this, mItemId, updatedQuantity)
                        } else {
                            Toast.makeText(this, "Please enter a valid positive number", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                R.id.btn_reduce_stock_quantity -> {
                    showEditTextDialog("Enter quantity you want to reduce") { quantityString ->
                        val quantityToReduce = quantityString.toIntOrNull()
                        if (quantityToReduce != null && quantityToReduce > 0) {
                            val currentQuantity = mItemDetails.stock_quantity.toInt()
                            if (quantityToReduce <= currentQuantity) {
                                val updatedQuantity = (currentQuantity - quantityToReduce).coerceAtLeast(0).toString()
                                FirestoreClass().updateItemQuantity(this, mItemId, updatedQuantity)
                            } else {
                                Toast.makeText(this, "Quantity to reduce cannot exceed current quantity", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this, "Please enter a valid positive number", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                R.id.iv_update_item_detail -> {
                    val intent = Intent(this@ItemDetailsActivity, AddItemActivity::class.java)
                    intent.putExtra(Constants.EXTRA_ITEM_DETAILS, mItemDetails)
                    intent.putExtra(Constants.EXTRA_ITEM_ID, mItemId)
                    startActivityForResult(intent, Constants.UPDATE_ITEM_REQUEST_CODE)
                }
            }
        }
    }


    private fun addToCart() {

        val cartItem = CartItem(
            FirestoreClass().getCurrentUserID(),
            mItemOwnerId,
            mItemId,
            mItemDetails.title,
            mItemDetails.price,
            mItemDetails.image,
            Constants.DEFAULT_CART_QUANTITY
        )

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addCartItems(this@ItemDetailsActivity, cartItem)
    }


    fun addToCartSuccess() {
        val btn_add_to_cart = findViewById<MSPButton>(R.id.btn_add_to_cart)
        val btn_go_to_cart = findViewById<MSPButton>(R.id.btn_go_to_cart)

        // Hide the progress dialog.
        hideProgressDialog()

        Toast.makeText(
            this@ItemDetailsActivity,
            resources.getString(R.string.success_message_item_added_to_cart),
            Toast.LENGTH_SHORT
        ).show()

        // Hide the AddToCart button if the item is already in the cart.
        btn_add_to_cart.visibility = View.GONE
        btn_go_to_cart.visibility = View.VISIBLE
    }

    fun itemExistsInCart() {
        val btn_add_to_cart = findViewById<MSPButton>(R.id.btn_add_to_cart)
        val btn_go_to_cart = findViewById<MSPButton>(R.id.btn_go_to_cart)
        // Hide the progress dialog.
        hideProgressDialog()

        // Hide the AddToCart button if the item is already in the cart.
        btn_add_to_cart.visibility = View.GONE
        // Show the GoToCart button if the item is already in the cart. User can update the quantity from the cart list screen if he wants.
        btn_go_to_cart.visibility = View.VISIBLE
    }

    fun itemQuantityUpdateSuccess() {
        getItemDetails()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.UPDATE_ITEM_REQUEST_CODE && resultCode == RESULT_OK) {
            // Reload item details
            getItemDetails()
        }
    }
}