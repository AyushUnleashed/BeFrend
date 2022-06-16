package com.ayushunleashed.mitram.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asynctaskcoffee.cardstack.CardContainerAdapter
import com.ayushunleashed.mitram.R
import com.ayushunleashed.mitram.models.UserModel
import com.bumptech.glide.Glide
import kotlinx.coroutines.NonDisposableHandle.parent


class UserCardAdapter(var users: List<UserModel>,context: Context): CardContainerAdapter() {

    var layoutInflater: LayoutInflater = LayoutInflater.from(context)


    @SuppressLint("InflateParams")
    override fun getView(position: Int): View {
        val view = layoutInflater.inflate(R.layout.item_user,null)

        var tvUserName = view.findViewById<TextView>(R.id.tvUserName)
        var tvUserBio = view.findViewById<TextView>(R.id.tvUserBio)
        var imgViewUserProfile = view.findViewById<ImageView>(R.id.imgViewUserProfile)

        val user = getItem(position)

        tvUserName.text = users[position].displayName
        tvUserBio.text = users[position].bio
        Glide.with(imgViewUserProfile.context).load(users[position].imageUrl).placeholder(R.drawable.img_user_place_holder)
            .error(R.drawable.img_user_profile_sample).into(imgViewUserProfile)

        return view
    }

    override fun getCount(): Int {
        return users.size
    }

    override fun getItem(position: Int): Any {
        return users[position]
    }
}

//
//class UserCardAdapter(var users: List<UserModel>,context: Context):RecyclerView.Adapter<UserCardAdapter.UserCardViewHolder>() {
//
//
//    inner class UserCardViewHolder(itemview: View):RecyclerView.ViewHolder(itemview)
//    {
//        var tvUserName:TextView
//        var tvUserBio:TextView
//        var imgViewUserProfile:ImageView
//
//        init {
//            tvUserBio = itemview.findViewById(R.id.tvUserBio)
//            tvUserName = itemview.findViewById(R.id.tvUserName)
//            imgViewUserProfile = itemview.findViewById(R.id.imgViewUserProfile)
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserCardViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user,parent,false)
//        return UserCardViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: UserCardViewHolder, position: Int) {
//
//        holder.tvUserName.text = users[position].displayName
//        holder.tvUserBio.text = users[position].bio
//        Glide.with(holder.imgViewUserProfile.context).load(users[position].imageUrl).into(holder.imgViewUserProfile)
//    }
//
//    override fun getItemCount(): Int {
//        return  users.size
//    }
//}