package com.example.chatroom_hackthon

import ChatAdapter
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatroom_hackthon.data.DataSource
import com.example.chatroom_hackthon.data.loadJSONFromAsset
import com.example.chatroom_hackthon.data.parseChatJSON


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_view)

        val dataSource = DataSource.getDataSource(resources)
        val chatList = dataSource.getChatList().value

        recyclerView.layoutManager =
            LinearLayoutManager(this) // 设置LayoutManager为LinearLayoutManager

        adapter = ChatAdapter(this, chatList ?: emptyList() ,recyclerView)
        recyclerView.adapter = adapter

        val recButton = findViewById<Button>(R.id.rec_button)
        recButton.setOnClickListener {
            //你要寫在這
            //test


            //----------------------------
            val json = loadJSONFromAsset(baseContext, "chats.json")
            val chatList = parseChatJSON(json)
            val currentChatList = dataSource.getChatList().value?.toMutableList() ?: mutableListOf()
            currentChatList.addAll(chatList)

            dataSource.getChatList().postValue(currentChatList)
            adapter.updateData(currentChatList)

            recyclerView.postDelayed({
                recyclerView.smoothScrollToPosition(currentChatList.size - 1)
            }, 100)



        }



    }

}

