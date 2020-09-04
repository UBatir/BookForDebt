package com.example.debt.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.debt.R
import com.example.debt.activities.HistoryActivity
import com.example.debt.data.Contact
import kotlinx.android.synthetic.main.dialog_change_balance.view.tvSumma
import kotlinx.android.synthetic.main.item_contact.view.*
import kotlinx.android.synthetic.main.item_contact.view.btnOptions
import kotlinx.android.synthetic.main.item_contact.view.tvName
import kotlinx.android.synthetic.main.item_history_contact.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HistoryAdapter(private val activity: HistoryActivity):
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {


    inner class HistoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("SetTextI18n")
        fun populateModel(model: Contact, activity: HistoryActivity){
            itemView.tvName.text=model.name
            itemView.tvComment.text = model.comment
            itemView.tvTimeHistory.text = model.time
            itemView.btnOptions.setOnClickListener{
                activity.onOptionsButtonClick(itemView.btnOptions, model, model.id)
            }
            when (model.debt) {
                1 -> {
                    itemView.tvSumma.setTextColor(Color.rgb(76, 175, 80))
                    itemView.tvSumma.text = "+${model.summa}"
                }
                -1 -> {
                    itemView.tvSumma.setTextColor(Color.rgb(97, 97, 97))
                    itemView.tvSumma.text = model.summa.toString()
                }
                else -> {
                    itemView.tvSumma.setTextColor(Color.rgb(229, 57, 53))
                    itemView.tvSumma.text= model.summa.toString()
                }
            }

        }
    }

    var models: MutableList<Contact> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(
            R.layout.item_history_contact,
            parent,
            false
        )
        return HistoryViewHolder(view)
    }

    override fun getItemCount()=models.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.populateModel(models[position], activity)
    }
}