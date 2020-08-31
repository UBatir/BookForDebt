package com.example.debt.activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import com.example.debt.R
import com.example.debt.adapters.HistoryAdapter
import com.example.debt.data.Contact
import com.example.debt.dialog.DialogChangeBalance
import com.example.debt.dialog.DialogRename
import com.example.debt.interfaces.ContactItemClickListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.app_bar_history.*
import kotlinx.android.synthetic.main.content_history.*
import kotlinx.android.synthetic.main.dialog_add_contact.*

class HistoryActivity : AppCompatActivity(){

    private val mAdapter = HistoryAdapter(this)
    private val db= FirebaseFirestore.getInstance()
    private val mAuth= FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        val toolbar: Toolbar = findViewById(R.id.toolbarHistory)
        setSupportActionBar(toolbar)
        title = "История"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        recyclerViewHistory.adapter=mAdapter
        getDataToHistory()
    }

    private fun getDataToHistory(){
        val result: MutableList<Contact> = mutableListOf()
        db.collection("contacts").document(mAuth.currentUser!!.uid).collection("history")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(
                        applicationContext,
                        error.message.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                    return@addSnapshotListener
                }
                result.clear()
                val idsRef: CollectionReference = db.collection("contacts").document(mAuth.currentUser!!.uid).collection("history")
                val a = intent.getStringExtra("key")
                val query : Query = idsRef.whereEqualTo("name", a)
                query.get()
                    .addOnSuccessListener {
                        it.documents.forEach {doc ->
                            val model = doc.toObject(Contact::class.java)
                            model?.id = doc.id
                            model?.let {
                                result.add(model)
                            }
                        }
                        mAdapter.models = result
                    }
            }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                val intent = Intent(
                    this,
                    MainActivity::class.java
                )
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun onOptionsButtonClick(view: View, contact: Contact, id: String){
        db.collection("contacts").document(mAuth.currentUser!!.uid).collection("data").document(id).get().addOnSuccessListener {doc->
            val optionsMenu= PopupMenu(this, view)
            val menuInflater=optionsMenu.menuInflater
            menuInflater.inflate(R.menu.menu_history_options, optionsMenu.menu)
            optionsMenu.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.item_delete -> {
                        val dialog = AlertDialog.Builder(this)
                        dialog.setTitle("Удаление контакта")
                        dialog.setMessage(
                            "Вы действительно хотите удалить контакт «${contact.name}»?" + "\n" + "\n" +
                                    "Это операция также удалит всю историю, связанную с выбранным контактом; удаление нельзя будет отменить."
                                    + "\n" + "\n" + "С этим контактом связана сумма «${contact.summa}». Хотите все равно удалить?"
                        )
                        dialog.setPositiveButton("УДАЛИТЬ") { _, _ ->
                            deleteData(id)
                        }
                        dialog.setNegativeButton("ОТМЕНА") { _, _ ->
                        }
                        dialog.show()
                    }
                }
                return@setOnMenuItemClickListener true
            }
            optionsMenu.show()
        }
    }
    private fun deleteData(id: String){
        db.collection("contacts").document(mAuth.currentUser!!.uid).collection("history").document(id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Данные были удалены", Toast.LENGTH_SHORT).show()
            }
    }


}