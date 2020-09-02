package com.example.debt.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import com.example.debt.R
import com.example.debt.adapters.HistoryAdapter
import com.example.debt.data.Contact
import com.example.debt.dialog.DialogSort
import com.example.debt.interfaces.SortClickListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.app_bar_history.*
import kotlinx.android.synthetic.main.content_history.*


class HistoryActivity : AppCompatActivity(),AdapterView.OnItemSelectedListener, SortClickListener {

    private val mAdapter = HistoryAdapter(this)
    private val db= FirebaseFirestore.getInstance()
    private val mAuth= FirebaseAuth.getInstance()
    val resultName: MutableList<String> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        val toolbar: Toolbar = findViewById(R.id.toolbarHistory)
        setSupportActionBar(toolbar)
        title = "История"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        recyclerViewHistory.adapter=mAdapter
        val a = intent.getStringExtra("key")
            spinnerData()

        if (!a.isNullOrEmpty()){
            getDataToHistory()
        }else{
        getAllHistory()
        }
        ivSort.setOnClickListener {
            val dialog = DialogSort(this,this)
            dialog.show()
        }
    }

    private fun spinnerData(){
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this@HistoryActivity,
            android.R.layout.simple_spinner_item, resultName
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        db.collection("contacts").document(mAuth.currentUser!!.uid).collection("data")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(
                        applicationContext,
                        error.message.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                    return@addSnapshotListener
                }
                resultName.clear()
                db.collection("contacts").document(mAuth.currentUser!!.uid).collection("data").get()
                    .addOnSuccessListener {
                        it.documents.forEach {doc ->
                            val model = doc.toObject(Contact::class.java)
                            val name = model?.name
                            model?.id = doc.id
                            name.let {
                                resultName.add(name!!)
                            }
                        }
                        spinner.adapter = adapter
                        spinner.onItemSelectedListener = this
                    }
            }
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

    override fun onClickSort(key: String, direction: Query.Direction) {
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
                val query: Query = idsRef.orderBy(key,direction)
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

    fun getAllHistory() {
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
                 db.collection("contacts").document(mAuth.currentUser!!.uid).collection("history").get()
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

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
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
                       val a = parent!!.getItemAtPosition(position).toString()
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

}