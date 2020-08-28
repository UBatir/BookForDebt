package com.example.debt.activities

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.debt.R
import kotlinx.android.synthetic.main.dialog_change_balance.view.*
import kotlinx.android.synthetic.main.dialog_change_balance.view.tvName
import kotlinx.android.synthetic.main.dialog_change_balance.view.tvSumma
import kotlinx.android.synthetic.main.item_contact.view.*

class ListAdapter(private val activity: MainActivity, private val listener: ContactItemClickListener):
    RecyclerView.Adapter<ListAdapter.ListViewHolder>() {


    inner class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        @SuppressLint("SetTextI18n")
        fun populateModel(model:Contact, activity: MainActivity){
            itemView.tvName.text=model.name
            itemView.tvKommentariy.text=model.comment
            itemView.btnOptions.setOnClickListener{
                activity.onOptionsButtonClick(itemView.btnOptions,model,model.id)
            }
            when (model.debt) {
                1 -> {
                    itemView.tvSumma.setTextColor(Color.rgb(76,175,80))
                    itemView.tvSumma.text="+${model.summa}"
                }
                -1 -> {
                    itemView.tvSumma.setTextColor(Color.rgb(97,97,97))
                    itemView.tvSumma.text= model.summa.toString()
                }
                else -> {
                    itemView.tvSumma.setTextColor(Color.rgb(229,57,53))
                    itemView.tvSumma.text= model.summa.toString()
                }
            }

            itemView.setOnClickListener {
                listener.onContactItemClick(model.id)
            }
        }
    }

    var models: List<Contact> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_contact,parent,false)
        return ListViewHolder(view)
    }

    override fun getItemCount()=models.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.populateModel(models[position],activity)
    }
}