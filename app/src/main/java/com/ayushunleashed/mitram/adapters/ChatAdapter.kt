package com.ayushunleashed.mitram.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ayushunleashed.mitram.R
import com.ayushunleashed.mitram.databinding.ItemContainerReceivedMessageBinding
import com.ayushunleashed.mitram.databinding.ItemContainerSentMessageBinding
import com.ayushunleashed.mitram.models.ChatMessageModel
import com.ayushunleashed.mitram.utils.DateClass

public class ChatAdapter(chatMessages:MutableList<ChatMessageModel>, senderId:String): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

            tvTextMessage.text = chatMessage.messageText
            Log.d("GENERAL","sender setting ${tvTextMessage.text} as TextMessage")
            //val timeAndDate = chatMessage.dateTime.split("\r?\n|\r".toRegex()).toTypedArray()
            val timeAndDate = chatMessage.dateTime.split("\\s".toRegex()).toTypedArray()
            var time = timeAndDate[1];
            time = DateClass().convertTime(time);
            val date = DateClass().convertDate(timeAndDate[0]);
            val timeArray = time.split(':');
            var timeHourAndMinutes:String;
            if(timeArray.size==1){
                timeHourAndMinutes = time;
            }else{
                timeHourAndMinutes = timeArray[0]+":"+timeArray[1];
            }
            tvTextTime.text =timeHourAndMinutes;
            Log.d("GENERAL","sender setting ${tvTextTime.text} as TextTime")
        }
    }




    class ReceivedMessageViewHolder(itemContainerReceivedMessageBinding: ItemContainerReceivedMessageBinding,
                                    itemView: View
    ):
        RecyclerView.ViewHolder(itemView) {

        var tvTextMessage = itemView.findViewById<TextView>(R.id.tvTextMessage)
        var tvTextTime = itemView.findViewById<TextView>(R.id.tvTextTime)

        private var binding: ItemContainerReceivedMessageBinding = ItemContainerReceivedMessageBinding.bind(itemContainerReceivedMessageBinding.root)

        fun setData(chatMessage:ChatMessageModel)
        {
            tvTextMessage.text = chatMessage.messageText
            Log.d("GENERAL","sender setting ${tvTextMessage.text} as TextMessage")
            //val timeAndDate = chatMessage.dateTime.split("\r?\n|\r".toRegex()).toTypedArray()
            val timeAndDate = chatMessage.dateTime.split("\\s".toRegex()).toTypedArray()
            var time = timeAndDate[1];
            time = DateClass().convertTime(time);
            val timeArray = time.split(':');
            val date = DateClass().convertDate(timeAndDate[0]);
            var timeHourAndMinutes:String;
            if(timeArray.size==1){
                timeHourAndMinutes = time;
            }else{
                timeHourAndMinutes = timeArray[0]+":"+timeArray[1];
            }
            tvTextTime.text =timeHourAndMinutes;
            Log.d("GENERAL","sender setting ${tvTextTime.text} as TextTime")

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if(viewType == VIEW_TYPE_SENT)
        {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_container_sent_message,parent,false)
            val itemViewHolder = SentMessageViewHolder( ItemContainerSentMessageBinding.inflate(LayoutInflater.from(parent.context),parent,false)  , view)
            return  itemViewHolder
        }
        else
        {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_container_received_message,parent,false)
            val itemViewHolder = ReceivedMessageViewHolder( ItemContainerReceivedMessageBinding.inflate(LayoutInflater.from(parent.context),parent,false), view)
            return  itemViewHolder
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        //holder.tvTextMessage

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