package com.example.debt.activities

import android.app.Dialog
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.SimpleAdapter
import com.example.debt.R
import kotlinx.android.synthetic.main.dialog_rename.*
import java.util.ArrayList
import java.util.HashMap

class DialogRename(private val id: Int,private val activity: MainActivity): Dialog(activity) {

    lateinit var dao:ContactDao
    lateinit var currentContact: Contact
    private lateinit var mPeopleList: ArrayList<Map<String, String>>
    private lateinit var mAdapter: SimpleAdapter
    private lateinit var mTxtPhoneNo: AutoCompleteTextView

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

        dao=NotebookDatabase.getInstance(activity).dao()
        currentContact=dao.getContactById(id)
        actvRename.setText(currentContact.name)
        actvRename.setSelection(actvRename.text.length)
        tvRename.text = currentContact.name
        btnAtinOzgertiw.setOnClickListener {
            currentContact.name=actvRename.text.toString()
            activity.rewriteContact(currentContact)
            dismiss()
        }
        btnBiykarlaw.setOnClickListener {
            dismiss()
        }
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
