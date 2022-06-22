package com.ayushunleashed.mitram.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ayushunleashed.mitram.R
import com.ayushunleashed.mitram.adapters.ChatAdapter
import com.ayushunleashed.mitram.databinding.FragmentChatBinding
import com.ayushunleashed.mitram.models.ChatMessageModel
import com.ayushunleashed.mitram.models.UserModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class ChatFragment : Fragment() {

//    lateinit var userIdToLoad:String
        var chatUser:UserModel? = null
     private lateinit var binding: FragmentChatBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        binding = FragmentChatBinding.bind(view)
        chatUser =requireArguments().getParcelable<UserModel>("userToSend")

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(chatUser!=null)
        {
            loadCurrentChatUserDetails()
        }
        handleClicks()
        setupRecyclerView()

    }

    fun dummyChatData():MutableList<ChatMessageModel>
    {
        var messagesList:MutableList<ChatMessageModel> = mutableListOf(
            ChatMessageModel("123","456","Hello","10:00 am"),
            ChatMessageModel("456","123","Hi Buddy","11:00 am"),
            ChatMessageModel("123","456","My Name is Ayush","12:00 pm"),
            ChatMessageModel("456","123","My Name is Yashraj","01:00 pm"),
            ChatMessageModel("123","456","Cool","02:00 pm"),
            ChatMessageModel("123","456","Nice to meet You, Yashraj","03:00 pm"),
            ChatMessageModel("123","456","Nice to meet You, Yashraj","03:00 pm")
        )
        return messagesList
    }

    fun setupRecyclerView()
    {
        val adapter = ChatAdapter(dummyChatData(),"123")
        binding.myRecyclerView.adapter = adapter
        binding.myRecyclerView.layoutManager = StaggeredGridLayoutManager(
            1,
            StaggeredGridLayoutManager.VERTICAL
        )
    }

    fun handleClicks()
    {
        binding.btnBackToConnections.setOnClickListener {
            findNavController().navigate(R.id.action_chatFragment_to_connectionsFragment)
        }
    }

    fun loadCurrentChatUserDetails()
    {
        // load image
        Glide.with(binding.imgReceiverUserProfile.context).load(chatUser!!.imageUrl).circleCrop().placeholder(R.drawable.img_user_place_holder)
            .error(R.drawable.img_user_not_found).into(binding.imgReceiverUserProfile)

        //load name
        binding.tvReceiverName.text = chatUser!!.displayName
    }


}