package com.example.cortoescritura.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.cortoescritura.R
import com.example.cortoescritura.models.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : BaseActivity() {
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()

        setupActionBar()
    }

    private fun setupActionBar(){

        var toolBar : Toolbar= findViewById(R.id.toolbar_sign_in_activity)
        var signInButton : Button = findViewById(R.id.btn_sign_in)

        setSupportActionBar(toolBar)

        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_color_white_24dp)
        }

        toolBar.setNavigationOnClickListener{
            onBackPressed()
        }
        signInButton.setOnClickListener {
            if(signInRegisteredUser()){ finish() }
        }
    }

    private fun signInRegisteredUser():Boolean{
        val email    : String =et_email_sign_in.text.toString().trim{it<=' '}
        val password : String =et_password_sign_in.text.toString().trim{it<=' '}
        var flag = false

        if(validateForm(email,password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                            var firebaseUser = FirebaseAuth.getInstance().currentUser

                            if(firebaseUser!!.isEmailVerified){
                                startActivity(Intent(this, MainMenuActivity::class.java))
                            }
                            else{
                                firebaseUser!!.sendEmailVerification()
                                Toast.makeText(this,"Revisa tu correo para verificar tu cuenta.",Toast.LENGTH_LONG).show()
                            }

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Sign in", "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Autenticacion fallida.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
        return flag
    }

    private fun validateForm(email:String, password: String):Boolean{

        return when{
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Por favor ingresa un correo valido")
                et_email_sign_in.requestFocus()
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->{
                et_email_sign_in.requestFocus()
                showErrorSnackBar("Este no es un E-mail valido...por favor ingresa un correo electronico valido")
                false
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar("Por favor ingresa la contraseña de ${et_email_sign_in.text.toString()}")
                et_password_sign_in.requestFocus()
                false
            }
            else->{
                true
            }
        }
    }

    fun signInSuccess(loggedInUser: User) {
        hideProgressDialog()
        startActivity((Intent(this,MainActivity::class.java)))
        finish()
    }


}