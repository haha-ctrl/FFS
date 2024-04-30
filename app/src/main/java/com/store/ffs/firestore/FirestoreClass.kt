package com.store.ffs.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.store.ffs.model.*
import com.store.ffs.ui.activitis.*
import com.store.ffs.ui.fragments.DashboardFragment
import com.store.ffs.ui.fragments.ItemsFragment
import com.store.ffs.ui.fragments.OrdersFragment
import com.store.ffs.ui.fragments.SoldItemsFragment
import com.store.ffs.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.store.ffs.ui.activities.AddEditEmployeeActivity
import com.store.ffs.ui.fragments.EmployeeManagerFragment

class FirestoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User) {

        // The "users" is collection name. If the collection is already created then it will not create the same one again.
        mFireStore.collection(Constants.USERS)
            // Document ID for users fields. Here the document it is the User ID.
            .document(userInfo.id)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge later on instead of replacing the fields.
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user.",
                    e
                )
            }
    }

    /**
     * A function to get the user id of current logged user.
     */
    fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    fun getUserDetails(activity: Activity) {

        // Here we pass the collection name from which we wants the data.
        mFireStore.collection(Constants.USERS)
            // The document id to get the Fields of user.
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                Log.i(activity.javaClass.simpleName, document.toString())

                // Here we have received the document snapshot which is converted into the User Data model object.
                val user = document.toObject(User::class.java)!!

                // Create an instance of the Android SharedPreferences.
                // START
                val sharedPreferences =
                    activity.getSharedPreferences(
                        Constants.MY_STORE_PREFERENCES,
                        Context.MODE_PRIVATE
                    )

                // Create an instance of the editor which is help us to edit the SharedPreference.
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                // key:value logged_in_username:Khanh Nguyen
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.firstName} ${user.lastName}"
                )
                editor.apply()
                // END

                val isAdmin = user.email == Constants.EMAIL_ADMIN
                // Pass the result to the Login Activity.
                // START
                when (activity) {
                    is LoginActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        activity.userLoggedInSuccess(user, isAdmin)
                    }

                    is SettingsActivity -> {
                        // Call the function of base class.
                        // Call a function of base activity for transferring the result to it.
                        activity.userDetailsSuccess(user)
                        // END
                    }

                }
                // END
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details.",
                    e
                )
            }
    }


    fun getAdminDetails(activity: Activity) {
        mFireStore.collection(Constants.USERS)
            .document("BK3LFlDCxHMf7LfOUrQ2NhaBKH83") // Use the provided userId instead of getCurrentUserID()
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())
                val user = document.toObject(User::class.java)!!
                // Determine if the user is an admin
                val isAdmin = user.email == Constants.EMAIL_ADMIN
                // Pass the result to the appropriate activity
                when (activity) {
                    is LoginActivity -> {
                        activity.getAdminDetailsSuccess(user, isAdmin)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, "Error while getting user details.", e)
            }
    }


    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        // Collection Name
        mFireStore.collection(Constants.USERS)
            // Document ID against which the data to be updated. Here the document id is the current logged in user id.
            .document(getCurrentUserID())
            // A HashMap of fields which are to be updated.
            .update(userHashMap)
            .addOnSuccessListener {

                // Notify the success result to the base activity.
                // START
                // Notify the success result.
                when (activity) {
                    is UserProfileActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        activity.userProfileUpdateSuccess()
                    }
                }
                // END
            }
            .addOnFailureListener { e ->

                when (activity) {
                    is UserProfileActivity -> {
                        // Hide the progress dialog if there is any error. And print the error in log.
                        activity.hideProgressDialog()
                    }
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the user details.",
                    e
                )
            }
    }


    fun updateItemDetails(activity: Activity, itemId: String, itemInfo: HashMap<String, Any>) {
        // Collection Name
        mFireStore.collection(Constants.ITEMS)
            // Document ID against which the data to be updated. Here the document id is the item id.
            .document(itemId)
            // A HashMap of fields which are to be updated.
            .update(itemInfo)
            .addOnSuccessListener {

                // Notify the success result to the base activity.
                // START
                // Notify the success result.
                when (activity) {
                    is AddItemActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        activity.itemUpdateSuccess()
                    }
                }
                // END
            }
            .addOnFailureListener { e ->

                when (activity) {
                    is AddItemActivity -> {
                        // Hide the progress dialog if there is any error. And print the error in log.
                        activity.hideProgressDialog()
                    }
                    // Handle other activity types if needed
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the item details.",
                    e
                )
            }
    }



    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String) {

        //getting the storage reference
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(
                imageFileURI
            )
        )

        //adding the file to reference
        sRef.putFile(imageFileURI!!)
            .addOnSuccessListener { taskSnapshot ->
                // The image upload is success
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                // Get the downloadable url from the task snapshot
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())

                        // Pass the success result to base class.
                        // START
                        // Here call a function of base activity for transferring the result to it.
                        when (activity) {
                            is UserProfileActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }

                            is AddItemActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                        }
                        // END
                    }
            }
            .addOnFailureListener { exception ->

                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                    is AddItemActivity -> {
                        activity.hideProgressDialog()
                    }
                    is SettingsActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }


    fun uploadItemDetails(activity: AddItemActivity, itemInfo: Item) {

        mFireStore.collection(Constants.ITEMS)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .add(itemInfo)
            .addOnSuccessListener {documentReference->

                // Here call a function of base activity for transferring the result to it.
                activity.itemUploadSuccess(documentReference.id)
            }
            .addOnFailureListener { e ->

                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading the item details.",
                    e
                )
            }
    }


    fun updateItemQuantity(activity: Activity, itemId: String, updatedQuantity: String) {
        val itemReference = mFireStore.collection(Constants.ITEMS).document(itemId)

        itemReference
            .update("stock_quantity", updatedQuantity)
            .addOnSuccessListener {
                when (activity) {
                    is ItemDetailsActivity -> {
                        activity.itemQuantityUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is ItemDetailsActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error while updating the item details.", e)
            }
    }



    fun getitemsList(fragment: Fragment) {
        // The collection name for itemS
        mFireStore.collection(Constants.ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of boards in the form of documents.
                Log.e("items List", document.documents.toString())

                // Here we have created a new instance for items ArrayList.
                val itemsList: ArrayList<Item> = ArrayList()

                // A for loop as per the list of documents to convert them into items ArrayList.
                for (i in document.documents) {

                    val item = i.toObject(Item::class.java)
                    item!!.item_id = i.id

                    itemsList.add(item)
                }

                when (fragment) {
                    is ItemsFragment -> {
                        fragment.successItemsListFromFireStore(itemsList)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error based on the base class instance.
                when (fragment) {
                    is ItemsFragment -> {
                        fragment.hideProgressDialog()
                    }
                }
                Log.e("Get Item List", "Error while getting item list.", e)
            }
    }

    fun getDashboardItemsList(fragment: DashboardFragment) {
        // The collection name for ITEMS
        mFireStore.collection(Constants.ITEMS)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of boards in the form of documents.
                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                // Here we have created a new instance for Items ArrayList.
                val itemsList: ArrayList<Item> = ArrayList()

                // A for loop as per the list of documents to convert them into Items ArrayList.
                for (i in document.documents) {

                    val item = i.toObject(Item::class.java)!!
                    item.item_id = i.id
                    itemsList.add(item)
                }

                // Pass the success result to the base fragment.
                fragment.successDashboardItemsList(itemsList)
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error which getting the dashboard items list.
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while getting dashboard items list.", e)
            }

    }


    fun deleteItem(fragment: ItemsFragment, itemId: String) {

        mFireStore.collection(Constants.ITEMS)
            .document(itemId)
            .delete()
            .addOnSuccessListener {

                // Notify the success result to the base class.
                // START
                // Notify the success result to the base class.
                fragment.itemDeleteSuccess()
                // END
            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is an error.
                fragment.hideProgressDialog()

                Log.e(
                    fragment.requireActivity().javaClass.simpleName,
                    "Error while deleting the item.",
                    e
                )
            }
    }


    fun getItemDetails(activity: ItemDetailsActivity, itemId: String) {

        // The collection name for ITEMS
        mFireStore.collection(Constants.ITEMS)
            .document(itemId)
            .get() // Will get the document snapshots.
            .addOnSuccessListener { document ->

                // Here we get the item details in the form of document.
                Log.e(activity.javaClass.simpleName, document.toString())

                // Convert the snapshot to the object of Item data model class.
                val item = document.toObject(Item::class.java)!!

                // Notify the success result.
                // START
                activity.itemDetailsSuccess(item)
                // END
            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is an error.
                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName, "Error while getting the item details.", e)
            }
    }


    fun addCartItems(activity: ItemDetailsActivity, addToCart: CartItem) {

        mFireStore.collection(Constants.CART_ITEMS)
            .document()
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(addToCart, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.addToCartSuccess()
            }
            .addOnFailureListener { e ->

                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating the document for cart item.",
                    e
                )
            }
    }


    fun checkIfItemExistInCart(activity: ItemDetailsActivity, ItemId: String) {

        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .whereEqualTo(Constants.ITEM_ID, ItemId)
            .get()
            .addOnSuccessListener { document ->

                Log.e(activity.javaClass.simpleName, document.documents.toString())

                // If the document size is greater than 1 it means the Item is already added to the cart.
                if (document.documents.size > 0) {
                    activity.itemExistsInCart()
                } else {
                    activity.hideProgressDialog()
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is an error.
                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while checking the existing cart list.",
                    e
                )
            }
    }


    fun getCartList(activity: Activity) {
        // The collection name for ITEMS
        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of cart items in the form of documents.
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                // Here we have created a new instance for Cart Items ArrayList.
                val list: ArrayList<CartItem> = ArrayList()

                // A for loop as per the list of documents to convert them into Cart Items ArrayList.
                for (i in document.documents) {
                    val cartItem = i.toObject(CartItem::class.java)!!
                    cartItem.id = i.id

                    list.add(cartItem)
                }

                when (activity) {
                    is CartListActivity -> {
                        activity.successCartItemsList(list)
                    }

                    is CheckoutActivity -> {
                        activity.successCartItemsList(list)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is an error based on the activity instance.
                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }

                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName, "Error while getting the cart list items.", e)
            }
    }


    fun getAllItemsList(activity: Activity) {
        // The collection name for ITEMS
        mFireStore.collection(Constants.ITEMS)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of boards in the form of documents.
                Log.e("Items List", document.documents.toString())

                // Here we have created a new instance for Items ArrayList.
                val itemsList: ArrayList<Item> = ArrayList()

                // A for loop as per the list of documents to convert them into Items ArrayList.
                for (i in document.documents) {

                    val item = i.toObject(Item::class.java)
                    item!!.item_id = i.id

                    itemsList.add(item)
                }

                when (activity) {
                    is CartListActivity -> {
                        activity.successItemsListFromFireStore(itemsList)
                    }

                    // Notify the success result to the base class.
                    // START
                    is CheckoutActivity -> {
                        activity.successItemsListFromFireStore(itemsList)
                    }
                    // END
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error based on the base class instance.
                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }

                    // Hide the progress dialog.
                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }
                }


                Log.e("Get Item List", "Error while getting all Item list.", e)
            }
    }





    fun removeItemFromCart(context: Context, cart_id: String) {

        // Cart items collection name
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cart_id) // cart id
            .delete()
            .addOnSuccessListener {

                // Notify the success result of the removed cart item from the list to the base class.
                when (context) {
                    is CartListActivity -> {
                        context.itemRemovedSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is any error.
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e(
                    context.javaClass.simpleName,
                    "Error while removing the item from the cart list.",
                    e
                )
            }
    }


    fun updateMyCart(context: Context, cart_id: String, itemHashMap: HashMap<String, Any>) {

        // Cart items collection name
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cart_id) // cart id
            .update(itemHashMap) // A HashMap of fields which are to be updated.
            .addOnSuccessListener {

                // Notify the success result of the updated cart items list to the base class.
                // START
                // Notify the success result of the updated cart items list to the base class.
                when (context) {
                    is CartListActivity -> {
                        context.itemUpdateSuccess()
                    }
                }
                // END
            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is any error.
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }

                Log.e(
                    context.javaClass.simpleName,
                    "Error while updating the cart item.",
                    e
                )
            }
    }


    fun addAddress(activity: AddEditAddressActivity, addressInfo: Address) {

        // Collection name address.
        mFireStore.collection(Constants.ADDRESSES)
            .document()
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Notify the success result to the base class.
                // START
                // Here call a function of base activity for transferring the result to it.
                activity.addUpdateAddressSuccess()
                // END
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while adding the address.",
                    e
                )
            }
    }


    fun getAddressesList(activity: AddressListActivity) {
        // The collection name for ITEMS
        mFireStore.collection(Constants.ADDRESSES)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                // Here we get the list of boards in the form of documents.
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                // Here we have created a new instance for address ArrayList.
                val addressList: ArrayList<Address> = ArrayList()

                // A for loop as per the list of documents to convert them into Boards ArrayList.
                for (i in document.documents) {

                    val address = i.toObject(Address::class.java)!!
                    address.id = i.id

                    addressList.add(address)
                }

                // Notify the success result to the base class.
                // START
                activity.successAddressListFromFirestore(addressList)
                // END
            }
            .addOnFailureListener { e ->
                // Here call a function of base activity for transferring the result to it.

                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName, "Error while getting the address list.", e)
            }

    }


    fun updateAddress(activity: AddEditAddressActivity, addressInfo: Address, addressId: String) {

        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the Address.",
                    e
                )
            }
    }


    fun deleteAddress(activity: AddressListActivity, addressId: String) {

        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            .delete()
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.deleteAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while deleting the address.",
                    e
                )
            }
    }


    fun placeOrder(activity: Activity, order: Order) {

        mFireStore.collection(Constants.ORDERS)
            .document()
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(order, SetOptions.merge())
            .addOnSuccessListener {
                when (activity) {
                    is CheckoutActivity -> {
                        // Notify the success result.
                        // START
                        // Here call a function of base activity for transferring the result to it.
                        activity.orderPlacedSuccess()
                        // END
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is CheckoutActivity -> {
                        // Hide the progress dialog if there is any error.
                        activity.hideProgressDialog()
                        Log.e(
                            activity.javaClass.simpleName,
                            "Error while placing an order.",
                            e
                        )
                    }
                }
            }
    }


    fun updateAllDetails(activity: CheckoutActivity, cartList: ArrayList<CartItem>, order: Order) {

        val writeBatch = mFireStore.batch()

        // Here we will update the item stock in the items collection based to cart quantity.
        for (cart in cartList) {

            val itemHashMap = HashMap<String, Any>()

            itemHashMap[Constants.STOCK_QUANTITY] =
                (cart.stock_quantity.toInt() - cart.cart_quantity.toInt()).toString()

            val documentReference = mFireStore.collection(Constants.ITEMS)
                .document(cart.item_id)

            writeBatch.update(documentReference, itemHashMap)
        }

        // Delete the list of cart items
        for (cartItem in cartList) {

            val documentReference = mFireStore.collection(Constants.CART_ITEMS)
                .document(cartItem.id)
            writeBatch.delete(documentReference)
        }

        writeBatch.commit().addOnSuccessListener {

            activity.allDetailsUpdatedSuccessfully()

        }.addOnFailureListener { e ->
            // Here call a function of base activity for transferring the result to it.
            activity.hideProgressDialog()

            Log.e(
                activity.javaClass.simpleName,
                "Error while updating all the details after order placed.",
                e
            )
        }
    }


    fun setSoldItems(order: Order) {

        val writeBatch = mFireStore.batch()
        val soldItem = SoldItem(
            Constants.UID_ADMIN,
            order.items,
            order.address,
            order.title,
            order.image,
            order.sub_total_amount,
            order.shipping_charge,
            order.total_amount,
            order.order_datetime
        )

        val documentReference = mFireStore.collection(Constants.SOLD_ITEMS).document()
        writeBatch.set(documentReference, soldItem)

        writeBatch.commit()
            .addOnSuccessListener {
                // Handle success
            }
            .addOnFailureListener { e ->
                // Handle failure
                Log.e("setSoldItems", "Error setting sold items", e)
            }
    }



    fun getMyOrdersList(fragment: OrdersFragment, isAdmin: Boolean = false) {
        val collectionReference = if (isAdmin) {
            mFireStore.collection(Constants.ORDERS)
        } else {
            mFireStore.collection(Constants.ORDERS)
                .whereEqualTo(Constants.USER_ID, getCurrentUserID())
        }

        collectionReference
            .orderBy("order_datetime", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())
                val list: ArrayList<Order> = ArrayList()

                for (i in document.documents) {
                    val orderItem = i.toObject(Order::class.java)!!
                    orderItem.id = i.id

                    list.add(orderItem)
                }

                // Notify the success result to base class.
                fragment.populateOrdersListInUI(list)
            }
            .addOnFailureListener { e ->
                // Here call a function of the base activity for transferring the result to it.
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while getting the orders list.", e)
            }
    }



    fun getSoldItemsList(fragment: SoldItemsFragment) {
        // The collection name for SOLD ITEMS
        mFireStore.collection(Constants.SOLD_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .orderBy("order_datetime", Query.Direction.DESCENDING)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                // Here we get the list of sold items in the form of documents.
                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                // Here we have created a new instance for Sold Items ArrayList.
                val list: ArrayList<SoldItem> = ArrayList()

                // A for loop as per the list of documents to convert them into Sold Items ArrayList.
                for (i in document.documents) {

                    val soldItem = i.toObject(SoldItem::class.java)!!
                    soldItem.id = i.id

                    list.add(soldItem)
                }

                // Notify the success result to the base class.
                // START
                fragment.successSoldItemsList(list)
                // END
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error.
                fragment.hideProgressDialog()

                Log.e(
                    fragment.javaClass.simpleName,
                    "Error while getting the list of sold items.",
                    e
                )
            }
    }

    fun updateOrderStatus(activity: Activity, orderDetails: Order, updateFields: Map<String, Any>) {
        mFireStore.collection(Constants.ORDERS)
            // Document ID against which the order status to be updated. Use the order ID here.
            .document(orderDetails.id)
            // A Map of fields which are to be updated.
            .update(updateFields)
            .addOnSuccessListener {
                // Notify the success result to the calling activity.
                when (activity) {
                    is MyOrderDetailsActivity -> {
                        // Call a function in MyOrderDetailsActivity for handling success.
                        activity.orderStatusUpdateSuccess(orderDetails)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Handle failure, hide progress dialog, and log the error.
                when (activity) {
                    is MyOrderDetailsActivity -> {
                        activity.hideProgressDialog()
                        Log.e(activity.javaClass.simpleName, "Error while updating order status.", e)
                    }
                }
            }
    }


    fun uploadEmployeeInfo(activity: AddEditEmployeeActivity, employeeInfo: Employee) {

        mFireStore.collection(Constants.EMPLOYEE)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .add(employeeInfo)
            .addOnSuccessListener {documentReference->

                // Here call a function of base activity for transferring the result to it.
                activity.employeeUploadSuccess(documentReference.id)
            }
            .addOnFailureListener { e ->

                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading the employee information.",
                    e
                )
            }
    }

    fun updateEmployeeInfo(activity: Activity, employeeId: String, employeeInfo: HashMap<String, Any>) {
        // Collection Name
        mFireStore.collection(Constants.EMPLOYEE)
            // Document ID against which the data to be updated. Here the document id is the item id.
            .document(employeeId)
            // A HashMap of fields which are to be updated.
            .update(employeeInfo)
            .addOnSuccessListener {

                // Notify the success result to the base activity.
                // START
                // Notify the success result.
                when (activity) {
                    is AddEditEmployeeActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        activity.employeeUpdateSuccess()
                    }
                }
                // END
            }
            .addOnFailureListener { e ->

                when (activity) {
                    is AddEditEmployeeActivity -> {
                        // Hide the progress dialog if there is any error. And print the error in log.
                        activity.hideProgressDialog()
                    }
                    // Handle other activity types if needed
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the employee information",
                    e
                )
            }
    }

    fun getemployeeList(fragment: Fragment) {
        // The collection name for itemS
        mFireStore.collection(Constants.EMPLOYEE)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                Log.e("current id employer", getCurrentUserID())
                // Here we get the list of boards in the form of documents.
                Log.e("employee List", document.documents.toString())

                // Here we have created a new instance for items ArrayList.
                val employeesList: ArrayList<Employee> = ArrayList()

                // A for loop as per the list of documents to convert them into items ArrayList.
                for (i in document.documents) {

                    val employee = i.toObject(Employee::class.java)
                    employee!!.employee_id = i.id

                    employeesList.add(employee)
                }

                when (fragment) {
                    is EmployeeManagerFragment -> {
                        fragment.successEmployeeListFromFireStore(employeesList)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error based on the base class instance.
                when (fragment) {
                    is EmployeeManagerFragment -> {
                        fragment.hideProgressDialog()
                    }
                }
                Log.e("Get Employee List", "Error while getting employee list.", e)
            }
    }


    fun deleteEmployee(fragment: EmployeeManagerFragment, employeeId: String) {

        mFireStore.collection(Constants.EMPLOYEE)
            .document(employeeId)
            .delete()
            .addOnSuccessListener {

                // Notify the success result to the base class.
                // START
                // Notify the success result to the base class.
                fragment.employeeDeleteSuccess()
                // END
            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is an error.
                fragment.hideProgressDialog()

                Log.e(
                    fragment.requireActivity().javaClass.simpleName,
                    "Error while deleting this employee.",
                    e
                )
            }
    }
}