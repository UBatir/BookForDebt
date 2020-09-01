package com.example.debt.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.example.debt.R
import com.example.debt.interfaces.SortClickListener
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.dialog_sort.*

class DialogSort(context: Context, private  val listener: SortClickListener): Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_sort)
        tvASCName.setOnClickListener{
            listener.onClickSort("name", Query.Direction.ASCENDING)
            dismiss()
        }
        tvASCBalance.setOnClickListener{
            listener.onClickSort("summa", Query.Direction.ASCENDING)
            dismiss()
        }
        tvASCDate.setOnClickListener{
            listener.onClickSort("date", Query.Direction.ASCENDING)
            dismiss()
        }
        tvDESCName.setOnClickListener{
            listener.onClickSort("name", Query.Direction.DESCENDING)
        dismiss()
        }
        tvDESCBalance.setOnClickListener{
            listener.onClickSort("summa", Query.Direction.DESCENDING)
        dismiss()
        }
        tvDESCDate.setOnClickListener{
            listener.onClickSort("date", Query.Direction.DESCENDING)
        dismiss()
        }
    }
}