package com.store.ffs.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.store.ffs.R
import com.store.ffs.model.Item
import com.store.ffs.ui.activitis.ItemDetailsActivity
import com.store.ffs.utils.Constants
import com.store.ffs.utils.GlideLoader
import com.store.ffs.utils.MSPTextView
import com.store.ffs.utils.MSPTextViewBold
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class DashboardItemsListAdapter (
    private val context: Context,
    private var list: ArrayList<Item>,
    private val token: String

    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onClickListener: OnClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_dashboard_layout,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        val iv_dashboard_item_image = holder.itemView.findViewById<ImageView>(R.id.iv_dashboard_item_image)
        val tv_dashboard_item_title = holder.itemView.findViewById<MSPTextViewBold>(R.id.tv_dashboard_item_title)
        val tv_dashboard_item_price = holder.itemView.findViewById<MSPTextView>(R.id.tv_dashboard_item_price)

        if (holder is MyViewHolder) {
            GlideLoader(context).loadItemPicture(
                model.image,
                iv_dashboard_item_image
            )
            tv_dashboard_item_title.text = model.title

            val nf: NumberFormat = NumberFormat.getNumberInstance(Locale.GERMAN)
            val df = nf as DecimalFormat
            // Convert the price to a numeric type (Double)
            val priceNumeric = model.price.toDoubleOrNull() ?: 0.0
            // Format the price with a thousand separator
            val formattedPrice = df.format(priceNumeric)
            tv_dashboard_item_price.text = "${formattedPrice}đ"

            holder.itemView.setOnClickListener {
                // Launch Item details screen.
                val intent = Intent(context, ItemDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_ITEM_ID, model.item_id)
                intent.putExtra(Constants.EXTRA_ITEM_OWNER_ID, model.user_id)
                intent.putExtra(Constants.USER_TOKEN, token)
                context.startActivity(intent)
            }
        }
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)


    interface OnClickListener {

        // Define a function to get the required params when user clicks on the item view in the interface.
        // START
        fun onClick(position: Int, item: Item)
        // END
    }


    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }
}
