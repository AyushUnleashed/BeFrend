package com.ayushunleashed.mitram.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ayushunleashed.mitram.R
import com.ayushunleashed.mitram.databinding.ItemContainerReceivedMessageBinding
import com.ayushunleashed.mitram.databinding.ItemContainerSentMessageBinding
import com.ayushunleashed.mitram.models.ChatMessageModel

public class ChatAdapterOrignal(chatMessages:MutableList<ChatMessageModel>, senderId:String): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var VIEW_TYPE_SENT: Int = 1
    private var VIEW_TYPE_RECEIVED: Int =2
    private var chatMessages:MutableList<ChatMessageModel> = chatMessages
    private var senderId:String = senderId

    class SentMessageViewHolder(itemContainerSentMessageBinding: ItemContainerSentMessageBinding,
                                      itemView: View
    ):
        RecyclerView.ViewHolder(itemView) {

        var tvTextMessage = itemView.findViewById<TextView>(R.id.tvTextMessage)
        var tvTextTime = itemView.findViewById<TextView>(R.id.tvTextTime)

        private var binding: ItemContainerSentMessageBinding = ItemContainerSentMessageBinding.bind(itemContainerSentMessageBinding.root)

        fun setData(chatMessage:ChatMessageModel)
        {
            binding.tvTextMessage.text = chatMessage.messageText
            Log.d("GENERAL","sender setting ${chatMessage.messageText} as TextMessage")
            binding.tvTextTime.text = chatMessage.dateTime
            Log.d("GENERAL","sender setting ${chatMessage.dateTime} as TextTime")

            tvTextMessage.text = chatMessage.messageText
            Log.d("GENERAL","sender setting ${tvTextMessage.text} as TextMessage")
            tvTextTime.text = chatMessage.dateTime
            Log.d("GENERAL","sender setting ${tvTextTime.text} as TextTime")
        }
    }

    class ReceivedMessageViewHolder(itemContainerReceivedMessageBinding: ItemContainerReceivedMessageBinding,
                                    itemView: View
    ):
        RecyclerView.ViewHolder(itemView) {

        private var binding: ItemContainerReceivedMessageBinding = ItemContainerReceivedMessageBinding.bind(itemContainerReceivedMessageBinding.root)

        fun setData(chatMessage:ChatMessageModel)
        {
            binding.tvTextMessage.text = chatMessage.messageText
            Log.d("GENERAL","Receiver setting ${chatMessage.messageText} as TextMessage")

            binding.tvTextTime.text = chatMessage.dateTime
            Log.d("GENERAL","Receiver setting ${chatMessage.dateTime} as TextTime")

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if(viewType == VIEW_TYPE_SENT)
        {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_container_sent_message,parent,false)
            var itemViewHolder = SentMessageViewHolder( ItemContainerSentMessageBinding.inflate(LayoutInflater.from(parent.context),parent,false)  , view)
            return  itemViewHolder
        }
        else
        {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_container_received_message,parent,false)
            var itemViewHolder = ReceivedMessageViewHolder( ItemContainerReceivedMessageBinding.inflate(LayoutInflater.from(parent.context),parent,false), view)
            return  itemViewHolder
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position) == VIEW_TYPE_SENT)
        {
            (holder as SentMessageViewHolder).setData(chatMessages[position])
        }
        else{
            (holder as ReceivedMessageViewHolder).setData(chatMessages[position])
        }
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    override fun getItemViewType(position: Int): Int {
        if(chatMessages[position].senderId == senderId)
        {
            return VIEW_TYPE_SENT
        }
        else
        {
            return VIEW_TYPE_RECEIVED
        }

    }

}