package com.store.ffs.ui.activitis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.store.ffs.R
import com.store.ffs.databinding.ActivitySoldItemDetailsBinding
import com.store.ffs.model.SoldItem
import com.store.ffs.ui.adapters.CartItemsListAdapter
import com.store.ffs.utils.Constants
import com.store.ffs.utils.GlideLoader
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class SoldItemDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivitySoldItemDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySoldItemDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupActionBar()

        var itemDetails: SoldItem = SoldItem()

        if (intent.hasExtra(Constants.EXTRA_SOLD_ITEM_DETAILS)) {
            itemDetails =
                intent.getParcelableExtra<SoldItem>(Constants.EXTRA_SOLD_ITEM_DETAILS)!!
        }

        setupUI(itemDetails)
    }


    private fun setupActionBar() {
        val toolbar_sold_item_details_activity = findViewById<Toolbar>(R.id.toolbar_sold_item_details_activity)
        setSupportActionBar(toolbar_sold_item_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_sold_item_details_activity.setNavigationOnClickListener { onBackPressed() }
    }


    private fun setupUI(itemDetails: SoldItem) {
        val nf: NumberFormat = NumberFormat.getNumberInstance(Locale.GERMAN)
        val df = nf as DecimalFormat

        binding.tvSoldItemDetailsId.text = itemDetails.title

        // Date Format in which the date will be displayed in the UI.
        val dateFormat = "dd MMM yyyy HH:mm"
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = itemDetails.order_datetime
        binding.tvSoldItemDetailsDate.text = formatter.format(calendar.time)

        binding.rvMySoldItemsList.layoutManager = LinearLayoutManager(this@SoldItemDetailsActivity)
        binding.rvMySoldItemsList.setHasFixedSize(true)

        val cartListAdapter =
            CartItemsListAdapter(this@SoldItemDetailsActivity, itemDetails.items, false)
        binding.rvMySoldItemsList.adapter = cartListAdapter


        binding.tvSoldDetailsAddressType.text = itemDetails.address.type
        binding.tvSoldDetailsFullName.text = itemDetails.address.name
        binding.tvSoldDetailsAddress.text =
            "${itemDetails.address.address}, ${itemDetails.address.zipCode}"
        binding.tvSoldDetailsAdditionalNote.text = itemDetails.address.additionalNote

        if (itemDetails.address.otherDetails.isNotEmpty()) {
            binding.tvSoldDetailsOtherDetails.visibility = View.VISIBLE
            binding.tvSoldDetailsOtherDetails.text = itemDetails.address.otherDetails
        } else {
            binding.tvSoldDetailsOtherDetails.visibility = View.GONE
        }
        binding.tvSoldDetailsMobileNumber.text = itemDetails.address.mobileNumber

        val itemDetailsSubTotalAmountNumeric = itemDetails.sub_total_amount.toDoubleOrNull() ?: 0.0
        val formattedItemDetailsSubTotalAmount = df.format(itemDetailsSubTotalAmountNumeric)
        val itemDetailsShippingChargeNumeric = itemDetails.shipping_charge.toDoubleOrNull() ?: 0.0
        val formattedItemDetailsShippingCharge = df.format(itemDetailsShippingChargeNumeric)
        val itemDetailsTotalAmountNumeric = itemDetails.total_amount.toDoubleOrNull() ?: 0.0
        val formattedItemDetailsTotalAmount = df.format(itemDetailsTotalAmountNumeric)
        binding.tvSoldItemSubTotal.text = "${formattedItemDetailsSubTotalAmount}đ"
        binding.tvSoldItemShippingCharge.text = "${formattedItemDetailsShippingCharge}đ"
        binding.tvSoldItemTotalAmount.text = "${formattedItemDetailsTotalAmount}đ"
    }
}