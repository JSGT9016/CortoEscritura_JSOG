package com.example.cortoescritura.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cortoescritura.Firestore.FirestoreClass
import com.example.cortoescritura.R
import com.example.cortoescritura.models.Comment
import kotlinx.android.synthetic.main.item_comment.view.*


class CommentAdapter(private val context: Context, private var list: ArrayList<Comment>, private var imageList:ArrayList<String>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val listImages : ArrayList<String> = FirestoreClass().getListImages(list)

    private var onClickListener: OnClickListener?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return myViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var comment = list.get(position)


        Glide
            .with(context)
            .load(imageList[position])
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(holder.itemView.iv_member_image)



        holder.itemView.tv_member_name.text = comment.commentedBy
        holder.itemView.tv_member_comment.text = comment.comment

        holder.itemView.setOnClickListener{
            if(onClickListener!=null){
                onClickListener!!.onClick(position, comment)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface  OnClickListener{
        fun onClick(position:Int, model: Comment)
    }

    fun setOnClickListener(onClickListener:OnClickListener) {
        this.onClickListener= onClickListener
    }

    private class myViewHolder(view: View):RecyclerView.ViewHolder(view){

    }
}