package com.example.cortoescritura.activities

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cortoescritura.Firestore.FirestoreClass
import com.example.cortoescritura.R
import com.example.cortoescritura.adapters.OtherStoryItemAdapter
import com.example.cortoescritura.models.Story
import com.example.cortoescritura.utils.Constants
import kotlinx.android.synthetic.main.activity_show_others_stories.*
import kotlinx.android.synthetic.main.other_stories_content.*

class ShowOthersStoriesActivity : BaseActivity() {

    private lateinit var key :String
    private lateinit var searchBy :String
    private lateinit var listStories : ArrayList<Story>
    private lateinit var recyclerView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_others_stories)

        if(intent.hasExtra("Genre")){
            searchBy = "Genre"
            key = intent.getStringExtra("Genre").toString()
        }
        else if(intent.hasExtra("SearchBy")){
            searchBy = "searchBy"
            key = intent.getStringExtra("SearchBy").toString()
        }
        else if(intent.hasExtra("User")){
            searchBy = "User"
            key = intent.getStringExtra("User").toString()
        }
        else{
            searchBy="Random"
            key=""
        }

        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().getStoryQueryList(this, searchBy,key)


        setUpActionBar()


    }

    private fun setUpActionBar(){
        setSupportActionBar(toolbar_show_other_stories_activity)
        val actionBar = supportActionBar
        toolbar_show_other_stories_activity.setNavigationIcon(R.drawable.ic_back_color_white_24dp)

        if(actionBar!=null){
            if(intent.hasExtra("Genre")) {
                actionBar.setTitle(Html.fromHtml("<font color='#FFFFFFF'>Historias de ${key}</font>"))
            }else if (intent.hasExtra("User")){
                actionBar.setTitle(Html.fromHtml("<font color='#FFFFFFF'>Historias de </font> <font color='#46B4FC'>${key}</font>"))
            }
            else if (intent.hasExtra("SearchBy")){
                actionBar.setTitle(Html.fromHtml("<font color='#FFFFFFF'>Historias por ${key}</font>"))
            }else{
                actionBar.setTitle(Html.fromHtml("<font color='#FFFFFFF'>Historias aleatorias</font>"))
            }
        }

        toolbar_show_other_stories_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun setStories(list : ArrayList<Story>){
        hideProgressDialog()
        if(list.size >0){
            rv_others_story_list.visibility = View.VISIBLE
            tv_no_others_stories_available.visibility = View.GONE

            rv_others_story_list.layoutManager = LinearLayoutManager(this)
            rv_others_story_list.setHasFixedSize(false)

            val adapter = OtherStoryItemAdapter(this, list)
            rv_others_story_list.adapter = adapter


            adapter.setOnClickListener(object:  OtherStoryItemAdapter.OnClickListener{
                override fun onClick(position: Int, model: Story) {
                    var intent = Intent(this@ShowOthersStoriesActivity, ReadStoryActivity::class.java)
                    intent.putExtra(Constants.STORY_ID,model.storyId)
                    startActivity(intent)

                }
            })
        }
        else{
            rv_others_story_list.visibility = View.GONE
            tv_no_others_stories_available.visibility = View.VISIBLE
        }
    }

}