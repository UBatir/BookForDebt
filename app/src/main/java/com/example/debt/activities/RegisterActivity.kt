package com.example.debt.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.debt.R
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()

        btnBack.setOnClickListener {
            finish()
        }
    }
}