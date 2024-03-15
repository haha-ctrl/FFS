package com.store.ffs.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    // Collection in cloud firestore
    const val USERS: String = "users"
    const val ITEMS: String = "items"

    const val MY_STORE_PREFERENCES: String = "MyStorePrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"
    const val EXTRA_USER_DETAILS: String = "extra_user_details"
    const val READ_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE = 1
    const val USER_PROFILE_IMAGE:String = "User_Profile_Image"
    const val COMPLETE_PROFILE: String = "profileCompleted"

    const val MALE: String = "male"
    const val FEMALE: String = "female"
    const val IMAGE: String = "image"
    const val FIRST_NAME: String = "firstName"
    const val LAST_NAME: String = "lastName"
    // Firebase database field names
    const val MOBILE: String = "mobile"
    const val GENDER: String = "gender"

    const val item_IMAGE: String = "Item_Image"

    const val USER_ID: String = "user_id"

    const val EXTRA_ITEM_ID: String = "extra_item_id"

    const val EXTRA_ITEM_OWNER_ID: String = "extra_item_owner_id"

    const val DEFAULT_CART_QUANTITY: String = "1"

    const val CART_ITEMS: String = "cart_items"

    const val ITEM_ID: String = "item_id"

    const val CART_QUANTITY: String = "cart_quantity"

    const val HOME: String = "Home"
    const val OFFICE: String = "Office"
    const val OTHER: String = "Other"

    const val ADDRESSES: String = "addresses"

    const val EXTRA_ADDRESS_DETAILS: String = "AddressDetails"

    const val EXTRA_SELECT_ADDRESS: String = "extra_select_address"
    const val ADD_ADDRESS_REQUEST_CODE: Int = 121

    const val EXTRA_SELECTED_ADDRESS: String = "extra_selected_address"

    const val ORDERS: String = "orders"

    const val STOCK_QUANTITY: String = "stock_quantity"

    const val EXTRA_MY_ORDER_DETAILS: String = "extra_MY_ORDER_DETAILS"

    const val SOLD_ITEMS: String = "sold_items"

    const val EXTRA_SOLD_ITEM_DETAILS: String = "extra_sold_item_details"

    const val EMAIL_ADMIN = "titanthophap2@gmail.com"
    const val UID_ADMIN = "BK3LFlDCxHMf7LfOUrQ2NhaBKH83"
    const val EXTRA_IS_ADMIN = "is_admin"
    const val ADMIN_STATUS = "adminStatus"
    const val ORDER_STATUS = "status"
    const val ORDER_PENDING = "Pending"
    const val ORDER_IN_PROCESS = "In Process"
    const val ORDER_DELIVERED = "Delivered"
    const val ORDER_CONFIRMED = "Confirmed"
    const val ORDER_REJECT = "Rejected"
    const val ORDER_CANCEL = "Canceled"
    const val ORDER_RETURN = "Returned"
    fun showImageChooser(activity: Activity) {
        // An intent for launching the image selection of phone storage.
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // Launches the image selection of phone storage using the constant code.
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        /*
         * MimeTypeMap: Two-way map that maps MIME-types to file extensions and vice versa.
         *
         * getSingleton(): Get the singleton instance of MimeTypeMap.
         *
         * getExtensionFromMimeType: Return the registered extension for the given MIME type.
         *
         * contentResolver.getType: Return the MIME type of the given content URL.
         */
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}