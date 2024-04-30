package com.store.ffs.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.store.ffs.model.Employee
import com.store.ffs.ui.fragments.EmployeeManagerFragment
import com.store.ffs.R
import com.store.ffs.ui.activities.AddEditEmployeeActivity
import com.store.ffs.utils.Constants
import com.store.ffs.utils.GlideLoader
import com.store.ffs.utils.MSPTextView
import com.store.ffs.utils.MSPTextViewBold

open class EmployeeListAdapter (
    private val context: Context,
    private var list: ArrayList<Employee>,
    private val fragment: EmployeeManagerFragment
): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.employee_list_layout,
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
        val iv_employee_image = holder.itemView.findViewById<ImageView>(R.id.iv_employee_image)
        val tv_employee_name = holder.itemView.findViewById<MSPTextViewBold>(R.id.tv_employee_name)
        val tv_employee_phoneNum = holder.itemView.findViewById<MSPTextView>(R.id.tv_employee_phoneNum)
        val ib_delete_employee = holder.itemView.findViewById<ImageButton>(R.id.ib_delete_employee)

        if (holder is MyViewHolder) {
            GlideLoader(context).loadItemPicture(model.employee_image, iv_employee_image)

            tv_employee_name.text = model.employee_name
            tv_employee_phoneNum.text = model.employee_phone_number
            ib_delete_employee.setOnClickListener {
                fragment.deleteEmployee(model.employee_id)
            }

            holder.itemView.setOnClickListener {
                // Launch Item details screen.
                val intent = Intent(context, AddEditEmployeeActivity::class.java)
                intent.putExtra(Constants.EXTRA_EMPLOYEE_ID, model.employee_id)
                intent.putExtra(Constants.EXTRA_EMPLOYEE_OWNER_ID, model.user_id)
                context.startActivity(intent)
            }
        }
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}