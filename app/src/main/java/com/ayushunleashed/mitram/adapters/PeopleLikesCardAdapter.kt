package com.ayushunleashed.mitram.adapters

import android.util.Log
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

class PeopleLikesCardAdapter(var users: MutableList<UserModel>):RecyclerView.Adapter<PeopleLikesCardAdapter.PeopleLikesCardViewHolder>() {

    inner class PeopleLikesCardViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        var tvUserName: TextView
        var imgViewUserProfile: ImageView
        var btnAcceptConnection: Button
        var btnDeclineConnection: Button

        init {
            tvUserName = itemview.findViewById(R.id.tvUserName)
            imgViewUserProfile = itemview.findViewById(R.id.imgViewUserProfile)
            btnDeclineConnection = itemview.findViewById(R.id.btnDeclineConnection)
            btnAcceptConnection = itemview.findViewById(R.id.btnAcceptConnection)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleLikesCardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_people_likes_modern, parent, false)
        return PeopleLikesCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: PeopleLikesCardViewHolder, position: Int) {

        val name = users[position].displayName
        val words = name?.split("\\s".toRegex())?.toTypedArray()
        holder.tvUserName.text = name?.toUpperCase()
        Glide.with(holder.imgViewUserProfile.context).load(users[position].imageUrl).circleCrop()
            .into(holder.imgViewUserProfile)


        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val likeCardUserModel = users[position]

        val bundle = bundleOf("userToSend" to users[position])

        //defining nav controller for navigation
        var navController: NavController? = null

        holder.imgViewUserProfile.setOnClickListener {
            val myBundle = bundleOf(
                "currentUser" to users[position],
                "previousFragmentName" to "LikesFragment"
            )
            navController = Navigation.findNavController(holder.itemView)
            navController!!.navigate(R.id.action_likesFragment_to_fullUserProfileFragment, myBundle)
        }

        holder.btnAcceptConnection.setOnClickListener {
            //add to connection of current user

            GlobalScope.launch(Dispatchers.IO) {

                //current user model has current user logged in
                var currentUserModel =
                    db.collection("users").document(currentUser!!.uid).get().await()
                        .toObject(UserModel::class.java)
                // if current user is not empty and current user does not has this person already as connections

                if (currentUserModel != null && !(currentUserModel.connections.contains(
                        likeCardUserModel.uid
                    )
                            && likeCardUserModel.connections.contains(currentUserModel.uid))
                ) {


                    //removing will make sure when i add there is only one connection
                    currentUserModel.connections.remove(likeCardUserModel.uid!!)

                    if (!currentUserModel.connections.contains(likeCardUserModel.uid)) {
                        //add the connection request person's id to current users list of connection
                        currentUserModel.connections.add(likeCardUserModel.uid)
                    }

                    //removing will make sure when i add there is only one connection
                    likeCardUserModel.connections.remove(currentUserModel.uid!!)

                    if (!likeCardUserModel.connections.contains(currentUserModel.uid)) {
                        // also add current user to the request person's connection list
                        likeCardUserModel.connections.add(currentUserModel.uid!!)
                    }
                    // removing liked from current users likedby array

                    //once they are connections no need for any of this
                    currentUserModel.likedBy.remove(likeCardUserModel.uid)

                    // precautions - this 3
                    currentUserModel.usersYouLiked.remove(likeCardUserModel.uid)
                    likeCardUserModel.likedBy.remove(currentUserModel.uid)
                    likeCardUserModel.usersYouLiked.remove(currentUserModel.uid)


                    withContext(Dispatchers.Main)
                    {
                        //delete from ui
                        users.removeAt(position)
                        notifyDataSetChanged()
                    }

                    //updating this to database

                    if (currentUserModel != null) {
                        // current user's like list and connections list both are updated
                        db.collection("users").document(currentUserModel.uid!!)
                            .set(currentUserModel).await()
                    }

                    if (likeCardUserModel != null) {
                        // request user's connection list is updated
                        db.collection("users").document(likeCardUserModel.uid)
                            .set(likeCardUserModel).await()
                    }
                } else if (currentUserModel != null && (currentUserModel.connections.contains(
                        likeCardUserModel.uid
                    )
                            || likeCardUserModel.connections.contains(currentUserModel.uid))
                ) {
                    // if duplicacy occured, we have person already in connections

                    //if they are connections no need for any of this
                    currentUserModel.likedBy.remove(likeCardUserModel.uid)

                    // precautions - this 3
                    currentUserModel.usersYouLiked.remove(likeCardUserModel.uid)
                    likeCardUserModel.likedBy.remove(currentUserModel.uid)
                    likeCardUserModel.usersYouLiked.remove(currentUserModel.uid)

                    //lets say one side has connection, other doesn't in that case we make it both side connection


                    if (!currentUserModel.connections.contains(likeCardUserModel.uid)) {

                        //add the connection request person's id to current users list of connection
                        currentUserModel.connections.add(likeCardUserModel.uid!!)
                    }


                    if (!likeCardUserModel.connections.contains(currentUserModel.uid)) {
                        // also add current user to the request person's connection list
                        likeCardUserModel.connections.add(currentUserModel.uid!!)
                    }

                    withContext(Dispatchers.Main)
                    {
                        //delete from ui
                        users.removeAt(position)
                        notifyDataSetChanged()
                    }

                    //updating this to database

                    if (currentUserModel != null) {
                        // current user's like list and connections list both are updated
                        db.collection("users").document(currentUserModel.uid!!)
                            .set(currentUserModel).await()
                    }

                    if (likeCardUserModel != null) {
                        // request user's connection list is updated
                        db.collection("users").document(likeCardUserModel.uid!!)
                            .set(likeCardUserModel).await()
                    }


                }

            }

        }

        holder.btnDeclineConnection.setOnClickListener {
            // remove from liked by of current user and remove from ui
            Log.d("DECLINE_LIKE","remove like button clicked")
            GlobalScope.launch(Dispatchers.IO) {
                var currentUserModel =
                    db.collection("users").document(currentUser!!.uid).get().await()
                        .toObject(UserModel::class.java)
                if(currentUserModel==null){
                    Log.d("DECLINE_LIKE","user model is null while canceling")
                }
                if (currentUserModel != null) {
                    Log.d("DECLINE_LIKE","user model is not null while canceling")
                    // since this person declined we would remove him from list of users who that person liked
                    likeCardUserModel.usersYouLiked.remove(currentUserModel.uid)

                    // removing from liked array
                    currentUserModel.likedBy.remove(likeCardUserModel.uid)

                    //precautions - this 2
                    currentUserModel.usersYouLiked.remove(likeCardUserModel.uid)
                    likeCardUserModel.likedBy.remove(currentUserModel.uid)


                    withContext(Dispatchers.Main)
                    {
                        //delete from ui
                        users.removeAt(position)
                        notifyDataSetChanged()
                    }

                    //updating this to database

                    if (currentUserModel != null) {
                        // current user's like list and connections list both are updated
                        db.collection("users").document(currentUserModel.uid!!)
                            .set(currentUserModel).await()
                    }

                    if (likeCardUserModel != null) {
                        // request user's connection list is updated
                        db.collection("users").document(likeCardUserModel.uid!!)
                            .set(likeCardUserModel).await()
                    }

                }
            }
        }
    }
        override fun getItemCount(): Int {
            return users.size
        }

}