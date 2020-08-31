package com.example.debt.dialog

import android.app.Dialog
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.SimpleAdapter
import android.widget.Toast
import com.example.debt.R
import com.example.debt.data.Contact
import com.example.debt.activities.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.dialog_add_contact.*
import kotlinx.android.synthetic.main.dialog_change_balance.*
import kotlinx.android.synthetic.main.dialog_change_balance.etKommentariy
import kotlinx.android.synthetic.main.dialog_change_balance.etSumma
import kotlinx.android.synthetic.main.dialog_change_balance.tvSane
import kotlinx.android.synthetic.main.dialog_rename.*
import kotlinx.android.synthetic.main.dialog_rename.btnBiykarlaw
import java.util.ArrayList
import java.util.HashMap

class DialogRename(private val id: String, private val activity: MainActivity): Dialog(activity) {

    private var currentContact = Contact()
    private lateinit var mPeopleList: ArrayList<Map<String, String>>
    private lateinit var mAdapter: SimpleAdapter
    private lateinit var mTxtPhoneNo: AutoCompleteTextView
    private val db= FirebaseFirestore.getInstance()
    private val mAuth= FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_rename)
        mPeopleList = ArrayList()
        populatePeopleList()
        mTxtPhoneNo=findViewById(R.id.actvRename)
        mAdapter= SimpleAdapter(context,mPeopleList,R.layout.auto_complete_tv, arrayOf("Name"),
            intArrayOf(R.id.tvNameContact))
        mTxtPhoneNo.setAdapter(mAdapter)


        mTxtPhoneNo.onItemClickListener =
            AdapterView.OnItemClickListener { av, _, index, _ ->
                val map = av.getItemAtPosition(index) as Map<*, *>
                val name = map["Name"]
                mTxtPhoneNo.setText("$name")
                mTxtPhoneNo.setSelection(mTxtPhoneNo.text!!.length)
            }

        db.collection("contacts").document(mAuth.currentUser!!.uid).collection("data").document(id).get().addOnSuccessListener {
            actvRename.setText(it.get("name").toString())
            actvRename.setSelection(actvRename.text.length)
            tvRename.text = it.get("name").toString()
            val summa=it.get("summa").toString().toLong()
            val debt=it.get("debt").toString().toInt()
            val date=it.get("date").toString()
            val comment=it.get("comment").toString()

            btnAtinOzgertiw.setOnClickListener {
                val update = hashMapOf<String, Any>(
                    "name" to actvRename.text.toString()
                )
                val updates = hashMapOf<String, Any>(
                    "name" to actvRename.text.toString(),
                    "summa" to summa,
                    "debt" to debt,
                    "date" to date,
                    "comment" to comment
                )
                db.collection("contacts").document(mAuth.currentUser!!.uid).collection("data").document(id).update(update)
                db.collection("contacts").document(mAuth.currentUser!!.uid).collection("history").document().set(updates)

                dismiss()
            }
            btnBiykarlaw.setOnClickListener {
                dismiss()
            }}
    }
    private fun populatePeopleList() {
        mPeopleList.clear()
        val people = activity.contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        if (people != null) {
            while (people.moveToNext())
            {
                val contactName = people.getString(people
                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
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
}