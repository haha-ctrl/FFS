package com.store.ffs.ui.activitis

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.store.ffs.R
import com.store.ffs.databinding.ActivityDashboardBinding
import com.store.ffs.firestore.FirestoreClass
import com.store.ffs.model.User
import com.store.ffs.utils.Constants
import com.store.ffs.utils.MyViewModel


class DashboardActivity : BaseActivity() {


    private lateinit var binding: ActivityDashboardBinding

    private val viewModel: MyViewModel by viewModels()
    private var mReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Log.i("Test log", "The log is funtioned")
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this@DashboardActivity,
                R.drawable.app_gradient_color_background
            )
        )

        val navController = findNavController(R.id.nav_host_fragment_activity_dashboard)

        val navView: BottomNavigationView = binding.navView

        if(intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            user = intent.getParcelableExtra<User>(Constants.EXTRA_USER_DETAILS)


        }


        if (!user!!.isAdmin) {
            val menu = navView.menu
            menu.findItem(R.id.navigation_items).isVisible = false
            menu.findItem(R.id.navigation_sold_items).isVisible = false
        }


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard,
                R.id.navigation_items,
                R.id.navigation_orders,
                R.id.navigation_sold_items
            )
        )
        viewModel.update(user!!.isAdmin)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

//        val dashboardFragment = newInstance(user!!.isAdmin)
//
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.nav_host_fragment_activity_dashboard, dashboardFragment)
//            .commit()
        getFCMToken()

    }

    private fun getFCMToken() {
        val userHashMap = HashMap<String, Any>()

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.i("My token", token)
                userHashMap[Constants.TOKEN] = token

                FirestoreClass().updateUserProfileData(
                    this@DashboardActivity,
                    userHashMap
                )

                Log.e("token.dashboardActivity", token)
                viewModel.updateToken(token)
            }
        }
    }

//    companion object {
//        fun newInstance(isAdmin: Boolean): DashboardFragment {
//            val myFragment = DashboardFragment()
//
//            val args = Bundle()
//            args.putBoolean("isAdmin", isAdmin)
//            myFragment.arguments = args
//
//            return myFragment
//        }
//    }

    companion object {
        private var user: User ?= null

        fun getUser(): User? {
            return user
        }

        fun setUser(newUser: User?) {
            user = newUser
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        doubleBackToExit()
    }

    public override fun onResume() {
        super.onResume()
        if (user!!.isAdmin) {
            // BROADCAST RECIEVER IS NOT NEEDED ANYMORE FOR REASONS DECLARED IN MYFIREBASE CLASS
            val intentFilter = IntentFilter(
                "com.store.ffs"
            )
            mReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    Toast.makeText(
                        this@DashboardActivity,
                        "You have an ordrer",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            this.registerReceiver(mReceiver, intentFilter, RECEIVER_EXPORTED)
        } else {
            val intentFilter = IntentFilter(
                "com.store.ffs"
            )
            mReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    Toast.makeText(
                        this@DashboardActivity,
                        "Your food is delivered",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            this.registerReceiver(mReceiver, intentFilter, RECEIVER_EXPORTED)
        }
    }
    public override fun onPause() {
        super.onPause()
        unregisterReceiver(mReceiver)
    }
}