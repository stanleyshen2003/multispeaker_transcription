package com.example.chatroom_java.chatList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatroom_java.R;
import com.example.chatroom_java.data.Chat;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private Context context;
    private List<Chat> chatList;
    private RecyclerView recyclerView;

    public ChatAdapter(Context context, List<Chat> chatList, RecyclerView recyclerView) {
        this.context = context;
        this.chatList = chatList;
        this.recyclerView = recyclerView;
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage;
        TextView userName;
        TextView chatText;

        public ChatViewHolder(View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_image);
            userName = itemView.findViewById(R.id.user_name);
            chatText = itemView.findViewById(R.id.chat_text);
        }
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.userImage.setImageResource(chat.getImage() != 0 ? chat.getImage() : R.drawable.user_image);
        holder.userName.setText(chat.getName());
        holder.chatText.setText(chat.getText());

        if (position == chatList.size() - 1) {
            recyclerView.smoothScrollToPosition(position);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public void updateData(List<Chat> newChatList) {
        chatList = newChatList;
        notifyDataSetChanged();
    }
}
