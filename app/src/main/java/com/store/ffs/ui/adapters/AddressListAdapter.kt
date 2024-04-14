package com.store.ffs.ui.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.store.ffs.R
import com.store.ffs.model.Address
import com.store.ffs.ui.activitis.AddEditAddressActivity
import com.store.ffs.ui.activitis.CheckoutActivity
import com.store.ffs.utils.Constants
import com.store.ffs.utils.MSPTextView
import com.store.ffs.utils.MSPTextViewBold


// Create an adapter class for AddressList adapter.
// START
/**
 * An adapter class for AddressList adapter.
 */
open class AddressListAdapter(
    private val context: Context,
    private var list: ArrayList<Address>,
    private val selectAddress: Boolean,
    private val token: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_address_layout,
                parent,
                false
            )
        )
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val tv_address_full_name = holder.itemView.findViewById<MSPTextViewBold>(R.id.tv_address_full_name)
        val tv_address_type = holder.itemView.findViewById<MSPTextView>(R.id.tv_address_type)
        val tv_address_details = holder.itemView.findViewById<MSPTextView>(R.id.tv_address_details)
        val tv_address_mobile_number = holder.itemView.findViewById<MSPTextView>(R.id.tv_address_mobile_number)
        val model = list[position]

        if (holder is MyViewHolder) {
            tv_address_full_name.text = model.name
            tv_address_type.text = model.type
            tv_address_details.text = "${model.address}, ${model.zipCode}"
            tv_address_mobile_number.text = model.mobileNumber

            Log.e("token.AddressListAdapter", token)
            if (selectAddress) {
               holder.itemView.setOnClickListener {
                   val intent = Intent(context, CheckoutActivity::class.java)
                   intent.putExtra(Constants.EXTRA_SELECTED_ADDRESS, model)
                   intent.putExtra(Constants.USER_TOKEN, token)
                   context.startActivity(intent)
               }
            }
        }
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }


    fun notifyEditItem(activity: Activity, position: Int) {
        val intent = Intent(context, AddEditAddressActivity::class.java)
        // Pass the address details through intent to edit the address.
        // START
        intent.putExtra(Constants.EXTRA_ADDRESS_DETAILS, list[position])
        // END
        activity.startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)

        notifyItemChanged(position) // Notify any registered observers that the item at position has changed.
    }




    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
// END