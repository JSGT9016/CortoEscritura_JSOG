package com.example.cortoescritura.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.view.size
import com.bumptech.glide.Glide
import com.example.cortoescritura.Firestore.FirestoreClass
import com.example.cortoescritura.R
import com.example.cortoescritura.models.Rating
import com.example.cortoescritura.models.Story
import com.example.cortoescritura.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_create_story.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CreateStoryActivity : BaseActivity() {

    private lateinit var mStoryDocumentID: String
    private lateinit var mStoryInfo : Story
    private var flag :Boolean =false

    private lateinit var mStoryname : String
    private lateinit var mStoryContent : String
    private lateinit var mStoryGenre : String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_story)
        generateSpinnerValues()

        if(intent.hasExtra(Constants.STORY_ID)){
            mStoryDocumentID = intent.getStringExtra(Constants.STORY_ID).toString()
            FirestoreClass().getStoryDetails(this, mStoryDocumentID)
            flag = true
        }else {
            setUpActionBar()
        }

        btn_create_story.setOnClickListener {
            validateStory()
        }
    }

    private fun validateStory() {
        if(et_name_story.text.toString().trim().equals("")){
            et_name_story.requestFocus()
            showErrorSnackBar("El nombre de la historia esta vacio, por favor verificalo.")
            return
        }
        if(sp_story_genre.selectedItemPosition==0){
            sp_story_genre.requestFocus()
            showErrorSnackBar("Selecciona un genero para tu historia")
            return
        }
        if(et_story_content.text.toString().length <1000){
            et_story_content.requestFocus()
            showErrorSnackBar("La historia es demaciado corta, minimo 1000 caracteres = ${et_story_content.text.toString().length}/1000")
            return
        }
        showProgressDialog(getString(R.string.please_wait))
        if(flag){updateStory()}
        else(createStory())

    }

    private fun createStory() {

        val story = Story(
            et_name_story.text.toString(),
            FirebaseAuth.getInstance().uid.toString(),
            sp_story_genre.getSelectedItem().toString(),
            ArrayList(),
            ArrayList(),
            0.0,
            et_story_content.text.toString()
        )

        FirestoreClass().createStory(this, story)
    }

    private fun updateStory() {
        var changesMade : Boolean = false
        var storyMap : HashMap<String, Any> = HashMap()

        if(!mStoryname.equals(et_name_story.text.toString())){
            storyMap.put("name", et_name_story.text.toString())
            changesMade = true
        }
        if(!mStoryContent.equals(et_story_content.text.toString())){
            storyMap.put("storyContent", et_story_content.text.toString())
            changesMade = true
        }
        if(!mStoryGenre.equals(sp_story_genre.selectedItem.toString())){
            storyMap.put("genre", sp_story_genre.selectedItem.toString())
            changesMade = true
        }

        if(changesMade){
            FirestoreClass().updateStoryData(this@CreateStoryActivity, mStoryDocumentID, storyMap)
        }

    }

    fun storyUpdateSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
        ///TODO Update activity for updated story
    }


    fun storyCreatedSuccessfully(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
        //TODO Update activity for new story
    }

    private fun setUpActionBar(){
        setSupportActionBar(toolbar_create_story_activity)

        val actionBar = supportActionBar

        toolbar_create_story_activity.setNavigationIcon(R.drawable.ic_back_color_white_24dp)

        if(actionBar!=null){
            actionBar.setTitle(Html.fromHtml("<font color='#FFFFFFF'>${getString(R.string.new_story)}</font>"))
        }
        toolbar_create_story_activity.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    private fun setUpActionBar(string:String){
        setSupportActionBar(toolbar_create_story_activity)

        val actionBar = supportActionBar

        toolbar_create_story_activity.setNavigationIcon(R.drawable.ic_back_color_white_24dp)

        if(actionBar!=null){
            actionBar.setTitle(Html.fromHtml("<font color='#FFFFFFF'>${getString(R.string.edit_story)} ${mStoryInfo.name}</font>"))
        }
        btn_create_story.setText("Editar Historia")

        toolbar_create_story_activity.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    private fun generateSpinnerValues(){

        ArrayAdapter.createFromResource(
            this,
            R.array.story_genres,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sp_story_genre.adapter = adapter
        }
    }

    fun setStoryDataInUI(story:Story) {

        mStoryInfo = story

        et_name_story.setText(story.name)
        var indexSelected = getSelectedItemSpinner( ArrayList(Arrays.asList(*resources.getStringArray(R.array.story_genres))), story.genre)
        if(indexSelected!=-1) {
            sp_story_genre.setSelection(indexSelected)
        }
        et_story_content.setText(story.storyContent)

        mStoryname = story.name
        mStoryContent = story.storyContent
        mStoryGenre = story.genre

        setUpActionBar("edit")


    }

    fun getSelectedItemSpinner(list: ArrayList<String>, string:String):Int{
        var a = 0
        for(i in list){
            if(i.equals(string)){
                return a
            }
            a++
        }
        return -1
    }

}