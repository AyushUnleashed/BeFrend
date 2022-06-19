package com.ayushunleashed.mitram.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
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
    private lateinit var db:FirebaseFirestore
    var  currentUserModel:UserModel? = null


    lateinit var tvNoUsersToShow: TextView
    private lateinit var progressBar: ProgressBar
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

        db = FirebaseFirestore.getInstance()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUser = FirebaseAuth.getInstance().currentUser!!

        GlobalScope.launch {
            currentUserModel = db.collection("users").document(currentUser.uid).get().await()
                .toObject(UserModel::class.java)
        }

        setupViews(view)

        loadData(view)
        
    }

    fun setupViews(view: View)
    {

        tvNoUsersToShow = view.findViewById(R.id.tvNoUsers)
        progressBar = view.findViewById(R.id.progressBar)
    }

    fun loadData(view: View)
    {
        progressBar.visibility = View.VISIBLE


        GlobalScope.launch(Dispatchers.IO) {

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

                        user?.let { users.add(it) }

                        Log.d("GENERAL", "${user?.displayName} is added to array" );
                    }
                }
            }

            addingUsers.join()

            withContext(Dispatchers.Main)
            {
                progressBar.visibility = View.GONE
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
                recyclerView = view.findViewById(R.id.myRecyclerView)
                val adapter = users.let { ConnectionsCardAdapter(it,requireContext()) }
                recyclerView.adapter = adapter
                recyclerView.layoutManager = StaggeredGridLayoutManager(
                    1,
                    StaggeredGridLayoutManager.VERTICAL
                )
            }
        }
    }

    fun removeConnection(connectionUidToBeRemoved: String)
    {
        GlobalScope.launch(Dispatchers.IO) {
            db = FirebaseFirestore.getInstance()
            val currentUserModel = db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid).get().await()
                .toObject(UserModel::class.java)

            var currentConnection = db.collection("users").document(connectionUidToBeRemoved).get().await().toObject(UserModel::class.java)

            if (currentConnection != null && currentUserModel!=null)
            {
                currentUserModel.connections.remove(connectionUidToBeRemoved)
                Log.d("GENERAL","Removed ${currentConnection.displayName} from ${currentUserModel.displayName} offline")

                currentConnection.connections.remove(currentUserModel.uid);
                Log.d("GENERAL","Removed ${currentUserModel.displayName} from ${currentConnection.displayName} offline")


                //precautions - this 4
                currentUserModel.likedBy.remove(currentConnection.uid)
                currentConnection.likedBy.remove(currentUserModel.uid)
                currentUserModel.usersYouLiked.remove(currentConnection.uid)
                currentConnection.usersYouLiked.remove(currentUserModel.uid)


            //update to database

                db.collection("users").document(connectionUidToBeRemoved).set(currentConnection).await()
                Log.d("GENERAL","Updated ${currentConnection.displayName}")
                db.collection("users").document(currentUserModel.uid!!).set(currentUserModel).await()
                Log.d("GENERAL","Updated ${currentUserModel.displayName}")
            }
        }

    }
}