package com.ayushunleashed.mitram.fragments

import android.graphics.Color
import kotlin.Comparable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ayushunleashed.mitram.R
import com.ayushunleashed.mitram.adapters.ChatAdapter
import com.ayushunleashed.mitram.databinding.FragmentChatBinding
import com.ayushunleashed.mitram.models.ChatMessageModel
import com.ayushunleashed.mitram.models.UserModel
import com.ayushunleashed.mitram.utils.DateClass
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.text.DateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class ChatFragment : Fragment() {

    private var chatUser:UserModel? = null
    private lateinit var binding: FragmentChatBinding
    private var db= FirebaseFirestore.getInstance()
    private var currentUser = FirebaseAuth.getInstance().currentUser
    private var senderId = currentUser!!.uid
    lateinit var currentUserModel: UserModel
    private var dateClass= DateClass()


    private var messagesArray:MutableList<ChatMessageModel> = mutableListOf()

    private lateinit var chatAdapter:ChatAdapter

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
        getMessages()
        Log.d("GENERAL","GET Messages is called")
        //listenMessages()

    }

    fun setupRecyclerView()
    {
        chatAdapter = ChatAdapter(messagesArray,senderId!!)
        binding.myRecyclerView.adapter = chatAdapter
        binding.myRecyclerView.layoutManager = LinearLayoutManager(requireContext())
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

    fun handleClicks()
    {
        binding.btnBackToConnections.setOnClickListener {
            findNavController().navigate(R.id.action_chatFragment_to_connectionsFragment)
        }

        binding.btnSendMessage.setOnClickListener {
            sendMessage()
        }
    }

    fun sendMessage()
    {
        val receiverId = chatUser!!.uid
        val dateTime =  dateClass.getTimeAndDate()
        val messageText = binding.etvEnterMessage.text.toString()

        if(messageText!="" && messageText.trim().isNotEmpty())
        {
            GlobalScope.launch(Dispatchers.IO) {

                val sender = db.collection("users").document(currentUser!!.uid).get().await().toObject(UserModel::class.java)
                val senderId = sender!!.uid

                val message = ChatMessageModel(senderId!!,receiverId!!,messageText,dateTime)
                // add to database

                db.collection("chat").document().set(message)
            }
        }

        // clear text view
        binding.etvEnterMessage.setText("")
    }


    fun getMessages()
    {
            val senderId = currentUser!!.uid
            Log.d("MESSAGE_LIST","Sender $senderId")
            db.collection("chat").whereEqualTo("senderId",senderId)
                .whereEqualTo("receiverId",chatUser!!.uid).addSnapshotListener(listner)
        Log.d("MESSAGE_LIST","Sender ${chatUser!!.uid}")
            db.collection("chat").whereEqualTo("senderId",chatUser!!.uid)
                .whereEqualTo("receiverId",senderId).addSnapshotListener(listner)
    }

    val listner: (QuerySnapshot?,FirebaseFirestoreException?)-> Unit  = { value, error ->

        error?.let {
            it.message?.let { it1 -> Log.d("GENERAL", it1) }
            return@let
        }
        value?.let {

            //var messagesArray:MutableList<ChatMessageModel> = mutableListOf()
            // messagesArray.clear()
            Log.d("MESSAGE_LIST","List Start Here")
            for(documentChange in value.documentChanges){

                val message = documentChange.document.toObject(ChatMessageModel::class.java)
                Log.d("MESSAGE_LIST","Message: ${message!!.messageText}")
                if (message != null) {
                    messagesArray.add(message)
                }
            }


            var sortedMessages = messagesArray.sortedWith(compareBy { it.dateTime })
            var sortedMessagesList:MutableList<ChatMessageModel> = mutableListOf()
            if(sortedMessages.size!=0)
            {
                sortedMessagesList = sortedMessages as MutableList<ChatMessageModel>
            }

            Log.d("MESSAGE_LIST","List End Here")

            // update UI
            chatAdapter = ChatAdapter(sortedMessagesList,senderId!!)
            binding.myRecyclerView.adapter = chatAdapter
            binding.myRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            chatAdapter.notifyDataSetChanged()
        }
    }

    fun getMessagesOrignal()
    {
        val senderId = currentUser!!.uid
        db.collection("chat").whereEqualTo("senderId",senderId)
            .whereEqualTo("receiverId",chatUser!!.uid).addSnapshotListener { value, error ->

                error?.let {
                    it.message?.let { it1 -> Log.d("GENERAL", it1) }
                    return@addSnapshotListener
                }
                value?.let {

                    var messagesArray:MutableList<ChatMessageModel> = mutableListOf()
                    // messagesArray.clear()
                    Log.d("MESSAGE_LIST","List Start Here")
                    for(document in value.documents) {

                        val message = document.toObject(ChatMessageModel::class.java)
                        Log.d("MESSAGE_LIST","Message: ${message!!.messageText}")
                        if (message != null) {
                            messagesArray.add(message)
                        }
                    }
                    Log.d("MESSAGE_LIST","List End Here")

                    // update UI
                    chatAdapter = ChatAdapter(messagesArray,senderId!!)
                    binding.myRecyclerView.adapter = chatAdapter
                    binding.myRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                    chatAdapter.notifyDataSetChanged()
                }
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