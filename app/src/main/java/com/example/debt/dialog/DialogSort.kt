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
            listener.onClickSort("name", "ASCENDING")
            dismiss()
        }
        tvASCBalance.setOnClickListener{
            listener.onClickSort("summa", "ASCENDING")
            dismiss()
        }
        tvASCDate.setOnClickListener{
            listener.onClickSort("date", "ASCENDING")
            dismiss()
        }
        tvDESCName.setOnClickListener{
            listener.onClickSort("name", "DESCENDING")
        dismiss()
        }
        tvDESCBalance.setOnClickListener{
            listener.onClickSort("summa", "DESCENDING")
        dismiss()
        }
        tvDESCDate.setOnClickListener{
            listener.onClickSort("date", "DESCENDING")
        dismiss()
        }
    }
}