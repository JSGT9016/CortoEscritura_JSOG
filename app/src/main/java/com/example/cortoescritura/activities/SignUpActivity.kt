package com.example.cortoescritura.activities

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.cortoescritura.Firestore.FirestoreClass
import com.example.cortoescritura.R
import com.example.cortoescritura.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        setupActionBar()
    }

    fun userRegisteredSuccess(){
        Toast.makeText(
            this,
            "Te has registrado correctamente!",
            Toast.LENGTH_LONG
        ).show()
        hideProgressDialog()

        FirebaseAuth.getInstance().signOut()
        finish()
    }

    private fun setupActionBar(){

        var toolBar : Toolbar = findViewById(R.id.toolbar_sign_up_activity)
        var btnSignUp : Button = findViewById(R.id.btn_sign_up)

        setSupportActionBar(toolBar)

        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_color_white_24dp)
        }

        toolBar.setNavigationOnClickListener{
            onBackPressed()
        }
        btnSignUp.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val name: String = et_name.text.toString().trim { it <= ' ' }
        val email: String = et_email.text.toString().trim { it <= ' ' }
        val password: String = et_password.text.toString().trim { it <= ' ' }
        val confirmPassword: String = et_confirm_password.text.toString().trim { it <= ' ' }

        if (validateForm(name, email, password, confirmPassword)) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val registeredEmail = firebaseUser.email!!
                        Log.d("emailRegistered", registeredEmail)
                        val user = User(firebaseUser.uid,name,registeredEmail)
                        Log.d("UserRegistered", user.email)

                        FirestoreClass().registerUser(this, user)
                    } else {
                        Toast.makeText(this, "No fue posible el registro...intenta nuevamente.", Toast.LENGTH_LONG).show()
                        hideProgressDialog()
                    }
                }
        }
    }

    private fun validateForm(name:String, email:String, password: String, confirmPassword:String):Boolean{
        return when{
            TextUtils.isEmpty(name)->{
                et_name.requestFocus()
                showErrorSnackBar("Por favor ingresa un nombre")
                false
            }
            TextUtils.isEmpty(email)->{
                et_email.requestFocus()
                showErrorSnackBar("Por favor ingresa un correo electronico")
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->{
                et_email.requestFocus()
                showErrorSnackBar("Este no es un E-mail valido...por favor ingresa un correo electronico valido")
                false
            }
            TextUtils.isEmpty(password)->{
                et_password.requestFocus()
                showErrorSnackBar("Por favor ingresa una contraseña")
                false
            }
            password.length < 8 -> {
                et_password.requestFocus()
                showErrorSnackBar("Por tu seguridad...La contraseña debe tener al menos 8 digitos.")
               false
            }
            password.filter { it.isDigit() }.firstOrNull() == null ->{
                et_password.requestFocus()
                showErrorSnackBar("Por tu seguridad...Ingresa por lo menos un numero en la contraseña, por favor.")
                false
            }
            password.filter { it.isLetter() }.filter { it.isUpperCase() }.firstOrNull() == null -> {
                et_password.requestFocus()
                showErrorSnackBar("Por tu seguridad...Ingresa por lo menos una letra mayuscula y una minuscula en la contraseña, por favor.")
               false
            }
            password.filter { it.isLetter() }.filter { it.isLowerCase() }.firstOrNull() == null ->{
                et_password.requestFocus()
                showErrorSnackBar("Por tu seguridad...Ingresa por lo menos una letra mayuscula y una minuscula en la contraseña, por favor.")
               false
            }
            password.filter { !it.isLetterOrDigit() }.firstOrNull() == null -> {
                et_password.requestFocus()
                showErrorSnackBar("Por tu seguridad, la contrasela debe tener al menos un caracter especial...")
                false
            }
            TextUtils.isEmpty(confirmPassword)->{
                et_password.requestFocus()
                showErrorSnackBar("Por favor ingresa la confirmacion de la contraseña.")
                false
            }
            !confirmPassword.equals(password)->{
                et_password.requestFocus()
                showErrorSnackBar("Las contraseñas no coinciden")
                false
            }
            else->{
                true
            }
        }
    }
}