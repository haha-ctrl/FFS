package com.store.ffs.ui.adapters


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.store.ffs.R
import com.store.ffs.model.Item
import com.store.ffs.ui.activitis.ItemDetailsActivity
import com.store.ffs.ui.fragments.ItemsFragment
import com.store.ffs.utils.Constants
import com.store.ffs.utils.GlideLoader
import com.store.ffs.utils.MSPTextView
import com.store.ffs.utils.MSPTextViewBold

open class MyItemsListAdapter (
    private val context: Context,
    private var list: ArrayList<Item>,
    private val fragment: ItemsFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_list_layout,
                parent,
                false
            )
        )

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        val iv_item_image = holder.itemView.findViewById<ImageView>(R.id.iv_item_image)
        val tv_item_name = holder.itemView.findViewById<MSPTextViewBold>(R.id.tv_item_name)
        val tv_item_price = holder.itemView.findViewById<MSPTextView>(R.id.tv_item_price)
        val ib_delete_item = holder.itemView.findViewById<ImageButton>(R.id.ib_delete_item)

        if (holder is MyViewHolder) {
            GlideLoader(context).loadItemPicture(model.image, iv_item_image)

            tv_item_name.text = model.title
            tv_item_price.text = "${model.price}đ"

            ib_delete_item.setOnClickListener {
                fragment.deleteItem(model.item_id)
            }

            holder.itemView.setOnClickListener {
                // Launch Item details screen.
                val intent = Intent(context, ItemDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_ITEM_ID, model.item_id)
                intent.putExtra(Constants.EXTRA_ITEM_OWNER_ID, model.user_id)
                context.startActivity(intent)
            }
        }
    }




    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}