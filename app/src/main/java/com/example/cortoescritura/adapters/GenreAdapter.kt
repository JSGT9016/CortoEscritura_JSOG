package com.example.cortoescritura.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cortoescritura.R
import com.example.cortoescritura.models.Genre
import kotlinx.android.synthetic.main.genre_adapter.view.*


open class GenreAdapter(private val context:Context, private var list:ArrayList<Genre>)
    :RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var onClickListener: OnClickListener?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return myViewHolder(
            LayoutInflater.from(context).inflate(R.layout.genre_adapter, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var genre = list.get(position)

        holder.itemView.backgroundCardGenre.setImageResource(genre.image)
        holder.itemView.genreText.text = genre.name

        holder.itemView.setOnClickListener{
            if(onClickListener!=null){
                onClickListener!!.onClick(position, genre)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface  OnClickListener{
        fun onClick(position:Int, model: Genre)
    }

    fun setOnClickListener(onClickListener:OnClickListener) {
        this.onClickListener= onClickListener
    }

    private class myViewHolder(view: View):RecyclerView.ViewHolder(view){

    }
}
