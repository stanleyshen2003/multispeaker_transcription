import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatroom_wav.R
import com.example.chatroom_wav.data.Chat
import com.example.chatroom_wav.data.getImageForName

class ChatAdapter(private val context: Context, private var chatList: List<Chat>, private val recyclerView: RecyclerView) :
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
        holder.userName.text = chat.name
        holder.chatText.text = chat.text
        holder.userImage.setImageResource(getImageForName(chat.name) ?: R.drawable.user_image)

        if (position == chatList.size - 1) {
            recyclerView.smoothScrollToPosition(position)

        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    fun updateData(newChatList: List<Chat>) {
        chatList = newChatList.toMutableList()
        notifyDataSetChanged()
    }

}
