import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatroom_hackthon.R
import com.example.chatroom_hackthon.data.Chat

class ChatAdapter(private val context: Context, private var chatList: List<Chat>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userImage: ImageView = itemView.findViewById(R.id.user_image)
        val userName: TextView = itemView.findViewById(R.id.user_name)
        val chatText: TextView = itemView.findViewById(R.id.chat_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatList[position]
        holder.userImage.setImageResource(chat.image ?: R.drawable.user_image)
        holder.userName.text = chat.name
        holder.chatText.text = chat.text
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    fun updateData(newChatList: List<Chat>) {
        chatList = newChatList
        notifyDataSetChanged() // Notify the RecyclerView that data has changed
    }
}
