package com.ayushunleashed.mitram.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ayushunleashed.mitram.R
import com.ayushunleashed.mitram.adapters.PeopleLikesCardAdapter
import com.ayushunleashed.mitram.adapters.UserCardAdapter
import com.ayushunleashed.mitram.models.UserModel
import com.ayushunleashed.notezen.daos.UserDao
import com.firebase.ui.auth.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await


class LikesFragment : Fragment() {

    private lateinit var usersList:MutableList<UserModel>
    private lateinit var currentUser:FirebaseUser
    lateinit var tvNoUsersToShow: TextView

    lateinit var recyclerView:RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_likes, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUser = FirebaseAuth.getInstance().currentUser!!
        tvNoUsersToShow =view.findViewById(R.id.tvNoUsers)
        loadData(view)
    }

    fun loadData(view: View)
    {        val db = FirebaseFirestore.getInstance()


        GlobalScope.launch(Dispatchers.IO) {

            val currentUserModel = db.collection("users").document(currentUser.uid).get().await()
                .toObject(UserModel::class.java)
            var users:MutableList<UserModel> = arrayListOf()

            val likedByArray = currentUserModel?.likedBy

            val addingUsers = launch {
                if (likedByArray != null) {
                    for (uid in likedByArray) {

                        var user:UserModel? = null
                        val job = launch {
                            user = db.collection("users").document(uid).get().await().toObject(UserModel::class.java)!!
                        }

                        job.join()

                        if (users != null) {
                            user?.let { users.add(it) }
                        }

                        Log.d("GENERAL", "${user?.displayName} is added to Likes Page array" );
                    }
                }
            }

            addingUsers.join()

            withContext(Dispatchers.Main)
            {
                if (likedByArray != null) {
                    if(likedByArray.size == 0) {
                        //Toast.makeText(requireContext(),"No Likes",Toast.LENGTH_SHORT).show()
                            tvNoUsersToShow.visibility = View.VISIBLE


                    }else {
                        tvNoUsersToShow.visibility = View.GONE
                    }
                }

                if (users != null) {
                    Log.d("GENERAL", "liked by users list" + users.toString());
                    //Toast.makeText(requireContext(),"Size:${users.size}",Toast.LENGTH_SHORT).show()
                }
                recyclerView = view.findViewById(R.id.myRecyclerView)
                val adapter = users?.let { PeopleLikesCardAdapter(it) }
                recyclerView.adapter = adapter
                recyclerView.layoutManager = StaggeredGridLayoutManager(
                    1,
                    StaggeredGridLayoutManager.VERTICAL
                )
            }
        }


    }
}