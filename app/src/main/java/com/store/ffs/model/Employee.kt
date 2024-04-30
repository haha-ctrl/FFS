package com.store.ffs.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Employee(
    var user_id: String = "",
    var employee_name: String ="",
    var employee_phone_number: String ="",
    var employee_Email: String ="",
    var employee_Address: String ="",
    var employee_image: String ="",
    var employee_id: String =""
) : Parcelable