package com.ayushunleashed.mitram.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayushunleashed.mitram.adapters.ChatAdapter
import com.ayushunleashed.mitram.databinding.FragmentChatBinding
import com.ayushunleashed.mitram.models.ChatMessageModel
import com.ayushunleashed.mitram.models.UserModel
import com.ayushunleashed.mitram.utils.DateClass
import com.bumptech.glide.Glide
import com.ayushunleashed.mitram.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await


class ChatFragment : Fragment() {
    lateinit var thisContext: Context
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

        if (container != null) {
            thisContext = container.getContext()
        };

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        binding = FragmentChatBinding.bind(view)
        chatUser =requireArguments().getParcelable<UserModel>("userToSend")
        return view
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(chatUser!=null)
        {
            loadCurrentChatUserDetails()
        }
        listenIfReceiverIsOnline()
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
        binding.myRecyclerView.layoutManager = LinearLayoutManager(thisContext)
    }

    fun listenIfReceiverIsOnline()
    {
        db.collection("users").document(chatUser!!.uid!!).addSnapshotListener { value, error ->
            error?.let {
                it.message?.let { it1 -> Log.d("GENERAL", it1) }
                return@addSnapshotListener
            }
            value?.let {
                var onlineStatus = it.getBoolean("isOnline")
                Log.d("IS_ONLINE","Online: {$onlineStatus}")

            if(it.getBoolean("isOnline") == true)
            {
                binding.tvOnlineStatus.setTextColor(ContextCompat.getColor(thisContext,R.color.myGreen))
                binding.tvOnlineStatus.text = "Online"
            }else  if(it.getBoolean("isOnline") == false)
            {
                binding.tvOnlineStatus.setTextColor(ContextCompat.getColor(thisContext,R.color.myGray))
                binding.tvOnlineStatus.text = "Offline"
            }
            }
        }
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun handleClicks()
    {
        binding.btnBackToConnections.setOnClickListener {
            findNavController().navigate(R.id.action_chatFragment_to_connectionsFragment)
        }

        binding.btnSendMessage.setOnClickListener {
            sendMessage()
        }

        binding.btnMoreOptionsChat.setOnClickListener {
            Toast.makeText(thisContext,"You clicked on More Options",Toast.LENGTH_SHORT).show()
        }

        binding.imgReceiverUserProfile.setOnClickListener{
            val myBundle = bundleOf("currentUser" to chatUser,"previousFragmentName" to "ChatFragment")
            findNavController().navigate(R.id.action_chatFragment_to_fullUserProfileFragment,myBundle)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessage()
    {
        val receiverId = chatUser!!.uid
        val dateTime =  dateClass.getTimeStamp()
        var messageText = binding.etvEnterMessage.text.toString()
        messageText = messageText.replace("(^[\\r\\n]+|[\\r\\n]+$)".toRegex(), "");


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
    {        messagesArray.clear()
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
            Log.d("MESSAGE_LIST","List End Here")

            var sortedMessages = messagesArray.sortedWith(compareBy { it.dateTime })
            var sortedMessagesList:MutableList<ChatMessageModel> = mutableListOf()
            if(sortedMessages.isNotEmpty())
            {
                sortedMessagesList = sortedMessages as MutableList<ChatMessageModel>
            }

            Log.d("MESSAGE_LIST","${sortedMessagesList.toString()}")

            // update UI
            chatAdapter = ChatAdapter(sortedMessagesList,senderId!!)

            binding.myRecyclerView.adapter = chatAdapter
            binding.myRecyclerView.layoutManager = LinearLayoutManager(thisContext)
            binding.myRecyclerView.scrollToPosition(sortedMessagesList.size-1)
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
                    binding.myRecyclerView.layoutManager = LinearLayoutManager(thisContext)
                    chatAdapter.notifyDataSetChanged()
                }
            }
    }


    fun loadCurrentChatUserDetails()
    {
        // load image
        Glide.with(binding.imgReceiverUserProfile.context).load(chatUser!!.imageUrl).circleCrop().placeholder(R.drawable.img_user_place_holder)
            .error(R.drawable.img_keep_calm_reload).into(binding.imgReceiverUserProfile)

        //load name
        binding.tvReceiverName.text = chatUser!!.displayName
    }
}