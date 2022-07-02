package com.example.cortoescritura.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cortoescritura.R
import com.example.cortoescritura.models.Story
import android.graphics.Paint
import kotlinx.android.synthetic.main.item_story.view.*


open class OtherStoryItemAdapter(private val context: Context, private var list:ArrayList<Story>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener?=null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return myViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_story, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        holder.itemView.tv_name_story_o.text = model.name
        holder.itemView.tv_story_genre_o.text = model.genre
        holder.itemView.tv_story_creator.text = model.createdBy

        holder.itemView.tv_story_creator.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG)

        holder.itemView.story_rating_stars_o.rating = model.averageRating.toString().toFloat()
        holder.itemView.tv_amount_rating_o.text = model.ratings.size.toString()

        holder.itemView.setOnClickListener{
            if(onClickListener!=null){
                onClickListener!!.onClick(position, model)
            }
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

