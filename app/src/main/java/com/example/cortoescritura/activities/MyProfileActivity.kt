package com.example.cortoescritura.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.cortoescritura.Firestore.FirestoreClass
import com.example.cortoescritura.R
import com.example.cortoescritura.models.User
import com.example.cortoescritura.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_my_profile.*
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    private val mFireStore = FirebaseFirestore.getInstance()

    private lateinit var activity : String

    private var mSelectedImageFileUri : Uri? = null
    private lateinit var mUserDetails : User
    private var mProfileImageURL : String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        activity = intent.getStringExtra("activity")!!

        FirestoreClass().loadUserData(this)

        iv_profile_user_image.setOnClickListener {

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }
            else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        btn_update.setOnClickListener {
            if(mSelectedImageFileUri!=null){
                uploadUserImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileData()
            }
        }

        setUpActionBar()
    }

    private fun setUpActionBar(){
        setSupportActionBar(toolbar_my_profile_activity)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_color_white_24dp)
            actionBar.setTitle(Html.fromHtml("<font color='#FFFFFFF'>${getString(R.string.my_profile_title)}</font>"))
        }

        toolbar_my_profile_activity.setNavigationOnClickListener{
            onBackPressed()
        }
    }

    private fun updateUserProfileData(){
        var userHashMap = HashMap<String, Any>()
        var anyChangesMade = false

        if(mProfileImageURL.isNotEmpty() && mProfileImageURL!= mUserDetails.image){
            userHashMap[Constants.IMAGE] = mProfileImageURL
            anyChangesMade=true
        }

        if(et_name.text.toString()!= mUserDetails.name){
            userHashMap[Constants.NAME] = et_name.text.toString()
            anyChangesMade=true
        }

        if(et_mobile.text.toString()!= mUserDetails.mobile.toString() || et_mobile.text!!.isNotEmpty()){
            userHashMap[Constants.MOBILE] = et_mobile.text.toString().toLong()
            anyChangesMade=true
        }

        if(anyChangesMade) {
            FirestoreClass().updateUserProfileData(this, userHashMap)
        }
    }

    private fun uploadUserImage(){
        showProgressDialog(resources.getString((R.string.please_wait)))

        if(mSelectedImageFileUri!=null){
            val sRef : StorageReference = FirebaseStorage.getInstance().
            reference.child("USER_IMAGE" + System.currentTimeMillis()
                    + "." + Constants.getFileExtension(this,mSelectedImageFileUri))
            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                    taskSnapshot ->
                Log.i("Firebase Image URL",  taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri ->
                    Log.e("Downloadable image URL", uri.toString())
                    mProfileImageURL = uri.toString()

                    updateUserProfileData()
                }
            }.addOnFailureListener {
                    exception ->
                Toast.makeText(this,
                    exception.message,
                    Toast.LENGTH_LONG).show()

                hideProgressDialog()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK
            && requestCode == Constants.PICK_IMAGE_REQUEST_CODE
            && data!!.data != null){
            mSelectedImageFileUri = data.data

            try {
                Glide
                    .with(this@MyProfileActivity)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(iv_profile_user_image)
            }
            catch (e : IOException){
                e.printStackTrace()
            }

        }
    }

    fun setUserDataInUI(user:User) {

        mUserDetails = user

        Glide
            .with(this@MyProfileActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(iv_profile_user_image)

        et_name.setText(user.name)
        et_email_profile.setText(FirebaseAuth.getInstance().getCurrentUser()!!.email)
        if(user.mobile!=0L){
            et_mobile.setText(user.mobile.toString())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== Constants.READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }
        }else{
            Toast.makeText(
                this,
                "No hay permisos para acceder a las fotos. Eso se debe hacer desde la configuración del celular.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun profileUpdateSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)

        if(activity.equals("MainActivity")){
            startActivity(Intent(this, MainActivity::class.java))
        }
        else{
            startActivity(Intent(this, MainMenuActivity::class.java))
        }

        Toast.makeText(this, "Perfil editado exitosamente!", Toast.LENGTH_LONG).show()

    }
}