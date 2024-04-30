package com.store.ffs.utils

import android.app.Activity
import android.content.ContentResolver
import android.net.Uri
import android.webkit.MimeTypeMap
import com.theartofdev.edmodo.cropper.CropImage
import java.io.File

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
    const val TOKEN: String = "token"
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
    const val EXTRA_ITEM_DETAILS = "extra_item_details"
    const val USERNAME = "user_name"
    const val ITEM_TITLE = "title"
    const val ITEM_PRICE = "price"
    const val ITEM_DESCRIPTION = "description"
    const val ITEM_QUANTITY = "stock_quantity"
    const val ITEM_IMAGE_URL = "image"
    const val UPDATE_ITEM_REQUEST_CODE = 1001
    const val LEGACY_SERVER_KEY = "AAAAmbbuN-c:APA91bETHglsq5ZRuZw-GLbIGibUcJMjvy2w0GVIWvOwjxUFUcMm2WZ2BCzPM9rKM_utZpcmTOTTOg_KTlAxBD9Qq-szs-MQiGGuLNHzCDJmw-gVd_64uj-8L6ajR5FaAp7eOma0mske"
    const val USER_TOKEN = "userToken"

    const val EMPLOYEE: String = "employees"

    const val EXTRA_EMPLOYEE_INFO = "extra_employee_info"
    const val EXTRA_EMPLOYEE_ID: String = "extra_employee_id"

    const val MY_EMPLOYEE_LIST: String = "MyEmployeeList"
    const val EMPLOYEE_ID: String = "employee_id"
    const val EMPLOYEE_NAME = "employee_name"
    const val EMPLOYEE_PHONE_NUM = "employee_phoneNum"
    const val EMPLOYEE_EMAIL = "employee_email"
    const val EMPLOYEE_ADDRESS = "employee_address"
    const val EMPLOYEE_IMAGE_URL = "employee_image"

    const val EMPLOYEE_IMAGE: String = "Employee_Image"

    const val EXTRA_EMPLOYEE_OWNER_ID: String = "extra_employee_owner_id"
    fun showImageChooser(activity: Activity) {
        CropImage.activity().start(activity)
    }

    fun getFileExtension(uri: Uri?): String? {
        uri ?: return null

        val path = uri.path ?: return null
        val file = File(path)
        val fileName = file.name
        val dotIndex = fileName.lastIndexOf('.')

        return if (dotIndex != -1) {
            fileName.substring(dotIndex + 1)
        } else {
            null
        }
    }
}