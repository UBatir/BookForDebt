package com.example.debt.dialog

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.format.DateUtils
import com.example.debt.R
import com.example.debt.interfaces.SetData
import kotlinx.android.synthetic.main.dialog_data.*
import java.util.*


class DataDialog(context: Context, private val listener: SetData):Dialog(context) {

    private var c = Calendar.getInstance()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.dialog_data)
        calendarView.setOnDateChangeListener{ _, year, month, dayOfMonth ->
            btnPositive.setOnClickListener {
                val data ="$dayOfMonth.${month + 1}.$year"
                setInitialDateTime()
                val time=tvTime.text.toString()
                listener.setData(data,time)
                dismiss()
            }
        }
        btnNegative.setOnClickListener {
            dismiss()
        }
        tvTime.setOnClickListener {
            setTime()
        }
    }

    private var t:TimePickerDialog.OnTimeSetListener =
        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            c.set(Calendar.HOUR_OF_DAY, hourOfDay)
            c.set(Calendar.MINUTE, minute)
            setInitialDateTime()
        }

    private fun setInitialDateTime() {
        tvTime.text = DateUtils.formatDateTime(context,
            c.timeInMillis,
            (DateUtils.FORMAT_SHOW_TIME))
    }

    private fun setTime() {
        TimePickerDialog(
            context,t,
            c.get(Calendar.HOUR_OF_DAY),
            c.get(Calendar.MINUTE), true
        )
            .show()
    }
}
