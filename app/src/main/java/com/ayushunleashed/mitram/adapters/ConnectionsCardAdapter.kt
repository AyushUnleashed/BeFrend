package com.ayushunleashed.mitram.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asynctaskcoffee.cardstack.CardContainerAdapter
import com.ayushunleashed.mitram.R
import com.ayushunleashed.mitram.models.UserModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.NonDisposableHandle.parent
import kotlinx.coroutines.tasks.await


class ConnectionsCardAdapter(var users: MutableList<UserModel>):RecyclerView.Adapter<ConnectionsCardAdapter.ConnectionsCardViewHolder>() {

    inner class ConnectionsCardViewHolder(itemview: View):RecyclerView.ViewHolder(itemview)
    {
        var tvUserName:TextView
        var imgViewUserProfile:ImageView

        init {
            tvUserName = itemview.findViewById(R.id.tvUserName)
            imgViewUserProfile = itemview.findViewById(R.id.imgViewUserProfile)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConnectionsCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_connections,parent,false)
        return ConnectionsCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConnectionsCardViewHolder, position: Int) {

        val name = users[position].displayName
        val words = name.split("\\s".toRegex()).toTypedArray()
        holder.tvUserName.text = words[0]
        Glide.with(holder.imgViewUserProfile.context).load(users[position].imageUrl).placeholder(R.drawable.img_user_place_holder)
            .error(R.drawable.img_user_not_found).into(holder.imgViewUserProfile)

    }

    override fun getItemCount(): Int {
        return  users.size
    }
}