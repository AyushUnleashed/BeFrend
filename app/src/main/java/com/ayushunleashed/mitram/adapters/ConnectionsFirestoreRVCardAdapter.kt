package com.ayushunleashed.mitram.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class ConnectionsFirestoreRVCardAdapter(options: FirestoreRecyclerOptions<UserModel>) :
    FirestoreRecyclerAdapter<UserModel, ConnectionsFirestoreRVCardAdapter.ConnectionsCardViewHolder>(
        options
    ) {

    private var currentUser = FirebaseAuth.getInstance().currentUser
    private var senderId = currentUser!!.uid

    class ConnectionsCardViewHolder(itemview: View): RecyclerView.ViewHolder(itemview)
    {
        var tvUserName: TextView = itemview.findViewById(R.id.tvUserName)
        var imgViewUserProfile: ImageView  = itemview.findViewById(R.id.imgViewUserProfile)
        var cardViewConnections: CardView = itemview.findViewById(R.id.cardViewConnections)
        var btnMessageConnection: ExtendedFloatingActionButton = itemview.findViewById(R.id.btnMessageConnection)
        var tvLastMessage: TextView = itemview.findViewById(R.id.tvLastMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConnectionsCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_connections_modern,parent,false)
        var itemViewViewHolder = ConnectionsCardViewHolder(view)

        return ConnectionsCardViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ConnectionsCardViewHolder,
        position: Int,
        model: UserModel
    ) {
        val name = model.displayName
        val words = name?.split("\\s".toRegex())?.toTypedArray()
        holder.tvUserName.text = name?.toUpperCase()
        Glide.with(holder.imgViewUserProfile.context).load(model.imageUrl).circleCrop().placeholder(R.drawable.img_user_place_holder)
            .error(R.drawable.img_keep_calm_reload).into(holder.imgViewUserProfile)

        holder.tvLastMessage.text =  getLastMessage(position,model)
        Log.d("GENERAL","name is set:$name")


        holder.itemView.setOnLongClickListener {
            Toast.makeText(holder.itemView.context,"You are holding card", Toast.LENGTH_SHORT).show()

            var popupMenu = PopupMenu(holder.itemView.context,holder.itemView)
            popupMenu.inflate(R.menu.connections_menu)
            //popupMenu.gravity = Gravity.END
            popupMenu.setOnMenuItemClickListener {

                ConnectionsFragment().removeConnection( model.uid!!)
                // remove user from shared view model also
                notifyDataSetChanged()
                false
            }
            popupMenu.show()
            true
        }


        //
        val bundle = bundleOf("userToSend" to model)

        //defining nav controller for navigation
        var navController: NavController?=null


        holder.btnMessageConnection.setOnClickListener {

            //for navigating to product description fragment
            navController = Navigation.findNavController(holder.itemView)
            navController!!.navigate(R.id.action_connectionsFragment_to_chatFragment,bundle)
        }
//
        holder.imgViewUserProfile.setOnClickListener{
            val myBundle = bundleOf("currentUser" to model,"previousFragmentName" to "ConnectionsFragment")
            navController = Navigation.findNavController(holder.itemView)
            navController!!.navigate(R.id.action_connectionsFragment_to_fullUserProfileFragment,myBundle)
        }
    }



    fun getLastMessage(position: Int,model: UserModel):String
    {   Log.d("GENERAL","FirestoreRV, Fetching lastmsg")
        var db = FirebaseFirestore.getInstance()
        var receiverId = model.uid
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
        runBlocking {
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
}