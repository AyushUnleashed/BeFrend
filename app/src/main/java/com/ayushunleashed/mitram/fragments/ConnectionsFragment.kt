package com.ayushunleashed.mitram.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ayushunleashed.mitram.R
import com.ayushunleashed.mitram.SharedViewModel
import com.ayushunleashed.mitram.adapters.ConnectionsCardAdapter
import com.ayushunleashed.mitram.adapters.ConnectionsFirestoreRVCardAdapter
import com.ayushunleashed.mitram.databinding.FragmentConnectionsBinding
import com.ayushunleashed.mitram.models.ChatMessageModel
import com.ayushunleashed.mitram.models.UserModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await


class ConnectionsFragment : Fragment() {
    lateinit var thisContext: Context
    lateinit var sharedViewModel: SharedViewModel
    private lateinit var usersList:MutableList<UserModel>
    private lateinit var currentUser: FirebaseUser
    private lateinit var db:FirebaseFirestore
    lateinit var  currentUserModel:UserModel
    var myConnectionsList:MutableList<UserModel> = mutableListOf()

    lateinit var tvNoUsersToShow: TextView
    private lateinit var progressBar: ProgressBar
    lateinit var recyclerView: RecyclerView

    private lateinit var binding: FragmentConnectionsBinding

    var connectionsListArray = ArrayList<String>()
    var firestoreAdapter: ConnectionsFirestoreRVCardAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (container != null) {
            thisContext = container.getContext()
        };
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_connections, container, false)
        binding = FragmentConnectionsBinding.bind(view)

        db = FirebaseFirestore.getInstance()
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        currentUserModel = sharedViewModel.currentUserModel

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUser = FirebaseAuth.getInstance().currentUser!!

        setupViews(view)
        //giveRealtimeUpdate(view)

        binding.refreshLayout.setOnRefreshListener {
            loadData(view)

        }

        if(sharedViewModel.loadedConnectionsFragmentBefore == true) {
            binding.tvUserCount.text = "( ${sharedViewModel.myConnectionsList.size} )"
            val adapter =
                sharedViewModel.myConnectionsList.let { ConnectionsCardAdapter(sharedViewModel.messagesHashMap,it, thisContext) }
            recyclerView.adapter = adapter
            recyclerView.layoutManager = StaggeredGridLayoutManager(
                1,
                StaggeredGridLayoutManager.VERTICAL
            )
        }else
        {
            loadData(view)
            sharedViewModel.loadedConnectionsFragmentBefore = true
        }

        //loadData(view) // old method
        //setupFirestoreRecyclerView(view) - new method incomplete
    }

    fun giveRealtimeUpdate(view: View){
        Log.d("Snap", "Inside Give Realtime Update")
        db.collection("users").document(currentUser.uid).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.d("Snap", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d("Snap", "Current data: ${snapshot.data}")
                loadData(view)
                Log.d("Snap","After Load Data in snapshot listener")

            } else {
                Log.d("Snap", "Current data: null")
            }
        }
    }

    fun setupViews(view: View)
    {

        tvNoUsersToShow = view.findViewById(R.id.tvNoUsers)
        progressBar = view.findViewById(R.id.progressBar)
        recyclerView = view.findViewById(R.id.myRecyclerView)
    }

    fun loadConnectionArray(){
        myConnectionsList.clear()
        progressBar.visibility = View.VISIBLE


        GlobalScope.launch(Dispatchers.IO) {
            currentUserModel = db.collection("users").document(currentUser.uid).get().await()
                .toObject(UserModel::class.java)!!
            connectionsListArray = currentUserModel?.connections!!
        }


    }

