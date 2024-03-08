package com.store.ffs.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.store.ffs.R
import com.store.ffs.databinding.FragmentItemsBinding
import com.store.ffs.databinding.FragmentSoldItemsBinding
import com.store.ffs.firestore.FirestoreClass
import com.store.ffs.model.SoldItem
import com.store.ffs.ui.adapters.SoldItemsListAdapter


class SoldItemsFragment : BaseFragment() {

    private lateinit var binding: FragmentSoldItemsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSoldItemsBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onResume() {
        super.onResume()
        getSoldItemsList()
    }

    private fun getSoldItemsList() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of Firestore class.
        FirestoreClass().getSoldItemsList(this@SoldItemsFragment)
    }


    fun successSoldItemsList(soldItemsList: ArrayList<SoldItem>) {

        // Hide Progress dialog.
        hideProgressDialog()

        // Populate the list in the RecyclerView using the adapter class.
        // START
        if (soldItemsList.size > 0) {
            binding.rvSoldItemItems.visibility = View.VISIBLE
            binding.tvNoSoldItemsFound.visibility = View.GONE

            binding.rvSoldItemItems.layoutManager = LinearLayoutManager(activity)
            binding.rvSoldItemItems.setHasFixedSize(true)

            val soldItemsListAdapter =
                SoldItemsListAdapter(requireActivity(), soldItemsList)
            binding.rvSoldItemItems.adapter = soldItemsListAdapter
        } else {
            binding.rvSoldItemItems.visibility = View.GONE
            binding.tvNoSoldItemsFound.visibility = View.VISIBLE
        }
        // END
    }
}