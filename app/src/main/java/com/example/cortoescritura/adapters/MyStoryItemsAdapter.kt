package com.example.cortoescritura.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.RecyclerView
import com.example.cortoescritura.R
import com.example.cortoescritura.activities.CreateStoryActivity
import com.example.cortoescritura.activities.MyStoriesProfileActivity.Companion.UPDATE_STORY_REQUEST_CODE
import com.example.cortoescritura.models.Story
import com.example.cortoescritura.utils.Constants
import kotlinx.android.synthetic.main.item_my_story.view.*
import android.content.DialogInterface
import android.app.AlertDialog
import com.example.cortoescritura.Firestore.FirestoreClass


open class MyStoryItemsAdapter(private val context: Context, private var list:ArrayList<Story>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener?=null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return myViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_my_story, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        holder.itemView.tv_name_story.text = model.name
        holder.itemView.tv_story_genre.text = model.genre

        holder.itemView.tv_story_comments.text = model.comments.size.toString()

        holder.itemView.story_rating_stars.rating = model.averageRating.toString().toFloat()
        holder.itemView.tv_amount_rating.text = model.ratings.size.toString()


        holder.itemView.tv_edit_story.setOnClickListener {
            var intent = Intent(
                context,
                CreateStoryActivity::class.java
            )
            intent.putExtra(Constants.STORY_ID, model.storyId)
            startActivityForResult(context as Activity, intent, UPDATE_STORY_REQUEST_CODE, null)
        }

        holder.itemView.tv_delete_story.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)

            builder.setTitle("Confirmación eliminar ${model.name}")
            builder.setMessage("¿Estas seguro de eliminarla?")

            builder.setPositiveButton(
                "SI",
                DialogInterface.OnClickListener { dialog, which ->
                    FirestoreClass().deleteStory(context as Activity, list[position].storyId)
                    dialog.dismiss()
                    holder.itemView.visibility = View.GONE

                })

            builder.setNegativeButton(
                "NO",
                DialogInterface.OnClickListener { dialog, which -> // Do nothing
                    dialog.dismiss()
                })

            val alert: AlertDialog = builder.create()
            alert.show()
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface  OnClickListener{
        fun onClick(position:Int, model:Story)
    }

    fun setOnClickListener(onClickListener:OnClickListener) {
        this.onClickListener= onClickListener
    }

    private class myViewHolder(view: View):RecyclerView.ViewHolder(view){
    }

}

