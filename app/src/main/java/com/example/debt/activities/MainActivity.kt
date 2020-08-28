package com.example.debt.activities

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.debt.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity(),
    ContactItemClickListener {

    private val adapter= ListAdapter(this, this)
    private val db=FirebaseFirestore.getInstance()
    private val mAuth=FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Daptershe"
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_CONTACTS),
            123
        )
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        recyclerView.adapter=adapter
        dao=NotebookDatabase.getInstance(this).dao()
        adapter.models= dao.getAllContact()
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
                R.id.nav_qosiw -> {
                    val dialog =
                        AddContactDialog(this, this)
                    dialog.show()
                }
                R.id.nav_tariyx -> {

                }
                R.id.nav_rezerv -> {

                }
                R.id.nav_sazlaw -> {

                }
                R.id.nav_dastur -> {

                }
                else -> return@setNavigationItemSelectedListener false
            }

            drawerLayout.closeDrawer(GravityCompat.START)
            return@setNavigationItemSelectedListener true
        }

        ivSort.setOnClickListener {
            val dialog=DialogSort(this, this)
            dialog.show()
        }
    }

    fun addContact(contact: Contact){
        dao.insertContact(contact)
        adapter.models= dao.getAllContact()
        totalSum()
    }

    private fun removeContact(contact: Contact){
        dao.deleteContact(contact)
        adapter.models=dao.getAllContact()
        totalSum()
    }

    fun rewriteContact(contact: Contact){
        dao.updateContact(contact)
        adapter.models=dao.getAllContact()
        totalSum()
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

    fun onOptionsButtonClick(view: View, contact: Contact, id: Int){
        val optionsMenu=PopupMenu(this, view)
        val menuInflater=optionsMenu.menuInflater
        menuInflater.inflate(R.menu.menu_item_options, optionsMenu.menu)
        optionsMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.itemAmount -> {
                    val value = contact.summa
                    contact.summa = 0
                    contact.debt = -1
                    val snackBar = Snackbar.make(
                        view,
                        "Siz «${contact.name}» kontakttin «$value» summasin oshirdiniz!",
                        Snackbar.LENGTH_LONG
                    )
                    snackBar.setAction("Biykarlaw") {
                        if (value > 0) {
                            contact.summa = value
                            contact.debt = 1
                        } else {
                            contact.summa = value
                            contact.debt = 0
                        }
                        rewriteContact(contact)
                        snackBar.dismiss()
                    }
                    snackBar.setActionTextColor(Color.rgb(253, 216, 53))
                    rewriteContact(contact)
                    snackBar.show()
                }
                R.id.itemChangeBalance -> {
                    val dialog =
                        DialogChangeBalance(
                            this,
                            id
                        )
                    dialog.show()
                }
                R.id.itemRename -> {
                    val dialog =
                        DialogRename(
                            id,
                            this
                        )
                    dialog.show()
                }
                R.id.itemDelete -> {
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("Oshiriw")
                    dialog.setMessage(
                        "Rasinda da «${contact.name}» kontaktti joq qilajaqsizba?" + "\n" + "\n" +
                                "Bul amel tanlag'an kontakt penen baylanisli barliq informaciyani oshirip taslaydi, buni biykarlap bolmaydi."
                                + "\n" + "\n" + "«${contact.summa}» mug'dari usi kontakt penen baylanisli. Eleda dawam etpekshisizba?"
                    )
                    dialog.setPositiveButton("Oshiriw") { _, _ ->
                        removeContact(contact)
                    }
                    dialog.setNegativeButton("Biykarlaw") { _, _ ->
                    }
                    dialog.show()
                }
            }
            return@setOnMenuItemClickListener true
        }
        optionsMenu.show()
    }

    override fun onContactItemClick(id: Int) {
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

}