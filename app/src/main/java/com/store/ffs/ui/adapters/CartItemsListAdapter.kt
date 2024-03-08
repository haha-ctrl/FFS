package com.store.ffs.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.store.ffs.R
import com.store.ffs.firestore.FirestoreClass
import com.store.ffs.model.CartItem
import com.store.ffs.ui.activitis.CartListActivity
import com.store.ffs.utils.Constants
import com.store.ffs.utils.GlideLoader
import com.store.ffs.utils.MSPTextView
import com.store.ffs.utils.MSPTextViewBold

class CartItemsListAdapter (
    private val context: Context,
    private var list: ArrayList<CartItem>,
    private val updateCartItems: Boolean
)  : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_cart_layout,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        val iv_cart_item_image = holder.itemView.findViewById<ImageView>(R.id.iv_cart_item_image)
        val tv_cart_item_title = holder.itemView.findViewById<MSPTextView>(R.id.tv_cart_item_title)
        val tv_cart_item_price = holder.itemView.findViewById<MSPTextViewBold>(R.id.tv_cart_item_price)
        val tv_cart_quantity = holder.itemView.findViewById<MSPTextView>(R.id.tv_cart_quantity)
        val ib_remove_cart_item = holder.itemView.findViewById<ImageButton>(R.id.ib_remove_cart_item)
        val ib_add_cart_item = holder.itemView.findViewById<ImageButton>(R.id.ib_add_cart_item)
        val ib_delete_cart_item = holder.itemView.findViewById<ImageButton>(R.id.ib_delete_cart_item)


        if (holder is MyViewHolder) {

            GlideLoader(context).loadItemPicture(model.image, iv_cart_item_image)

            tv_cart_item_title.text = model.title
            tv_cart_item_price.text = "${model.price}đ"
            tv_cart_quantity.text = model.cart_quantity

            if (model.cart_quantity == "0") {
                ib_remove_cart_item.visibility = View.GONE
                ib_add_cart_item.visibility = View.GONE

                if (updateCartItems) {
                    ib_delete_cart_item.visibility = View.VISIBLE
                } else {
                    ib_delete_cart_item.visibility = View.GONE
                }

                tv_cart_quantity.text =
                    context.resources.getString(R.string.lbl_out_of_stock)

                tv_cart_quantity.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorSnackBarError
                    )
                )
            } else {
                if (updateCartItems) {
                    ib_remove_cart_item.visibility = View.VISIBLE
                    ib_add_cart_item.visibility = View.VISIBLE
                    ib_delete_cart_item.visibility = View.VISIBLE
                } else {
                    ib_remove_cart_item.visibility = View.GONE
                    ib_add_cart_item.visibility = View.GONE
                    ib_delete_cart_item.visibility = View.GONE
                }

                tv_cart_quantity.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorSecondaryText
                    )
                )
            }

            // Assign the click event to the ib_remove_cart_item.
            // START
            ib_remove_cart_item.setOnClickListener {
//
//                // Call the update or remove function of firestore class based on the cart quantity.
//                // START
                if (model.cart_quantity == "1") {
                    FirestoreClass().removeItemFromCart(context, model.id)
                }
                else {
                    val cartQuantity: Int = model.cart_quantity.toInt()

                    val itemHashMap = HashMap<String, Any>()

                    itemHashMap[Constants.CART_QUANTITY] = (cartQuantity - 1).toString()

                    // Show the progress dialog.

                    if (context is CartListActivity) {
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }

                    FirestoreClass().updateMyCart(context, model.id, itemHashMap)
                }
                // END
           }
            // END
//
            // Assign the click event to the ib_add_cart_item.
            // START
            ib_add_cart_item.setOnClickListener {

                // Call the update function of firestore class based on the cart quantity.
                // START
                val cartQuantity: Int = model.cart_quantity.toInt()

                if (cartQuantity < model.stock_quantity.toInt()) {

                    val itemHashMap = HashMap<String, Any>()

                    itemHashMap[Constants.CART_QUANTITY] = (cartQuantity + 1).toString()

                    // Show the progress dialog.
                    if (context is CartListActivity) {
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }

                    FirestoreClass().updateMyCart(context, model.id, itemHashMap)
                } else {
                    if (context is CartListActivity) {
                        context.showErrorSnackBar(
                            context.resources.getString(
                                R.string.msg_for_available_stock,
                                model.stock_quantity
                            ),
                            true
                        )
                    }
                }
                // END
            }
            // END
//
//
            ib_delete_cart_item.setOnClickListener {

                when (context) {
                    is CartListActivity -> {
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }
                }

                FirestoreClass().removeItemFromCart(context, model.id)
            }
        }
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}