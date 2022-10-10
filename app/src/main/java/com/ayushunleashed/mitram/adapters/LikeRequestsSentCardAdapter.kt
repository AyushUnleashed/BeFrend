package com.ayushunleashed.mitram.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.ayushunleashed.mitram.R
import com.ayushunleashed.mitram.models.UserModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import org.json.JSONObject.NULL


class LikeRequestsSentCardAdapter(var users: MutableList<UserModel>):RecyclerView.Adapter<LikeRequestsSentCardAdapter.LikeRequestsSentCardViewHolder>() {

    inner class  LikeRequestsSentCardViewHolder(itemview: View):RecyclerView.ViewHolder(itemview)
    {
        var tvUserName:TextView
        var imgViewUserProfile:ImageView
        var btnCancelRequest: Button

        init {
            tvUserName = itemview.findViewById(R.id.tvUserName)
            imgViewUserProfile = itemview.findViewById(R.id.imgViewUserProfile)
            btnCancelRequest = itemview.findViewById(R.id.btnCancelRequest)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LikeRequestsSentCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_people_like_requests,parent,false)
        return LikeRequestsSentCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: LikeRequestsSentCardViewHolder, position: Int) {

        val name = users[position].displayName
        val words = name?.split("\\s".toRegex())?.toTypedArray()
        holder.tvUserName.text = name?.toUpperCase()
        Glide.with(holder.imgViewUserProfile.context).load(users[position].imageUrl).circleCrop().into(holder.imgViewUserProfile)



        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val likeCardUserModel = users[position]

        val bundle = bundleOf("userToSend" to users[position])

        //defining nav controller for navigation
        var navController: NavController?=null

        holder.imgViewUserProfile.setOnClickListener{
            val myBundle = bundleOf("currentUser" to users[position],"previousFragmentName" to "LikeRequestsSentFragment")
            navController = Navigation.findNavController(holder.itemView)
            navController!!.navigate(R.id.action_likeRequestsSentFragment_to_fullUserProfileFragment,myBundle)
        }

        holder.btnCancelRequest.setOnClickListener{
            // remove from liked by of current user and remove from ui

            GlobalScope.launch(Dispatchers.IO) {
                var currentUserModel = db.collection("users").document(currentUser!!.uid).get().await().toObject(UserModel::class.java)

                if (currentUserModel != null) {

                    // cc
                    currentUserModel.usersYouLiked.remove(likeCardUserModel.uid)

                    // cc
                    likeCardUserModel.likedBy.remove(currentUserModel.uid)

                    //precautions - this 2

                    likeCardUserModel.usersYouLiked.remove(currentUserModel.uid)
                    currentUserModel.likedBy.remove(likeCardUserModel.uid)


                    withContext(Dispatchers.Main)
                    {
                        //delete from ui
                        users.removeAt(position)
                        notifyDataSetChanged()
                    }

                    //updating this to database

                    if (currentUserModel !=NULL) {
                        // current user's like list and connections list both are updated
                        currentUserModel.uid?.let { it1 ->
                            db.collection("users").document(it1).set(currentUserModel)
                                .await()
                        }
                    }

                    if (likeCardUserModel != NULL) {
                        // request user's connection list is updated
                        db.collection("users").document(likeCardUserModel.uid!!)
                            .set(likeCardUserModel).await()
                    }
                }


            }
        }
    }

    override fun getItemCount(): Int {
        return  users.size
    }
}