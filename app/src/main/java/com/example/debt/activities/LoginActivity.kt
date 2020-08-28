package com.example.debt.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.debt.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*


class   LoginActivity : AppCompatActivity() {

    private val mAuth=FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        btnLogin.setOnClickListener {
            if(etLogin.text.isNullOrEmpty()&&etPassword.text.isNullOrEmpty()){
                loading.visibility=View.VISIBLE
                mAuth.signInWithEmailAndPassword(etLogin.text.toString(),etPassword.text.toString())
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            loading.visibility=View.GONE
                            val currentUser = mAuth.currentUser
                            updateUI(currentUser)
                        }else{
                            loading.visibility=View.GONE
                            Toast.makeText(this,it.exception?.localizedMessage, Toast.LENGTH_LONG).show()
                        }
                    }
            }else{
                Toast.makeText(this,"Заполнителе поля",Toast.LENGTH_LONG).show()
            }
        }

        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user!=null){
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }
}