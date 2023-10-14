package com.example.chatroom_hackthon

import ChatAdapter
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatroom_hackthon.data.Chat
import com.example.chatroom_hackthon.data.DataSource


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

        adapter = ChatAdapter(this, chatList ?: emptyList())
        recyclerView.adapter = adapter

        val recButton = findViewById<Button>(R.id.rec_button)
        recButton.setOnClickListener {
            //你要寫在這
            //test


            //----------------------------
            val newChat = Chat(id = 7, name = "New User", image = R.drawable.user_image, text = "New Message")
            dataSource.addChat(newChat)

            adapter.updateData(dataSource.getChatList().value ?: emptyList())

        }



    }

}

