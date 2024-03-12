package com.store.ffs.utils

import androidx.lifecycle.ViewModel

class MyViewModel : ViewModel() {
    var isAdmin: Boolean = false
    fun update(data: Boolean){
        isAdmin = data
    }
}