package com.example.debt.interfaces

import com.google.firebase.firestore.Query


interface SortClickListener {

    fun onClickSort(key: String, direction: Query.Direction)

}