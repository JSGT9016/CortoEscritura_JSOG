package com.example.cortoescritura.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cortoescritura.Firestore.FirestoreClass
import com.example.cortoescritura.R
import com.example.cortoescritura.adapters.MyStoryItemsAdapter
import com.example.cortoescritura.models.Story
import com.example.cortoescritura.models.User
import com.example.cortoescritura.utils.Constants
import kotlinx.android.synthetic.main.activity_my_stories_profile.*
import kotlinx.android.synthetic.main.main_content.*

class MyStoriesProfileActivity : BaseActivity() {

    private lateinit var mUserName: String
    private lateinit var mSharedPreferences  : SharedPreferences

    companion object{
        const val MY_PROFILE_REQUEST_CODE : Int = 11
        const val CREATE_STORY_REQUEST_CODE: Int  = 12
        const val UPDATE_STORY_REQUEST_CODE: Int  = 13
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_stories_profile)

        mSharedPreferences = this.getSharedPreferences(Constants.MYAPP_PREFERENCES, Context.MODE_PRIVATE)

        setUpActionBar()

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getStoriesList(this@MyStoriesProfileActivity)

        create_story_btn.setOnClickListener {
            startActivity(Intent(this, CreateStoryActivity::class.java))
        }

    }

    fun updateNavigationUserDetails(user: User, readStoriesList:Boolean) {
        hideProgressDialog()

        if(readStoriesList){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getStoriesList(this)
        }
    }

    private fun setUpActionBar(){
        setSupportActionBar(toolbar_my_stories_profile_activity)
        val actionBar = supportActionBar

        if(actionBar!=null){
            actionBar.setTitle(Html.fromHtml("<font color='#FFFFFFF'>${getString(R.string.my_stories_profile_title)}</font>"))
        }

        toolbar_my_stories_profile_activity.setNavigationIcon(R.drawable.ic_back_color_white_24dp)

        toolbar_my_stories_profile_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE) {
            FirestoreClass().loadUserData(this)
        } else if(resultCode== Activity.RESULT_OK && requestCode == CREATE_STORY_REQUEST_CODE
            || resultCode== Activity.RESULT_OK && requestCode == UPDATE_STORY_REQUEST_CODE){
            FirestoreClass().getStoriesList(this)
        }
        else{
            Log.e("Cancelled", "Cancelado...")
        }
    }

    fun populateStoryListToUI(storyList:ArrayList<Story>){
        hideProgressDialog()
        if(storyList.size >0){
            rv_story_list.visibility = View.VISIBLE
            tv_no_stories_available.visibility = View.GONE

            rv_story_list.layoutManager = LinearLayoutManager(this)
            rv_story_list.setHasFixedSize(false)

            val adapter = MyStoryItemsAdapter(this, storyList)
            rv_story_list.adapter = adapter

            /*
            adapter.setOnClickListener(object:  StoryItemsAdapter.OnClickListener{
                override fun onClick(position: Int, model: Story) {

                }
            })*/
        }
        else{
            rv_story_list.visibility = View.GONE
            tv_no_stories_available.visibility = View.VISIBLE
        }
    }

    private fun updateFCMToken(token:String){
        val userHashMap = HashMap<String,Any>()
        userHashMap[Constants.FCM_TOKEN] = token
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().updateUserProfileData(this,userHashMap)
    }


}