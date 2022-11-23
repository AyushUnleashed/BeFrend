package com.ayushunleashed.mitram.adapters

import android.content.Context
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
import com.ayushunleashed.mitram.models.UserModel
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*


class ConnectionsCardAdapter(var lastMessageHashMap: HashMap<String,String>,var users: MutableList<UserModel>,context: Context):RecyclerView.Adapter<ConnectionsCardAdapter.ConnectionsCardViewHolder>() {

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
            .error(R.drawable.img_keep_calm_reload).into(holder.imgViewUserProfile)

        holder.tvLastMessage.text = lastMessageHashMap[users[position].uid]



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



        val bundle = bundleOf("userToSend" to users[position])

        //defining nav controller for navigation
        var navController: NavController?=null


        holder.btnMessageConnection.setOnClickListener {

            //for navigating to product description fragment
            navController = Navigation.findNavController(holder.itemView)
            navController!!.navigate(R.id.action_connectionsFragment_to_chatFragment,bundle)
        }

        holder.imgViewUserProfile.setOnClickListener{
            val myBundle = bundleOf("currentUser" to users[position],"previousFragmentName" to "ConnectionsFragment")
            navController = Navigation.findNavController(holder.itemView)
            navController!!.navigate(R.id.action_connectionsFragment_to_fullUserProfileFragment,myBundle)
        }
    }

    override fun getItemCount(): Int {
        return  users.size
    }
}