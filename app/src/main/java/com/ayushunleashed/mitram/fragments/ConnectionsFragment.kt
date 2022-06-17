package com.ayushunleashed.mitram.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ayushunleashed.mitram.R
import com.ayushunleashed.mitram.adapters.ConnectionsCardAdapter
import com.ayushunleashed.mitram.adapters.PeopleLikesCardAdapter
import com.ayushunleashed.mitram.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class ConnectionsFragment : Fragment() {

    private lateinit var usersList:MutableList<UserModel>
    private lateinit var currentUser: FirebaseUser
    lateinit var tvNoUsersToShow: TextView

    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_connections, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUser = FirebaseAuth.getInstance().currentUser!!
        tvNoUsersToShow = view.findViewById(R.id.tvNoUsers)
        loadData(view)
        
    }

    fun loadData(view: View)
    {    val db = FirebaseFirestore.getInstance()


        GlobalScope.launch(Dispatchers.IO) {

            val currentUserModel = db.collection("users").document(currentUser.uid).get().await()
                .toObject(UserModel::class.java)
            var users:MutableList<UserModel> = arrayListOf()

            val connectionsArray = currentUserModel?.connections

            val addingUsers = launch {
                if (connectionsArray != null) {
                    for (uid in connectionsArray) {

                        var user:UserModel? = null
                        val job = launch {
                            user = db.collection("users").document(uid).get().await().toObject(UserModel::class.java)!!
                        }

                        job.join()

                        if (users != null) {
                            user?.let { users.add(it) }
                        }

                        Log.d("GENERAL", "${user?.displayName} is added to array" );
                    }
                }
            }

            addingUsers.join()

            withContext(Dispatchers.Main)
            {

                if (users != null) {
                    Log.d("GENERAL", "connectionsArray by users list" + users.toString());

                    if(users.isEmpty())
                    {
                        tvNoUsersToShow.visibility = View.VISIBLE
                    }
                    else
                    {
                        tvNoUsersToShow.visibility = View.GONE
                    }
                    Toast.makeText(requireContext(),"Size:${users.size}", Toast.LENGTH_SHORT).show()
                }
                recyclerView = view.findViewById(R.id.myRecyclerView)
                val adapter = users?.let { ConnectionsCardAdapter(it) }
                recyclerView.adapter = adapter
                recyclerView.layoutManager = StaggeredGridLayoutManager(
                    2,
                    StaggeredGridLayoutManager.VERTICAL
                )
            }
        }


    }
}