package com.example.cortoescritura.activities

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.text.Html
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cortoescritura.Firestore.FirestoreClass
import com.example.cortoescritura.R
import com.example.cortoescritura.adapters.CommentAdapter
import com.example.cortoescritura.models.Comment
import com.example.cortoescritura.models.Rating
import com.example.cortoescritura.models.Report
import com.example.cortoescritura.models.Story
import com.example.cortoescritura.utils.Constants
import kotlinx.android.synthetic.main.activity_read_story.*
import kotlinx.android.synthetic.main.report_story_alert.view.*
import kotlinx.android.synthetic.main.show_comments_alert_screen.view.*
import kotlinx.android.synthetic.main.story_options_alert_dialog.view.*
import java.util.*
import kotlin.collections.ArrayList


class ReadStoryActivity : BaseActivity() {

    private lateinit var mStoryDocumentID : String
    private lateinit var story :Story
    private lateinit var commentImages : ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_story)



        mStoryDocumentID = intent.getStringExtra(Constants.STORY_ID).toString()
        FirestoreClass().getStoryDetails(this@ReadStoryActivity, mStoryDocumentID)

        tv_rate_story.setOnClickListener {
            showRatingAlert()
        }

        tv_comments_story.setOnClickListener {
            showCommentAlert()
        }

        tv_story_view_settings.setOnClickListener {
            showOptionAlert()
        }

        tv_report_story.setOnClickListener {
            showReportAlert()
        }

    }


    fun setStoryDataInUI(story: Story) {
        this.story = story
        tv_read_story_title.text = story.name
        tv_read_story_content.text = story.storyContent
        setUpActionBar()
    }

    private fun setUpActionBar(){
        setSupportActionBar(toolbar_reader_story_activity)

        val actionBar = supportActionBar

        toolbar_reader_story_activity.setNavigationIcon(R.drawable.ic_back_color_white_24dp)

        if(actionBar!=null){
            actionBar.setTitle(Html.fromHtml("<font color='#FFFFFFF'>Autor : </font><font color='#0492C2'>${story.createdBy}</font>"))
        }
        toolbar_reader_story_activity.setNavigationOnClickListener {
            onBackPressed()
        }

        commentImages = FirestoreClass().getListImages(story.comments)

    }

    private fun showRatingAlert(){

        var ll  = LinearLayout(this)
        var rb = RatingBar(this@ReadStoryActivity)

        var params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        rb.setNumStars(5)
        rb.setMax(5)
        rb.setIsIndicator(false)
        rb.setStepSize(0.5f)

        val drawable: LayerDrawable = rb.getProgressDrawable() as LayerDrawable
        drawable.getDrawable(0).setColorFilter(Color.parseColor("#DDDDDD"), PorterDuff.Mode.SRC_ATOP)
        drawable.getDrawable(1).setColorFilter(Color.parseColor("#333333"), PorterDuff.Mode.SRC_ATOP)
        drawable.getDrawable(2).setColorFilter(Color.parseColor("#FFC800"), PorterDuff.Mode.SRC_ATOP)

        ll.addView(rb)
        ll.setGravity(Gravity.CENTER)

        var alertDialog = AlertDialog.Builder(this)
            .setTitle("Calificar historia")
            .setMessage("De 0 a 5 estrellas, ¿Que calificacion le das?")
            .setView(ll)
            .setPositiveButton("Calificar"){
                    dialogInterface,which ->
                FirestoreClass().rateStory( this@ReadStoryActivity, story.storyId, Rating(FirestoreClass().getCurrentUserId(),rb.rating))
                Toast.makeText(this,"Gracias! Calificaste: ${rb.rating} estrellas.",Toast.LENGTH_LONG).show()
            }.setNegativeButton("Cancelar"){
                    dialogInterface,which ->
            }.setCancelable(true)
            .show()

        var neg_button = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        var pos_button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)

        var lp : LinearLayout.LayoutParams = pos_button.getLayoutParams() as LinearLayout.LayoutParams
        lp.weight = 10F
        pos_button.setLayoutParams(lp)
        neg_button.setLayoutParams(lp)

    }

    private fun showCommentAlert(){
        val alert = AlertDialog.Builder(this)
        var listComments = story.comments
        val view = LayoutInflater.from(this@ReadStoryActivity).inflate(R.layout.show_comments_alert_screen, null)


        alert.setView(view)
        alert.setTitle("Comentarios...")

        if(listComments.size >0){

            view.tv_no_comments.visibility = View.GONE
            view.lv_list_comments.visibility = View.VISIBLE

            view.lv_list_comments.layoutManager = LinearLayoutManager(this)
            view.lv_list_comments.setHasFixedSize(false)

            val adapter = CommentAdapter(this, listComments, commentImages)
            view.lv_list_comments.adapter = adapter

        }
        else{
            view.tv_no_comments.visibility = View.VISIBLE
            view.lv_list_comments.visibility = View.GONE
        }


        alert.setPositiveButton("Comentar") { dialog, id ->
            if(!view.et_comment_story_o.text.toString().equals("")) {
                var comment = Comment(
                    mStoryDocumentID,
                    view.et_comment_story_o.text.toString(),
                    FirestoreClass().getCurrentUserId(),
                    Date()
                )
                listComments.add(comment)

                var map = HashMap<String, Any>()
                map.put("comments", listComments)
                FirestoreClass().updateStoryData(
                    this@ReadStoryActivity,
                    mStoryDocumentID,
                    map
                )
            }
            else{
                Toast.makeText(this, "No has comentado nada, Intentalo de nuevo.",Toast.LENGTH_LONG).show()
                
            }
        }
        alert.setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialog, id ->
            dialog.cancel()
        })
        alert.show()

    }

    private fun showOptionAlert() {
        val alert = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this@ReadStoryActivity).inflate(R.layout.story_options_alert_dialog, null)

        var theme = "Normal"
        var size = 16.0f

        view.sp_theme_reading.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view2: View?, position: Int, id: Long) {
                if(position == 0){
                    view!!.tv_sample.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    view.tv_sample.setTextColor(Color.parseColor("#000000"))
                    theme= "Normal"
                }
                else{
                    view!!.tv_sample.setBackgroundColor(Color.parseColor("#000000"))
                    view.tv_sample.setTextColor(Color.parseColor("#FFFFFF"))
                    theme="Dark"
                }
            }

        }

        view.sp_size_text_reading.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view2: View?, position: Int, id: Long) {
                view!!.tv_sample.textSize = 16.0f + (2*position)
                size=16.0f + (2*position)
            }

        }

        alert.setView(view)

        alert.setPositiveButton("Confirmar") { dialog, id ->
            tv_read_story_content.textSize = size
            if(theme=="Normal"){
                tv_read_story_content.setBackgroundColor(Color.parseColor("#FFFFFF"))
                tv_read_story_content.setTextColor(Color.parseColor("#000000"))
            }
            else{
                tv_read_story_content.setBackgroundColor(Color.parseColor("#000000"))
                tv_read_story_content.setTextColor(Color.parseColor("#FFFFFF"))
            }
        }
        alert.setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialog, id ->
            dialog.cancel()
        })
        alert.show()
    }

    private fun showReportAlert(){

        val alert = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this@ReadStoryActivity).inflate(R.layout.report_story_alert, null)

        alert.setView(view)

        alert.setPositiveButton("Reportar") { dialog, id ->
            if(view.sp_reason_report.selectedItem.toString().equals("Selecciona una:")){

                Toast.makeText(this, "No has seleccionado una razon para reportar esta historia.",Toast.LENGTH_LONG).show()
            }
            else{
                var report = Report(FirestoreClass().getCurrentUserId(), mStoryDocumentID,view.sp_reason_report.selectedItem.toString(), view.et_report_details.text.toString())
                FirestoreClass().createReport(this, report)
                Toast.makeText(this, "Muchas gracias por tu retroalimentación, reporte enviado.",Toast.LENGTH_LONG).show()
            }
        }
        alert.setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialog, id ->
            dialog.cancel()
        })

        alert.show()

    }


    //dz$Yp.tp.pY+iXAF6_@MR*8Ka(vyM!
}