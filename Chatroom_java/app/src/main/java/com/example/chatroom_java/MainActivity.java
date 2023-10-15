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
                //TODO: ---------------------------------------------------
                /* 寫一個function 可以每三秒鐘儲存一次 .wav
                   function 可以寫在 folder Yihua/ 底下
                   新增檔案方式: 右鍵 Yihua >> New >>  Java Class >> Class
                   呼叫 function的方式: import com.example.chatroom_java.Yihua.你的檔名 */

                //-----------------------------------------------------------------------
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
