package com.example.chatroom_wav

import ChatAdapter
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatroom_wav.data.DataSource
import com.example.chatroom_wav.data.loadJSONFromAsset
import com.example.chatroom_wav.data.parseChatJSON
import com.example.chatroom_wav.R
import com.example.chatroom_wav.data.Chat
import com.example.chatroom_wav.wave.RecorderState
import com.example.chatroom_wav.wave.WaveRecorder


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter

    private val PERMISSIONS_REQUEST_RECORD_AUDIO = 77

    private lateinit var waveRecorder: WaveRecorder
    private lateinit var filePath: String
    private var isRecording = false

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

        val downloadDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        if (!downloadDir.exists()) {
            downloadDir.mkdirs()
        }
        val recordFilePath = downloadDir.absolutePath

        filePath = recordFilePath + "/audioFile.wav"

        waveRecorder = WaveRecorder(filePath)

        waveRecorder.onStateChangeListener = {
            when (it) {
                RecorderState.RECORDING -> startRecording()
                RecorderState.STOP -> stopRecording()
                else -> {}
            }
        }
        //todo
        val newChat = Chat(id = 7, name = "New User", image = R.drawable.user_image, text = "New Message")
        dataSource.addChat(newChat)
        adapter.updateData(dataSource.getChatList().value ?: emptyList())
        recyclerView.adapter = adapter
        //========================================

        val recButton = findViewById<Button>(R.id.rec_button)
        recButton.setOnClickListener {
            //你要寫在這
            if (!isRecording) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.RECORD_AUDIO
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.RECORD_AUDIO),
                        PERMISSIONS_REQUEST_RECORD_AUDIO
                    )
                } else {
                    waveRecorder.startRecording()
                }
            } else {
                waveRecorder.stopRecording()
            }
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

    private fun startRecording() {
        Log.d(TAG, waveRecorder.audioSessionId.toString())
        isRecording = true
    }

    private fun stopRecording() {
        isRecording = false
        Toast.makeText(this, "File saved at : $filePath", Toast.LENGTH_LONG).show()
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_RECORD_AUDIO -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    waveRecorder.startRecording()
                }
                return
            }

            else -> {
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }

}

