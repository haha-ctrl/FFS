package com.store.ffs.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// Create a data model class for Order Items with required fields.
// START
/**
 * A data model class for Order item with required fields.
 */
@Parcelize
data class Order(
    val user_id: String = "",
    val items: ArrayList<CartItem> = ArrayList(),
    val address: Address = Address(),
    val title: String = "",
    val image: String = "",
    val sub_total_amount: String = "",
    val shipping_charge: String = "",
    val total_amount: String = "",
    val order_datetime: Long = 0L,
    val user_token: String = "",
    var id: String = "",
    var status: String = "Pending"
) : Parcelable
// END