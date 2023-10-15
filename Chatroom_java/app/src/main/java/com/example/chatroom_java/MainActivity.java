package com.example.chatroom_java;

import static com.example.chatroom_java.data.LoadData.loadJSONFromAsset;
import static com.example.chatroom_java.data.LoadData.parseChatJSON;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatroom_java.chatList.ChatAdapter;
import com.example.chatroom_java.data.Chat;
import com.example.chatroom_java.data.DataSource;
import com.example.chatroom_java.data.LoadData;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);

        DataSource dataSource = DataSource.getDataSource(getResources());
        final java.util.List<Chat> chatList = dataSource.getChatList().getValue();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ChatAdapter(this, chatList != null ? chatList : new ArrayList<Chat>(), recyclerView);
        recyclerView.setAdapter(adapter);

        Button recButton = findViewById(R.id.rec_button);
        recButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = loadJSONFromAsset(getApplicationContext(), "chats.json");
                List<Chat> chatList = parseChatJSON(json);
                List<Chat> currentChatList = new ArrayList<>(dataSource.getChatList().getValue());
                currentChatList.addAll(chatList);

                dataSource.getChatList().setValue(currentChatList);
                adapter.updateData(currentChatList);

                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.smoothScrollToPosition(currentChatList.size() - 1);
                    }
                }, 100);
            }
        });
    }
}
