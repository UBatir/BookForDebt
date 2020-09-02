package com.example.debt.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.SimpleAdapter
import android.widget.Toast
import com.example.debt.R
import com.example.debt.activities.*
import com.example.debt.data.Contact
import com.example.debt.interfaces.SetData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.dialog_add_contact.*
import java.util.*
import kotlin.collections.HashMap

class AddContactDialog(context: Context, private val activity: MainActivity):Dialog(context),
    SetData {

    private lateinit var mPeopleList:ArrayList<Map<String, String>>
    private lateinit var mAdapter: SimpleAdapter
    private lateinit var mTxtPhoneNo: AutoCompleteTextView
    private val db=FirebaseFirestore.getInstance()
    private val mAuth=FirebaseAuth.getInstance()
    var currentContact = Contact()
    var summa:Long=0
    private var time: String=""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_add_contact)
        mPeopleList = ArrayList()
        populatePeopleList()
        mTxtPhoneNo=findViewById(R.id.actvName)
        mAdapter= SimpleAdapter(
            context, mPeopleList, R.layout.auto_complete_tv, arrayOf("Name"),
            intArrayOf(R.id.tvNameContact)
        )
        mTxtPhoneNo.setAdapter(mAdapter)
        actvName.setSelection(actvName.text!!.length)


        mTxtPhoneNo.onItemClickListener =
            AdapterView.OnItemClickListener { av, _, index, _ ->
                val map = av.getItemAtPosition(index) as Map<*, *>
                val name = map["Name"]
                mTxtPhoneNo.setText("$name")
                mTxtPhoneNo.setSelection(mTxtPhoneNo.text!!.length)
            }

        val c=Calendar.getInstance()
        val year=c.get(Calendar.YEAR)
        val month=c.get(Calendar.MONTH)
        val day=c.get(Calendar.DAY_OF_MONTH)
        tvSane.text="$day.${month+1}.$year"
        val hour =c.get(Calendar.HOUR_OF_DAY)
        val minute=c.get(Calendar.MINUTE)
        time = if(minute<10){
            "$hour:0$minute"
        }else{
            "$hour:$minute"
        }

        btnBiykarlaw.setOnClickListener {
            dismiss()
        }
        tvSane.setOnClickListener{
            val dialog= DataDialog(context, this)
            dialog.show()
        }
        calculator.setOnClickListener {
            val dialog=
                CalculatorDialog(context, this)
            dialog.show()
        }

        btnPayda.setOnClickListener {
            currentContact.debt=1
            summa= etSumma.text.toString().toLong()
            addContact(currentContact.debt,summa)
            dismiss()
        }

        btnQariz.setOnClickListener {
            currentContact.debt=0
            summa=-etSumma.text.toString().toLong()
            addContact(currentContact.debt,summa)
            dismiss()
        }
    }

    fun addContact(debt:Int,summa: Long){
        if (actvName.text.isNotEmpty() && etSumma.text!!.isNotEmpty()){
            val map: MutableMap<String, Any> = mutableMapOf()
            map["name"] = actvName.text.toString()
            map["comment"] = etKommentariy.text.toString()
            map["summa"] = summa
            map["date"] = tvSane.text.toString()
            map["debt"]=debt
            map["time"]=time
        db.collection("contacts").document(mAuth.currentUser!!.uid).collection("data").document().set(map)
        db.collection("contacts").document(mAuth.currentUser!!.uid).collection("history").document().set(map)
            .addOnSuccessListener {
                Toast.makeText(context, "Было добавлено", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(context, "Заполните поля", Toast.LENGTH_SHORT).show()
        }
    }

    private fun populatePeopleList() {
        mPeopleList.clear()
        val people = activity.contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI, null, null, null, null
        )
        if (people != null) {
            while (people.moveToNext())
            {
                val contactName = people.getString(
                    people
                        .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                )
                if (contactName!=null)
                {
                    val namePhoneType = HashMap<String, String>()
                    namePhoneType["Name"] = contactName
                    mPeopleList.add(namePhoneType)
                }
            }
        }
        people?.close()
        activity.startManagingCursor(people)
    }

    override fun setData(data: String,time:String) {
        tvSane.text=data
        this.time =time
    }

    override fun setSum(sum: Long) {
        etSumma.setText(sum.toString())
    }
}