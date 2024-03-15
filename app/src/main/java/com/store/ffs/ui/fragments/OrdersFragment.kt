package com.store.ffs.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.store.ffs.R
import com.store.ffs.databinding.FragmentOrdersBinding
import com.store.ffs.firestore.FirestoreClass
import com.store.ffs.model.Order
import com.store.ffs.ui.activitis.DashboardActivity
import com.store.ffs.ui.adapters.MyOrdersListAdapter
import com.store.ffs.utils.Constants
import com.store.ffs.utils.MyViewModel


class OrdersFragment : BaseFragment() {

    private var _binding: FragmentOrdersBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val model: MyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // val notificationsViewModel =
        //    ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun populateOrdersListInUI(ordersList: ArrayList<Order>) {
        val filteredOrdersList = if (model.isAdmin) {
            getNotConfirmedOrders(ordersList)
        } else {
            ordersList
        }
        // Hide the progress dialog.
        hideProgressDialog()

        // Populate the orders list in the UI.
        // START
        if (ordersList.size > 0) {

            binding.rvMyOrderItems.visibility = View.VISIBLE
            binding.tvNoOrdersFound.visibility = View.GONE

            binding.rvMyOrderItems.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL, false)
            binding.rvMyOrderItems.setHasFixedSize(true)

            val myOrdersAdapter = MyOrdersListAdapter(requireActivity(), filteredOrdersList)
            binding.rvMyOrderItems.adapter = myOrdersAdapter
        } else {
            binding.rvMyOrderItems.visibility = View.GONE
            binding.tvNoOrdersFound.visibility = View.VISIBLE
        }
        // END
    }

    private fun getNotConfirmedOrders(ordersList: List<Order>): ArrayList<Order> {
        return ArrayList(ordersList.filter { it.status != Constants.ORDER_CONFIRMED })
    }


    private fun getMyOrdersList() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getMyOrdersList(this@OrdersFragment, model.isAdmin)
    }




    override fun onResume() {
        super.onResume()

        getMyOrdersList()

    }


}