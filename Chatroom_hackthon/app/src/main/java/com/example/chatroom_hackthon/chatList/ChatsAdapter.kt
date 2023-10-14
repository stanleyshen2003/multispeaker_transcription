package com.example.chatroom_hackthon.chatList


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chatroom_hackthon.R
import com.example.chatroom_hackthon.data.Chat

class ChatsAdapter(private val onClick: (Chat) -> Unit) :
    ListAdapter<Chat, ChatsAdapter.ChatViewHolder>(ChatDiffCallback) {

        class ChatViewHolder(itemView: View, val onClick: (Chat) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val userTextView: TextView = itemView.findViewById(R.id.user_name)
        private val chatTextView: TextView = itemView.findViewById(R.id.chat_text)
        private val userImageView: ImageView = itemView.findViewById(R.id.user_image)
        private var currentChat: Chat? = null

        init {
            itemView.setOnClickListener {
                currentChat?.let {
                    onClick(it)
                }
            }
        }

        fun bind(chat: Chat) {
            currentChat = chat

            chatTextView.text = chat.text
            userTextView.text = chat.name
            if (chat.image != null) {
                userImageView.setImageResource(chat.image)
            } else {
                userImageView.setImageResource(R.drawable.user_image)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_item, parent, false)
        return ChatViewHolder(view, onClick)
    }

    /* Gets current flower and uses it to bind view. */
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = getItem(position)
        holder.bind(chat)

    }
}

object ChatDiffCallback : DiffUtil.ItemCallback<Chat>() {
    override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
        return oldItem.id == newItem.id
    }
}