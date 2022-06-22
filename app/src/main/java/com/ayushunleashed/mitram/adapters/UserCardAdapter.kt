package com.ayushunleashed.mitram.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.asynctaskcoffee.cardstack.CardContainerAdapter
import com.ayushunleashed.mitram.R
import com.ayushunleashed.mitram.models.UserModel
import com.bumptech.glide.Glide


class UserCardAdapter(var users: List<UserModel>,context: Context): CardContainerAdapter() {

    var layoutInflater: LayoutInflater = LayoutInflater.from(context)
    var thisContext:Context = context


    @SuppressLint("InflateParams")
    override fun getView(position: Int): View {
        val view = layoutInflater.inflate(R.layout.item_user,null)

        var tvUserName = view.findViewById<TextView>(R.id.tvUserName)
        var tvUserBio = view.findViewById<TextView>(R.id.tvUserBioDetailPage)
        var imgViewUserProfile = view.findViewById<ImageView>(R.id.imgViewUserProfile)
        var cardViewProfileDescription:CardView = view.findViewById(R.id.cardViewProfileDescription)
        var btnShowFullProfile = view.findViewById<ImageButton>(R.id.btnShowFullProfile)
        var myCardView =view.findViewById<CardView>(R.id.myCardView)
        val user = getItem(position)

        tvUserName.text = users[position].displayName
        tvUserBio.text = users[position].bio
        Glide.with(imgViewUserProfile.context).load(users[position].imageUrl).placeholder(R.drawable.img_user_place_holder)
            .error(R.drawable.img_user_profile_sample).into(imgViewUserProfile)

        //
        val bundle = bundleOf("currentUser" to users[position])

        //defining nav controller for navigation
        var navController: NavController?=null


       btnShowFullProfile.setOnClickListener{

                //for navigating to product description fragment
                navController = Navigation.findNavController(view)
                navController!!.navigate(R.id.action_discoverFragment_to_fullUserProfileFragment,bundle)
        }



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