package com.store.ffs.ui.activitis

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.os.Handler
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.store.ffs.R

import com.store.ffs.utils.MSPTextView
import com.google.android.material.snackbar.Snackbar
import com.store.ffs.utils.Constants
import com.store.ffs.utils.MSPButton
import com.store.ffs.utils.MSPEditText
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.lang.ref.WeakReference

open class BaseActivity : AppCompatActivity() {

    private lateinit var mProgressDialog: Dialog
    private var doubleBackToExitPressedOnce = false
    fun showErrorSnackBar(message: String, errorMessage: Boolean) {
        val snackBar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view

        if (errorMessage) {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity,
                    R.color.colorSnackBarError
                )
            )
        }else{
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity,
                    R.color.colorSnackBarSuccess
                )
            )
        }
        snackBar.show()
    }

    fun showProgressDialog(text: String) {

        mProgressDialog = Dialog(this)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mProgressDialog.setContentView(R.layout.dialog_progress)
        val tv_progress_text = mProgressDialog.findViewById<MSPTextView>(R.id.tv_progress_text)
        tv_progress_text.text = text


        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)

        //Start the dialog and display it on screen.
        mProgressDialog.show()
    }


    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }


    fun doubleBackToExit() {

        if (doubleBackToExitPressedOnce) {
            // super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true

        Toast.makeText(
            this,
            resources.getString(R.string.please_click_back_again_to_exit),
            Toast.LENGTH_SHORT
        ).show()

        @Suppress("DEPRECATION")
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    fun showCustomDialogBox(message: String?, callback: (Boolean) -> Unit) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_custom_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvMessage: MSPTextView = dialog.findViewById(R.id.tvMessage)
        val btnYes: MSPButton = dialog.findViewById(R.id.btnYes)
        val btnNo: MSPButton = dialog.findViewById(R.id.btnNo)
        tvMessage.text = message

        btnYes.setOnClickListener {
            dialog.dismiss()
            callback(true) // Return true if "Yes" is clicked
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
            callback(false) // Return false if "No" is clicked
        }

        dialog.show()
    }


    fun showEditTextDialog(message: String?, callback: (String) -> Unit) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_custom_dialog_edit_text)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvMessageEditText: MSPTextView = dialog.findViewById(R.id.tv_message_edit_text)
        val etMessage: MSPEditText = dialog.findViewById(R.id.et_message)
        val btnConfirm: MSPButton = dialog.findViewById(R.id.btn_confirm)
        val btnCancel: MSPButton = dialog.findViewById(R.id.btn_cancel)
        tvMessageEditText.text = message

        btnConfirm.setOnClickListener {
            val textMessage = etMessage.text.toString()
            if (textMessage.isNotEmpty()) {
                dialog.dismiss()
                callback(textMessage)
            } else {
                Toast.makeText(this, "Please enter a valid value", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    class MyTask internal constructor(
        context: Activity,
        private val message: String,
        private val title: String,
        private val token: String
    ) : AsyncTask<Void, Void, String>() {

        private val activityReference: WeakReference<Activity> = WeakReference(context)
        val JSON: MediaType = "application/json; charset=utf-8".toMediaType()

        override fun doInBackground(vararg params: Void): String {
            // do some long running task...
            val client = OkHttpClient()
            val json = JSONObject()
            val dataJson = JSONObject()

            dataJson.put("body", message)
            dataJson.put("title", title)
            json.put("notification", dataJson)
            json.put("to", token)
            val body = RequestBody.create(JSON, json.toString())
            val request = Request.Builder()
                .header("Authorization", "key=${Constants.LEGACY_SERVER_KEY}")
                .url("https://fcm.googleapis.com/fcm/send")
                .post(body)
                .build()
            val response: Response = client.newCall(request).execute()
            val finalResponse: String = response.body?.string() ?: ""
            return "task finished"
        }
    }
}