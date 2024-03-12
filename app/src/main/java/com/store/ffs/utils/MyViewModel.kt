package com.store.ffs.utils

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.store.ffs.model.User

class MyViewModel : ViewModel() {
    var isAdmin: Boolean = false
    var user: User ?= null
    fun update(data: Boolean){
        isAdmin = data
    }
    fun update(data: User) {
        user = data
    }
}