package com.example.cortoescritura.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cortoescritura.R
import com.example.cortoescritura.adapters.GenreAdapter
import com.example.cortoescritura.internal_data.GenresStored
import com.example.cortoescritura.models.Genre
import kotlinx.android.synthetic.main.activity_discover_stories.*


class DiscoverStoriesActivity : AppCompatActivity() {

    lateinit var recyclerView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discover_stories)

        setUpActionBar()

        recyclerView = findViewById(R.id.viewGenresRecycler)

        var adapter = GenreAdapter(this, GenresStored.returnGenres())
        recyclerView.adapter = adapter

        var layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        cl_by_genre.setOnClickListener{
            viewGoneAnimator(ll_first_filter)
            viewVisibleAnimator(ll_second_filter)
        }

        cl_by_date.setOnClickListener {
            var intent = Intent(this@DiscoverStoriesActivity, ShowOthersStoriesActivity::class.java)
            intent.putExtra("SearchBy", "Recent")
            startActivity(intent)
        }

        cl_by_rating.setOnClickListener {
            var intent = Intent(this@DiscoverStoriesActivity, ShowOthersStoriesActivity::class.java)
            intent.putExtra("SearchBy", "Score")
            startActivity(intent)
        }

        cl_by_user.setOnClickListener {

            var input = EditText(this)

            AlertDialog.Builder(this)
                .setTitle("Buscar usuario")
                .setMessage("Ingresa nombre usuario")
                .setView(input)
                .setPositiveButton("Ingresar"){
                    dialogInterface,which ->
                    var intent = Intent(this@DiscoverStoriesActivity, ShowOthersStoriesActivity::class.java)
                    intent.putExtra("User", input.text.toString())
                    startActivity(intent)
                }.setNegativeButton("Cancelar"){
                        dialogInterface,which ->
                }.setCancelable(true)
                .show()

        }

        cl_random.setOnClickListener {
            var intent = Intent(this@DiscoverStoriesActivity, ShowOthersStoriesActivity::class.java)
            intent.putExtra("Random", "random")
            startActivity(intent)
        }


        adapter.setOnClickListener(object:  GenreAdapter.OnClickListener {
            override fun onClick(position: Int, model: Genre) {
                var intent = Intent(this@DiscoverStoriesActivity, ShowOthersStoriesActivity::class.java)
                intent.putExtra("Genre", model.name)
                startActivity(intent)
            }
        })

    }

    private fun setUpActionBar(){
        setSupportActionBar(toolbar_discover_activity)
        val actionBar = supportActionBar
        toolbar_discover_activity.setNavigationIcon(R.drawable.ic_back_color_white_24dp)

        if(actionBar!=null){
            actionBar.setTitle(Html.fromHtml("<font color='#FFFFFFF'>${getString(R.string.discover_toolbar_title)}</font>"))
        }

        toolbar_discover_activity.setNavigationOnClickListener{
            if(ll_first_filter.visibility == View.GONE){
                viewVisibleAnimator(ll_first_filter)
                viewGoneAnimator(ll_second_filter)
            }else {
                onBackPressed()
            }
        }
    }

    private fun viewGoneAnimator(view: View) {
        view.animate()
            .alpha(0f)
            .setDuration(500)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    view.visibility = View.GONE
                }
            })
    }

    private fun viewVisibleAnimator(view: View) {
        view.animate()
            .alpha(1f)
            .setDuration(500)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.VISIBLE
                }
            })
    }

}