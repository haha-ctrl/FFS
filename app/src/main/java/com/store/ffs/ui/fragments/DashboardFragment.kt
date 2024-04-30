package com.store.ffs.ui.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.store.ffs.R
import com.store.ffs.databinding.FragmentDashboardBinding
import com.store.ffs.firestore.FirestoreClass
import com.store.ffs.model.Item
import com.store.ffs.model.User
import com.store.ffs.ui.activitis.CartListActivity
import com.store.ffs.ui.activitis.ItemDetailsActivity
import com.store.ffs.ui.activitis.SettingsActivity
import com.store.ffs.ui.adapters.DashboardItemsListAdapter
import com.store.ffs.utils.Constants
import com.store.ffs.utils.MyViewModel


class DashboardFragment : BaseFragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val model: MyViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // If we want to use the option menu in fragment we need to add it.
//        arguments?.let {
//            isAdmin = it.getBoolean("isAdmin", false)
//        }
        setHasOptionsMenu(true)

    }
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // val dashboardViewModel =
        //    ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)
        // Log.d("DashboardFragment", "onCreateOptionsMenu")
        val actionCart = menu.findItem(R.id.action_cart)
        val actionMenu = menu.findItem(R.id.action_menu)
        actionCart.isVisible = !model.isAdmin
        actionMenu.isVisible = model.isAdmin

        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {

            R.id.action_settings -> {

                // Launch the SettingActivity on click of action item.
                // START
                val intent = Intent(activity, SettingsActivity::class.java)
                intent.putExtra(Constants.ADMIN_STATUS, model.isAdmin)
                startActivity(intent)
                // END
                return true
            }

            R.id.action_cart -> {
                Log.e("model.token", model.token)
                val intent = Intent(activity, CartListActivity::class.java)
                intent.putExtra(Constants.USER_TOKEN, model.token)
                startActivity(intent)
                return true
            }

            R.id.action_menu -> {
                showDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottomsheetlayout)

        val manageStaffLayout = dialog.findViewById<LinearLayout>(R.id.layoutManageStaff)
        val manageTableLayout = dialog.findViewById<LinearLayout>(R.id.layoutManageTable)

        manageStaffLayout.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(requireContext(), "Manage staff is Clicked", Toast.LENGTH_SHORT).show()
        }

        manageTableLayout.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(requireContext(), "Manage Table is Clicked", Toast.LENGTH_SHORT).show()
        }


        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }

    fun successDashboardItemsList(dashboardItemsList: ArrayList<Item>) {

        // Hide the progress dialog.
        hideProgressDialog()

        if (dashboardItemsList.size > 0) {

            binding.rvDashboardItems.visibility = View.VISIBLE
            binding.tvNoDashboardItemsFound.visibility = View.GONE

            binding.rvDashboardItems.layoutManager = GridLayoutManager(activity, 2)
            binding.rvDashboardItems.setHasFixedSize(true)

            val adapter = DashboardItemsListAdapter(requireActivity(), dashboardItemsList, model.token)
            binding.rvDashboardItems.adapter = adapter

            adapter.setOnClickListener(object: DashboardItemsListAdapter.OnClickListener {
                override fun onClick(position: Int, item: Item) {
                    // Launch the item details screen from the dashboard.
                    // START
                    val intent = Intent(context, ItemDetailsActivity::class.java)
                    intent.putExtra(Constants.EXTRA_ITEM_ID, item.item_id)
                    startActivity(intent)
                    // END
                }
            })
        } else {
            binding.rvDashboardItems.visibility = View.GONE
            binding.tvNoDashboardItemsFound.visibility = View.VISIBLE
        }
    }


    private fun getDashboardItemsList() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getDashboardItemsList(this@DashboardFragment)
    }

    override fun onResume() {
        super.onResume()

        getDashboardItemsList()
    }

}