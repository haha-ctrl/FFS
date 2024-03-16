package com.store.ffs.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Item(
    val user_id: String = "",
    val user_name: String = "",
    val title: String = "",
    val price: String = "",
    val description: String = "",
    var stock_quantity: String = "",
    val image: String = "",
    var item_id: String = "",
) : Parcelable