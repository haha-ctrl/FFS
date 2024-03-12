package com.store.ffs.firestore

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database

import com.google.firebase.ktx.Firebase
import com.store.ffs.model.User
import com.store.ffs.ui.activitis.LoginActivity
import com.store.ffs.ui.fragments.DashboardFragment
import com.store.ffs.utils.Constants

class RealtimeClass {
    private val database = Firebase.database

    fun saveUser(activity: LoginActivity, user: User) {
        val myRef = database.getReference(Constants.USERS)
        myRef.child(user.id).setValue(user)
            .addOnSuccessListener {
                activity.saveUserSuccess(user)
            }
            .addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user.",
                    e
                )
            }
    }


    fun getUser(activity: Activity) {
        val myRef = database.getReference(Constants.USERS)

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value.

                val user = dataSnapshot.child(getCurrentUserID()).getValue(User::class.java)

                when (activity) {

                    // Add more cases for other activity types if needed
                }
            }

            override fun onCancelled(error: DatabaseError) {
                when (activity) {

                    // Add more cases for other activity types if needed
                }
            }
        })
    }



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
}