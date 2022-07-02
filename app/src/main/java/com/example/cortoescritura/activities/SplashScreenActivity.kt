package com.example.cortoescritura.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.AnimationUtils
import com.example.cortoescritura.Firestore.FirestoreClass
import com.example.cortoescritura.R
import pl.droidsonroids.gif.GifTextView

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val backgroundImage: GifTextView = findViewById(R.id.splashScreenView)
        val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.side_slide)
        backgroundImage.startAnimation(slideAnimation)

        Handler().postDelayed({
            var currenUserID = FirestoreClass().getCurrentUserId()
            if(currenUserID!=""){
                startActivity(Intent(this, MainMenuActivity::class.java))
            }
            else {
                startActivity(Intent(this, IntroActivity::class.java))
            }
            finish()
        },5000)
    }


 }
