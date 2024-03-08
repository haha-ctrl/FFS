package com.store.ffs.ui.activitis

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.store.ffs.R
import com.store.ffs.utils.Constants

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get the stored username from the Android SharedPreferences.
        // START
        // Create an instance of Android SharedPreferences
        val tv_main = findViewById<TextView>(R.id.tv_main)
        val sharedPreferences =
            getSharedPreferences(Constants.MY_STORE_PREFERENCES, Context.MODE_PRIVATE)

        val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "")!!
        // Set the result to the tv_main.
        tv_main.text= "The logged in user is $username."
        // END
    }
}