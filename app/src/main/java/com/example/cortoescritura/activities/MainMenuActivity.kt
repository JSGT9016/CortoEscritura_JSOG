package com.example.cortoescritura.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.example.cortoescritura.Firestore.FirestoreClass
import com.example.cortoescritura.R
import com.example.cortoescritura.models.User
import com.example.cortoescritura.utils.Constants
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlinx.android.synthetic.main.activity_main_menu.nav_view_menu
import kotlinx.android.synthetic.main.app_bar_main_menu.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainMenuActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {



    private lateinit var mUserName: String
    private lateinit var mSharedPreferences  : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        setUpActionBar()

        nav_view_menu.setNavigationItemSelectedListener(this)

        mSharedPreferences = this.getSharedPreferences(Constants.MYAPP_PREFERENCES, Context.MODE_PRIVATE)

        val tokenUpdated = mSharedPreferences.getBoolean(Constants.FCM_TOKEN_UPDATED, false)

        if(tokenUpdated){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().loadUserData(this, true)
        }else{
            FirebaseMessaging.getInstance().token.addOnSuccessListener(this@MainMenuActivity){ result ->
                if(result != null){
                    updateFCMToken(result)
                }
            }
        }

        FirestoreClass().loadUserData(this, true)

        creator.setOnClickListener {
            startActivity(Intent(this, CreateStoryActivity::class.java))
        }

        reader.setOnClickListener {
            startActivity(Intent(this, DiscoverStoriesActivity::class.java))
        }

    }

    private fun setUpActionBar(){
        setSupportActionBar(toolbar_main_menu_activity)
        val actionBar = supportActionBar
        toolbar_main_menu_activity.setNavigationIcon(R.drawable.ic_action_navigation_menu_white)

        if(actionBar!=null){
            actionBar.setTitle(Html.fromHtml("<font color='#FFFFFFF'>${getString(R.string.main_menu)}</font>"))
        }

        toolbar_main_menu_activity.setNavigationOnClickListener{
            toggleDrawer()
        }
    }

    fun updateNavigationUserDetails(user: User) {
        hideProgressDialog()
        mUserName = user.name
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(nav_user_image)

        tv_username.text = user.name
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_stories ->{
                startActivity(Intent(this, MyStoriesProfileActivity::class.java))
            }
            R.id.nav_edit_my_profile->{
                var intent = Intent(this, MyProfileActivity::class.java)
                intent.putExtra("activity", "MainMenuActivity")
                startActivity(intent)
            }
            R.id.nav_sign_out ->{
                FirebaseAuth.getInstance().signOut()

                mSharedPreferences.edit().clear().apply()

                val intent = Intent(this,IntroActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        main_drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun toggleDrawer(){
        if(main_drawer_layout.isDrawerOpen(GravityCompat.START)){
            main_drawer_layout.closeDrawer(GravityCompat.START)
        }
        else{
            main_drawer_layout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if(main_drawer_layout.isDrawerOpen(GravityCompat.START)){
            main_drawer_layout.closeDrawer(GravityCompat.START)
        }
        else{
            doubleBackToExit()
        }
    }

    private fun updateFCMToken(token:String){
        val userHashMap = HashMap<String,Any>()
        userHashMap[Constants.FCM_TOKEN] = token
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().updateUserProfileData(this,userHashMap)
    }

    fun tokenUpdateSuccess() {
        hideProgressDialog()
        val editor : SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putBoolean(Constants.FCM_TOKEN_UPDATED, true)
        editor.apply()
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().loadUserData(this, true)
    }
}