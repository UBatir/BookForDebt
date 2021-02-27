package com.example.debt.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.example.debt.R
import com.example.debt.data.Contact
import com.example.debt.activities.MainActivity
import com.example.debt.interfaces.SetData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.dialog_add_contact.etSumma
import kotlinx.android.synthetic.main.dialog_add_contact.tvSane
import kotlinx.android.synthetic.main.dialog_change_balance.*
import kotlinx.android.synthetic.main.dialog_change_balance.tvName
import kotlinx.android.synthetic.main.dialog_change_balance.tvSumma
import kotlinx.android.synthetic.main.item_contact.*
import kotlinx.android.synthetic.main.item_history_contact.*
import java.util.*

class DialogChangeBalance(private val activity: MainActivity, val id: String): Dialog(activity),
    SetData {
    private  val mAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var currentContact = Contact()
    private var time: String=""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_change_balance)

        db.collection("contacts").document(mAuth.currentUser!!.uid).collection("data").document(id).get()
            .addOnSuccessListener {
            tvName.text= it.get("name").toString()
            tvSumma.text=it.get("summa").toString()
            currentContact.debt=it.get("debt").toString().toInt()
            currentContact.summa=it.get("summa").toString().toLong()
                currentContact.comment = it.get("comment").toString()
            val c= Calendar.getInstance()
            val year=c.get(Calendar.YEAR)
            val month=c.get(Calendar.MONTH)
            val day=c.get(Calendar.DAY_OF_MONTH)
            tvSane.text="$day.${month + 1}.$year"
            val hour =c.get(Calendar.HOUR_OF_DAY)
            val minute=c.get(Calendar.MINUTE)
            time = if(minute<10){
                "$day.${month+1}.$year $hour:0$minute"
            }else{
                "$day.${month+1}.$year $hour:$minute"
            }
            tvSane.setOnClickListener{
                val dialog= DataDialog(context, this)
                dialog.show()
            }

            if(currentContact.debt==1){
                    tvMinusText.setTextColor(Color.rgb(76,175,80))
                    tvPlusText.setTextColor(Color.rgb(76,175,80))
                    tvMinusText.text = "+${it.get("summa").toString()}"
                    tvPlusText.text = "+${it.get("summa").toString()}"
            }else if(currentContact.debt==-1){
                    tvPlusText.setTextColor(Color.rgb(97,97,97))
                    tvMinusText.setTextColor(Color.rgb(97,97,97))
                    tvMinusText.text = it.get("summa").toString()
                    tvPlusText.text = it.get("summa").toString()
            }
            else{
                    tvMinusText.setTextColor(Color.rgb(229,57,53))
                    tvPlusText.setTextColor(Color.rgb(229,57,53))
                    tvMinusText.text = it.get("summa").toString()
                    tvPlusText.text = it.get("summa").toString()
            }
            etSumma.addTextChangedListener( object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    val a=if (etSumma.text.toString().trim() == "") 0 else etSumma.text.toString().toLong()
                    if(currentContact.debt==1){
                        tvPlusText.setTextColor(Color.rgb(76,175,80))
                        tvPlusText.text = "+${currentContact.summa + a}"
                        when {
                            currentContact.summa- a>0 -> {
                                tvMinusText.setTextColor(Color.rgb(76,175,80))
                                tvMinusText.text = "+${currentContact.summa- a}"
                            }
                            currentContact.summa- a<0 -> {
                                tvMinusText.setTextColor(Color.rgb(229,57,53))
                                tvMinusText.text = "${currentContact.summa- a}"
                            }
                            else -> {
                                tvMinusText.setTextColor(Color.rgb(97,97,97))
                                tvMinusText.text = "${currentContact.summa- a}"
                            }
                        }
                    } else{
                        tvMinusText.setTextColor(Color.rgb(229,57,53))
                        tvMinusText.text = "${currentContact.summa- a}"
                        when {
                            currentContact.summa+ a>0 -> {
                                tvPlusText.setTextColor(Color.rgb(76,175,80))
                                tvPlusText.text = "+${currentContact.summa+ a}"
                            }
                            currentContact.summa+ a<0 -> {
                                tvPlusText.setTextColor(Color.rgb(229,57,53))
                                tvPlusText.text = "${currentContact.summa+ a}"
                            }
                            else -> {
                                tvPlusText.setTextColor(Color.rgb(97,97,97))
                                tvPlusText.text = "${currentContact.summa+ a}"
                            }
                        }
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }
            })
            btnPlus.setOnClickListener{
                if (etSumma.text.toString().trim() == ""||etSumma.text.toString().toInt()==0) {
                    Toast.makeText(context,"Сумма не задана или равна нулю. Ведите не нулевое значение!",
                        Toast.LENGTH_SHORT).show()
                } else{
                    val a=etSumma.text.toString().toInt()
                    if(currentContact.debt==1){
                        currentContact.debt=1
                        tvPlusText.text = "${currentContact.summa+ a}"
                    } else{
                        when {
                            currentContact.summa+ a>0 -> {
                                currentContact.debt=1
                                tvPlusText.text = "${currentContact.summa+ a}"
                            }
                            currentContact.summa+ a<0 -> {
                                currentContact.debt=0
                                tvPlusText.text = "${currentContact.summa+ a}"
                            }
                            currentContact.summa.toInt()+ a==0 -> {
                                currentContact.debt=-1
                                tvPlusText.text = "${currentContact.summa+ a}"
                            }
                        }
                    }
                    val update= hashMapOf(
                        "summa" to tvPlusText.text.toString().toLong(),
                        "comment" to etKommentariy.text.toString(),
                        "debt" to currentContact.debt,
                        "time" to time
                    )
                    val updates= hashMapOf(
                        "summa" to a,
                        "name" to tvName.text.toString(),
                        "debt" to currentContact.debt,
                        "comment" to etKommentariy.text.toString(),
                        "time" to time
                    )
                    db.collection("contacts").document(mAuth.currentUser!!.uid).collection("data").document(id).update(update)
                    db.collection("contacts").document(mAuth.currentUser!!.uid).collection("history").document().set(updates)
                    dismiss()
                }
            }
            btnMinus.setOnClickListener{
                if (etSumma.text.toString().trim() == ""||etSumma.text.toString().toInt()==0) {
                    Toast.makeText(context,"Сумма не задана или равна нулю. Ведите не нулевое значение!",
                        Toast.LENGTH_SHORT).show()
                }
                else{
                    val a=etSumma.text.toString().toInt()
                    if(currentContact.debt==0) {
                        when {
                            currentContact.summa.toInt()+ a>0 -> {
                                currentContact.debt=0
                                tvMinusText.text = "${currentContact.summa.toInt()- a}"
                            }
                            currentContact.summa.toInt()+ a<0 -> {
                                currentContact.debt=0
                                tvMinusText.text = "${currentContact.summa.toInt()- a}"
                            }
                            else -> {
                                tvMinusText.setTextColor(Color.rgb(97,97,97))
                                tvMinusText.text = (currentContact.summa.toInt()+ a).toString()
                            }
                        }
                    }else{
                        when {
                            currentContact.summa.toInt()- a>0 -> {
                                currentContact.debt=1
                                tvMinusText.text = "${currentContact.summa.toInt() - a}"
                            }
                            currentContact.summa.toInt()- a<0 -> {
                                currentContact.debt=0
                                tvMinusText.text = "${currentContact.summa.toInt() - a}"
                            }
                            else -> {
                                currentContact.debt=-1
                                tvMinusText.text = "${currentContact.summa.toInt() - a}"
                            }
                        }
                    }
                    val update= hashMapOf<String,Any>(
                        "summa" to tvMinusText.text.toString().toLong(),
                        "comment" to etKommentariy.text.toString(),
                        "debt" to currentContact.debt,
                        "time" to time

                    )

                    val updates= hashMapOf<String,Any>(
                        "summa" to -a,
                        "name" to tvName.text.toString(),
                        "debt" to 0,
                        "time" to time,
                        "comment" to etKommentariy.text.toString()
                    )
                    db.collection("contacts").document(mAuth.currentUser!!.uid).collection("history").document().set(updates)
                    db.collection("contacts").document(mAuth.currentUser!!.uid).collection("data").document(id).update(update)
                    dismiss()
                }
            }

        }


        calculator1.setOnClickListener {
            val dialog= CalculatorDialog(context, this)
            dialog.show()
        }

    }

    override fun setData(data: String,time:String) {
        tvSane.text=data
        this.time ="$data $time"
    }

    override fun setSum(sum: Long) {
        etSumma.setText("$sum")
    }

}
