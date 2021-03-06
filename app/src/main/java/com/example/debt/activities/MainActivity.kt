package com.example.debt.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.debt.R
import com.example.debt.adapters.ListAdapter
import com.example.debt.data.Contact
import com.example.debt.dialog.AddContactDialog
import com.example.debt.dialog.DialogChangeBalance
import com.example.debt.dialog.DialogRename
import com.example.debt.dialog.DialogSort
import com.example.debt.interfaces.ContactItemClickListener
import com.example.debt.interfaces.SortClickListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.dialog_data.*
import kotlinx.android.synthetic.main.item_contact.*


class MainActivity : AppCompatActivity(),
    ContactItemClickListener, SortClickListener {

    private val adapter= ListAdapter(this, this)
    private val db=FirebaseFirestore.getInstance()
    private val mAuth=FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Записи"
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_CONTACTS),
            123
        )
        ivSort.setOnClickListener {
            val dialog = DialogSort(this,this)
            dialog.show()
        }
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        recyclerView.adapter=adapter
        getData()
        timer.start()
        totalSum()
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val dialog=
                AddContactDialog(this, this)
            dialog.show()
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle=ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_addContact -> {
                    val dialog =
                        AddContactDialog(this, this)
                    dialog.show()
                }
                R.id.nav_history -> {
                    val intent = Intent(this, HistoryActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_aboutProgram -> {
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("О программе")
                    dialog.setMessage(
                        "Записная книжка призванная " +
                                "стать помощниом в учете долгов и займов"
                    ).show()
                }
                R.id.nav_exitFromAccount->{
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("Выход из аккаунта")
                    dialog.setMessage("Вы действительно хотите выйти из аккаунта?"
                    )
                    dialog.setPositiveButton("OK") { _, _->
                        mAuth.signOut()
                        val intent = Intent(this,LoginActivity::class.java)
                        intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    }
                    dialog.setNegativeButton("Отмена"){_,_->
                    }
                    dialog.show()
                }
                else -> return@setNavigationItemSelectedListener false
            }

            drawerLayout.closeDrawer(GravityCompat.START)
            return@setNavigationItemSelectedListener true
        }

    }

    private val timer = object : CountDownTimer(100 , 1000){

        override fun onTick(p0: Long) {

        }
        override fun onFinish() {
            totalSum()
        }
    }
    private fun getData(){
        val result: MutableList<Contact> = mutableListOf()
                db.collection("contacts").document(mAuth.currentUser!!.uid).collection("data")
                    .addSnapshotListener { _, error ->
                    if (error != null) {
                        Toast.makeText(
                            applicationContext,
                            error.message.toString(),
                              Toast.LENGTH_LONG
                        ).show()
                        return@addSnapshotListener
                    }
                    result.clear()
                  db.collection("contacts").document(mAuth.currentUser!!.uid).collection("data").get()
                      .addOnSuccessListener {
                          it.documents.forEach {doc ->
                              val model = doc.toObject(Contact::class.java)
                              model?.id = doc.id
                              model?.let {
                                  result.add(model)
                              }
                          }
                          adapter.models = result
                          timer.start()
                      }
                }
        }


    private fun deleteData(id: String){
        db.collection("contacts").document(mAuth.currentUser!!.uid).collection("data").document(id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Данные были удалены", Toast.LENGTH_SHORT).show()
            }
        timer.start()
    }


    @SuppressLint("SetTextI18n")
    private fun totalSum(){
        var sum=0
        for (i in adapter.models){
            sum+= i.summa.toInt()
        }
        when {
            sum>0 -> {
                tvTotalSum.setTextColor(Color.rgb(76, 175, 80))
                tvTotalSum.text="+$sum"
            }
            sum==0 -> {
                tvTotalSum.setTextColor(Color.rgb(209, 209, 209))
                tvTotalSum.text=sum.toString()
            }
            else -> {
                tvTotalSum.setTextColor(Color.rgb(229, 57, 53))
                tvTotalSum.text=sum.toString()
            }
        }
    }

    fun onOptionsButtonClick(view: View, contact: Contact, id: String){
        db.collection("contacts").document(mAuth.currentUser!!.uid).collection("data").document(id).get().addOnSuccessListener {doc->
            val optionsMenu=PopupMenu(this, view)
            val menuInflater=optionsMenu.menuInflater
            menuInflater.inflate(R.menu.menu_item_options, optionsMenu.menu)
            contact.summa=doc.get("summa").toString().toLong()
            contact.debt=doc.get("debt").toString().toInt()
            optionsMenu.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.itemAmount -> {
                        val value = contact.summa
                        contact.summa=0
                        contact.debt = -1
                        updateSum(contact, id)
                        val snackBar = Snackbar.make(
                            view,
                            "Вы погасили сумму «$value» сумму для контакта «${contact.name}»!",
                            Snackbar.LENGTH_LONG
                        )
                        snackBar.setAction("ОТМЕНА") {
                            if (value.toInt() > 0) {
                                contact.summa = value
                                contact.debt = 1
                                updateSum(contact, id)
                            } else {
                                contact.summa = value
                                contact.debt = 0
                                updateSum(contact, id)
                            }
                            snackBar.dismiss()
                        }
                        snackBar.setActionTextColor(Color.rgb(253, 216, 53))
                        snackBar.show()
                    }
                    R.id.itemChangeBalance -> {
                        val dialog =
                            DialogChangeBalance(this, id)
                        dialog.show()
                    }
                    R.id.itemCleanHistory -> {
                        val dialog = AlertDialog.Builder(this)
                        dialog.setTitle("Очистить историю")
                        dialog.setMessage(
                            "Вы действительно хотите очистить историю операций для контакта «${contact.name}»?" + "\n" + "\n" +
                                    "Это операция также удалит всю историю, связанную с выбранным контактом"
                        )
                        dialog.setPositiveButton("Очистить") { _, _ ->
                            cleanHistory(id)
                        }
                        dialog.setNegativeButton("ОТМЕНА") { _, _ ->
                        }
                        dialog.show()
                    }
                    R.id.itemRename -> {
                        totalSum()
                        val dialog =
                            DialogRename(id, this)
                        dialog.show()
                    }
                    R.id.itemHistory ->{
                        val intent = Intent(this, HistoryActivity::class.java)
                        intent.putExtra("key", doc.get("name").toString())
                        startActivity(intent)
                        finish()
                    }
                    R.id.itemDelete -> {
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
    private fun cleanHistory(id: String){
        val idsRef: CollectionReference = db.collection("contacts").document(mAuth.currentUser!!.uid).collection("history")
        val query : Query = idsRef.whereEqualTo("name", tvName.text.toString())
        query.get()
            .addOnSuccessListener {i->
                i.documents.forEach { et ->
                    et.reference.delete()
                }
            }
        val zeroSum = db.collection("contacts").document(mAuth.currentUser!!.uid).collection("data")
        val queryZero: Query = zeroSum.whereEqualTo("name", tvName.text.toString())
        queryZero.get()
            .addOnSuccessListener { e->
                val updates= hashMapOf<String,Any>(
                    "summa" to 0,
                    "debt" to -1
                )
                db.collection("contacts").document(mAuth.currentUser!!.uid).collection("data").document(id).update(updates)
            }
    }

    private fun updateSum(contact: Contact, id: String){
        val update= hashMapOf<String,Any>(
            "summa" to contact.summa,
            "debt" to contact.debt
        )
        db.collection("contacts").document(mAuth.currentUser!!.uid).collection("data").document(id).update(update)
    }

    override fun onContactItemClick(id: String) {
        val dialog=
            DialogChangeBalance(
                this,
                id
            )
        dialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==123){
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            }
        }
    }

    override fun onClickSort(key: String, direction: String) {
        val a = adapter.models
        if (key == "name" && direction == "DESCENDING"){
            a.sortByDescending {
                it.name
            }
        }else if (key == "summa" && direction == "DESCENDING"){
            a.sortByDescending {
                it.summa
            }
        }else if (key == "date" && direction == "DESCENDING"){
            a.sortByDescending {
                it.date
            }
        }else if (key == "name" && direction == "ASCENDING"){
            a.sortBy {
                it.name
            }
        }else if (key == "summa" && direction == "ASCENDING"){
            a.sortBy {
                it.summa
            }
        }else if (key == "date" && direction == "ASCENDING"){
            a.sortBy {
                it.date
            }
        }
        a.forEach { _ ->
            adapter.models = a
        }
    }
}