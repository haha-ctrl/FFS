package com.store.ffs.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.store.ffs.R
import com.store.ffs.databinding.FragmentItemsBinding
import com.store.ffs.firestore.FirestoreClass
import com.store.ffs.model.Item
import com.store.ffs.ui.activitis.AddItemActivity
import com.store.ffs.ui.activitis.SettingsActivity
import com.store.ffs.ui.adapters.MyItemsListAdapter

class ItemsFragment : BaseFragment() {

    private var _binding: FragmentItemsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // If we want to use the option menu in fragment we need to add it.
        setHasOptionsMenu(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //val homeViewModel =
        //    ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentItemsBinding.inflate(inflater, container, false)
        val root: View = binding.root



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_item_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {

            R.id.action_add_item -> {

                // Launch the SettingActivity on click of action item.
                // START
                startActivity(Intent(activity, AddItemActivity::class.java))
                // END
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun successItemsListFromFireStore(itemsList: ArrayList<Item>) {

        // Hide Progress dialog.
        hideProgressDialog()

        if (itemsList.size > 0) {
            binding.rvMyItemItems.visibility = View.VISIBLE
            binding.tvNoItemsFound.visibility = View.GONE

            binding.rvMyItemItems.layoutManager = LinearLayoutManager(activity)
            binding.rvMyItemItems.setHasFixedSize(true)

            // Pass the third parameter value.
            // START
            val adapteritems =
                MyItemsListAdapter(requireActivity(), itemsList, this@ItemsFragment)
            // END
            binding.rvMyItemItems.adapter = adapteritems
        } else {
            binding.rvMyItemItems.visibility = View.GONE
            binding.tvNoItemsFound.visibility = View.VISIBLE
        }
    }


    private fun getItemListFromFireStore() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of Firestore class.
        FirestoreClass().getitemsList(this@ItemsFragment)
    }


    override fun onResume() {
        super.onResume()

        getItemListFromFireStore()
    }


    fun deleteItem(itemID: String) {
        showAlertDialogToDeleteItem(itemID)
    }


    fun itemDeleteSuccess() {

        // Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
            requireActivity(),
            resources.getString(R.string.item_delete_success_message),
            Toast.LENGTH_SHORT
        ).show()

        // Get the latest items list from cloud firestore.
        getItemListFromFireStore()
    }


    private fun showAlertDialogToDeleteItem(itemID: String) {

        val builder = AlertDialog.Builder(requireActivity())
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        //set message for alert dialog
        builder.setMessage(resources.getString(R.string.delete_dialog_message))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->

            // Call the function to delete the item from cloud firestore.
            // START
            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            // Call the function of Firestore class.
            FirestoreClass().deleteItem(this@ItemsFragment, itemID)
            // END

            dialogInterface.dismiss()
        }

        //performing negative action
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->

            dialogInterface.dismiss()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}