//    override fun onStart() {
//        super.onStart()
//        firestoreAdapter.startListening()
//    }
//
//    override fun onStop() {
//        super.onStop()
//        firestoreAdapter.stopListening()
//    }

    fun setupFirestoreRecyclerView(view: View){
        //progressBar.visibility = View.VISIBLE
        //loadConnectionArray()
        connectionsListArray = sharedViewModel.currentUserModel.connections
        Log.d("GENERAL","ConnectionsListArray:$connectionsListArray")

        val usersCollection = db.collection("users")
        val query = usersCollection.whereIn("uid",connectionsListArray)

        val options: FirestoreRecyclerOptions<UserModel> =
            FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel::class.java)
                .build()

        firestoreAdapter = ConnectionsFirestoreRVCardAdapter(options)
        recyclerView.adapter =firestoreAdapter
        recyclerView.layoutManager = LinearLayoutManager(thisContext)
    }

    fun loadData(view: View)
    {
        myConnectionsList.clear()
        progressBar.visibility = View.VISIBLE


        GlobalScope.launch(Dispatchers.IO) {
            currentUserModel = db.collection("users").document(currentUser.uid).get().await()
                .toObject(UserModel::class.java)!!

            val connectionsArray = currentUserModel?.connections

            val addingUsers = launch(Dispatchers.IO) {
                if (connectionsArray != null) {
                    for (uid in connectionsArray) {
                        sharedViewModel.messagesHashMap[uid] = getLastMessage(uid);
                        var user = db.collection("users").document(uid).get().await().toObject(UserModel::class.java)!!
                        user?.let { myConnectionsList.add(it) }

                        Log.d("GENERAL", "${user?.displayName} is added to array" );
                    }
                }
            }

            addingUsers.join()
//            sharedViewModel.loadedConnectionsFragmentBefore = true
//            sharedViewModel.myConnectionsList = myConnectionsList
            withContext(Dispatchers.Main)
            {
                binding.tvUserCount.text = "( ${myConnectionsList.size} )"
                progressBar.visibility = View.GONE
                binding.refreshLayout.isRefreshing = false
                Log.d("GENERAL", "connectionsArray by users list" + myConnectionsList.toString());

                if(myConnectionsList.isEmpty())
                {
                    tvNoUsersToShow.visibility = View.VISIBLE
                }
                else
                {
                    tvNoUsersToShow.visibility = View.GONE
                }
                //Toast.makeText(thisContext,"Size:${myConnectionsList.size}", Toast.LENGTH_SHORT).show()

                sharedViewModel.myConnectionsList = myConnectionsList
                val adapter = myConnectionsList.let { ConnectionsCardAdapter(sharedViewModel.messagesHashMap,it, thisContext) }
                recyclerView.adapter = adapter
                recyclerView.layoutManager = StaggeredGridLayoutManager(
                    1,
                    StaggeredGridLayoutManager.VERTICAL
                )
            }
        }
    }

    fun getLastMessage(receiverUId: String):String
    {
        var senderId = currentUser.uid
        var db = FirebaseFirestore.getInstance()
        var receiverId = receiverUId
        var chatsSender:MutableList<ChatMessageModel> = mutableListOf()
        var chatsReceiver:MutableList<ChatMessageModel> = mutableListOf()
        var chats:MutableList<ChatMessageModel> = mutableListOf()
        var lastMessage:String = "Start Conversation"

        val task1 = GlobalScope.launch(Dispatchers.IO) {

            // get chats of both of them
            chatsSender = db.collection("chat").whereEqualTo("senderId",senderId)
                .whereEqualTo("receiverId",receiverId).orderBy("dateTime", Query.Direction.DESCENDING).limit(1).get().await().toObjects(
                    ChatMessageModel::class.java)

            if(chatsSender.isNotEmpty()) //otherwise crash
            {
                chatsSender[0].messageText = "You: "+chatsSender[0].messageText
            }

            chatsReceiver= db.collection("chat").whereEqualTo("senderId",receiverId)
                .whereEqualTo("receiverId",senderId).orderBy("dateTime", Query.Direction.DESCENDING).limit(1).get().await().toObjects(
                    ChatMessageModel::class.java)

            //add chats of both users
            chats.addAll(chatsSender)
            chats.addAll(chatsReceiver)
            Log.d("GENERAL",chats.toString())
        }
        runBlocking{
            task1.join()
        }

        // if its not empty
        if(chats.size!=0)
        {
            chats = chats.sortedWith(compareBy { it.dateTime }) as MutableList<ChatMessageModel>

            // chats only has 2 messages , 0 ,1 , set the last message, chats[1] to last message
            if(chats.size==1){
                lastMessage = chats[0].messageText
            }else if(chats.size==2){
                lastMessage = chats[1].messageText
            }
        }
        return lastMessage
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