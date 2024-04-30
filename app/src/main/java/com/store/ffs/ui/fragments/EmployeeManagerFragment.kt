package com.store.ffs.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.store.ffs.R
import com.store.ffs.databinding.FragmentEmployeeManagerBinding
import com.store.ffs.firestore.FirestoreClass
import com.store.ffs.model.Employee
import com.store.ffs.ui.activities.AddEditEmployeeActivity
import com.store.ffs.ui.adapters.EmployeeListAdapter

class EmployeeManagerFragment: BaseFragment() {

    private var _binding: FragmentEmployeeManagerBinding? = null;
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

        _binding = FragmentEmployeeManagerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentEmployeeManagerBinding.inflate(inflater, container, false)
//        val view = binding.root
//
//        // Ánh xạ RecyclerView từ layout
//        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_employee_list)
//
//        // Thiết lập LinearLayoutManager cho RecyclerView
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//
//        // Gọi hàm để lấy danh sách nhân viên từ Firestore
//        getEmployeeListFromFireStore()
//
//        return view
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_employee_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(employee: MenuItem): Boolean {
        val id = employee.itemId

        when (id) {

            R.id.action_add_employee -> {

                // Launch the SettingActivity on click of action item.
                // START
                startActivity(Intent(activity, AddEditEmployeeActivity::class.java))
                // END
                return true
            }
        }
        return super.onOptionsItemSelected(employee)
    }

    fun successEmployeeListFromFireStore(employeeList: ArrayList<Employee>) {

        // Hide Progress dialog.
        hideProgressDialog()

        if (employeeList.size > 0) {
            binding.rvEmployeeList.visibility = View.VISIBLE
            binding.tvNoEmployee.visibility = View.GONE

            binding.rvEmployeeList.layoutManager = LinearLayoutManager(activity)
            binding.rvEmployeeList.setHasFixedSize(true)

            // Pass the third parameter value.
            // START
            val adapteremployees = EmployeeListAdapter(requireActivity(), employeeList, this@EmployeeManagerFragment)
            // END
            binding.rvEmployeeList.adapter = adapteremployees
        } else {
            binding.rvEmployeeList.visibility = View.GONE
            binding.tvNoEmployee.visibility = View.VISIBLE
        }
    }

    private fun getEmployeeListFromFireStore() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of Firestore class.
        FirestoreClass().getemployeeList(this@EmployeeManagerFragment)
    }

    override fun onResume() {
        super.onResume()

        getEmployeeListFromFireStore()
    }

    fun deleteEmployee(employeeID: String) {
        showAlertDialogToDeleteEmployee(employeeID)
    }

    fun employeeDeleteSuccess() {

        // Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
            requireActivity(),
            resources.getString(R.string.employee_delete_success_message),
            Toast.LENGTH_SHORT
        ).show()

        // Get the latest items list from cloud firestore.
        getEmployeeListFromFireStore()
    }

    private fun showAlertDialogToDeleteEmployee(employeeID: String) {

        val builder = AlertDialog.Builder(requireActivity())
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        //set message for alert dialog
        builder.setMessage(resources.getString(R.string.delete_employee_dialog_message))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->

            // Call the function to delete the item from cloud firestore.
            // START
            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            // Call the function of Firestore class.
            FirestoreClass().deleteEmployee(this@EmployeeManagerFragment, employeeID)
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