package com.ayushunleashed.mitram.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.ayushunleashed.mitram.R
import com.ayushunleashed.mitram.fragments.ConnectionsFragment
import com.ayushunleashed.mitram.models.ChatMessageModel
import com.ayushunleashed.mitram.models.UserModel
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await


class ConnectionsCardAdapter(var users: MutableList<UserModel>,context: Context):RecyclerView.Adapter<ConnectionsCardAdapter.ConnectionsCardViewHolder>() {

    var thisContext = context
    private var currentUser = FirebaseAuth.getInstance().currentUser
    private var senderId = currentUser!!.uid
    inner class ConnectionsCardViewHolder(itemview: View):RecyclerView.ViewHolder(itemview)
    {
        var tvUserName:TextView
        var imgViewUserProfile:ImageView
        var cardViewConnections:CardView
        var btnMessageConnection: ExtendedFloatingActionButton
        var tvLastMessage:TextView

        init {
            tvUserName = itemview.findViewById(R.id.tvUserName)
            imgViewUserProfile = itemview.findViewById(R.id.imgViewUserProfile)
            cardViewConnections = itemview.findViewById(R.id.cardViewConnections)
            btnMessageConnection = itemview.findViewById(R.id.btnMessageConnection)
            tvLastMessage = itemview.findViewById(R.id.tvLastMessage)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConnectionsCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_connections_modern,parent,false)
        var itemViewViewHolder = ConnectionsCardViewHolder(view)

        return ConnectionsCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConnectionsCardViewHolder, position: Int) {

        val name = users[position].displayName
        val words = name?.split("\\s".toRegex())?.toTypedArray()
        holder.tvUserName.text = name?.toUpperCase()
        Glide.with(holder.imgViewUserProfile.context).load(users[position].imageUrl).circleCrop().placeholder(R.drawable.img_user_place_holder)
            .error(R.drawable.img_user_not_found).into(holder.imgViewUserProfile)

        holder.tvLastMessage.text =  getLastMessage(position)



        holder.itemView.setOnLongClickListener {
            Toast.makeText(holder.itemView.context,"You are holding card",Toast.LENGTH_SHORT).show()

            var popupMenu = PopupMenu(holder.itemView.context,holder.itemView)
            popupMenu.inflate(R.menu.connections_menu)
            //popupMenu.gravity = Gravity.END
            popupMenu.setOnMenuItemClickListener {

                ConnectionsFragment().removeConnection( users[position].uid!!)
                users.removeAt(position)
                notifyDataSetChanged()
                false
            }
            popupMenu.show()
            true
        }


        //
        val bundle = bundleOf("userToSend" to users[position])

        //defining nav controller for navigation
        var navController: NavController?=null


        holder.btnMessageConnection.setOnClickListener {

            //for navigating to product description fragment
            navController = Navigation.findNavController(holder.itemView)
            navController!!.navigate(R.id.action_connectionsFragment_to_chatFragment,bundle)
        }
//
//        holder.imgViewUserProfile.setOnClickListener{
//            val myBundle = bundleOf("currentUser" to users[position])
//            navController = Navigation.findNavController(holder.itemView)
//            navController!!.navigate(R.id.action_connectionsFragment_to_fullUserProfileFragment,myBundle)
//        }
    }

    fun getLastMessage(position: Int):String
    {
        var db = FirebaseFirestore.getInstance()
        var receiverId = users[position].uid
        var chatsSender:MutableList<ChatMessageModel> = mutableListOf()
        var chatsReceiver:MutableList<ChatMessageModel> = mutableListOf()
        var chats:MutableList<ChatMessageModel> = mutableListOf()
        var lastMessage:String = "Start Conversation"

        val task1 = GlobalScope.launch(Dispatchers.IO) {

            // get chats of both of them
            chatsSender = db.collection("chat").whereEqualTo("senderId",senderId)
                .whereEqualTo("receiverId",receiverId).orderBy("dateTime",Query.Direction.DESCENDING).limit(1).get().await().toObjects(ChatMessageModel::class.java)

            if(chatsSender.isNotEmpty()) //otherwise crash
            {
                chatsSender[0].messageText = "You: "+chatsSender[0].messageText
            }

            chatsReceiver= db.collection("chat").whereEqualTo("senderId",receiverId)
                .whereEqualTo("receiverId",senderId).orderBy("dateTime",Query.Direction.DESCENDING).limit(1).get().await().toObjects(ChatMessageModel::class.java)

            //add chats of both users
            chats.addAll(chatsSender)
            chats.addAll(chatsReceiver)
            Log.d("GENERAL",chats.toString())
        }
        runBlocking {
            task1.join()
        }

        // if its not empty
        if(chats.size!=0)
        {
            chats = chats.sortedWith(compareBy { it.dateTime }) as MutableList<ChatMessageModel>

            // chats only has 2 messages , 0 ,1 , set the last message, chats[1] to last message
            lastMessage = chats[1].messageText
        }
        return lastMessage
    }

    override fun getItemCount(): Int {
        return  users.size
    }
}