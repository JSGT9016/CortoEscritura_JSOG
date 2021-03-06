package com.example.cortoescritura.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.example.cortoescritura.R
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        button_sign_up.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        button_sign_in.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

    }
}