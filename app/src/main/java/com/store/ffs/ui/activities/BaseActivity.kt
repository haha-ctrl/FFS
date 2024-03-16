package com.store.ffs.ui.activitis

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.store.ffs.utils.MSPButton
import com.store.ffs.utils.MSPEditText

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
            super.onBackPressed()
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
}