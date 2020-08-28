package com.example.debt.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.debt.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : AppCompatActivity() {

    private val mAuth=FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()
        btnRegisterFinish.setOnClickListener {
            if(etLogin.text.isNullOrEmpty()&&etPassword.text.isNullOrEmpty()){
                loading.visibility= View.VISIBLE
                mAuth.createUserWithEmailAndPassword(
                    etLogin.text.toString(),
                    etPassword.text.toString()
                )
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            loading.visibility=View.GONE
                            val user = mAuth.currentUser
                            updateUI(user)
                        }else{
                            loading.visibility=View.GONE
                            Toast.makeText(this,"Ошибка аутентификации",Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }


        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user!=null){
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }
}