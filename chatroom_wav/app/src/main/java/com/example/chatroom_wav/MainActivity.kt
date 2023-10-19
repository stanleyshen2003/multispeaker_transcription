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
import com.example.chatroom_wav.data.Chat
import com.example.chatroom_wav.data.DataSource
import com.example.chatroom_wav.data.SocketClient
import com.example.chatroom_wav.data.loadJSONFromAsset
import com.example.chatroom_wav.data.parseChatJSON
import com.example.chatroom_wav.wave.RecorderState
import com.example.chatroom_wav.wave.WaveRecorder
import java.util.Timer
import java.util.TimerTask


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter

    private val PERMISSIONS_REQUEST_RECORD_AUDIO = 77

    private lateinit var waveRecorder: WaveRecorder
    private lateinit var filePath: String
    private var isRecording = false

    private var timer: Timer? = null
    private var isTimerScheduled = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_view)

        val dataSource = DataSource.getDataSource(resources)
        val chatList = dataSource.getChatList().value?.toMutableList() ?: mutableListOf()

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
        waveRecorder.noiseSuppressorActive=true
        //todo




        val newChat = Chat(id = 7, name = "New User", image = R.drawable.user_image, text = "New Message")
        val updatedChatList = chatList?.toMutableList() ?: mutableListOf()
        val iterations = 5
        var count = 0

        fun createCancelTask(): TimerTask {
            return object : TimerTask() {
                override fun run() {
                    count++
                    // Add the new chat to the current chatList and update the adapter
                    Log.d("count", count.toString())

                    //add

                    val json = loadJSONFromAsset(baseContext, "chats1.json")
                    val chatList = parseChatJSON(json)
                    val currentChatList = dataSource.getChatList().value?.toMutableList() ?: mutableListOf()

                    if (currentChatList.isNotEmpty() && currentChatList.last().name == chatList.first().name) {
                        currentChatList[currentChatList.lastIndex] = currentChatList.last().copy(
                            text = currentChatList.last().text + " " + chatList.first().text
                        )
                        currentChatList.addAll(chatList.subList(1, chatList.size))
                    } else {
                        currentChatList.addAll(chatList)
                    }


                    dataSource.getChatList().postValue(currentChatList)
                    runOnUiThread {
                        adapter.updateData(currentChatList)
                    }
                    recyclerView.postDelayed({
                        recyclerView.smoothScrollToPosition(currentChatList.size - 1)
                    }, 100)
                    //
                    /*
                    updatedChatList.add(newChat)

                    runOnUiThread {
                        adapter.updateData(updatedChatList)
                    }*/
                }
            }
        }

        //========================================

        val recButton = findViewById<Button>(R.id.rec_button)
        recButton.setOnClickListener {
            //你要寫在這

            if (!isTimerScheduled) {
                timer = Timer()
                val cancelTask1 = createCancelTask()
                timer?.schedule(cancelTask1, 0, 3000)
                Log.d("schedule", "schedule")
                isTimerScheduled = true
            } else {
                timer?.cancel()
                Log.d("cancel", "cancel")
                isTimerScheduled = false
            }

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
                    //timer.schedule(cancelTask, 0, 3000)
                }
            } else {
                waveRecorder.stopRecording()
                //timer.cancel()
            }
            //----------------------------
            val json = loadJSONFromAsset(baseContext, "chats1.json")
            val chatList = parseChatJSON(json)
            val currentChatList = dataSource.getChatList().value?.toMutableList() ?: mutableListOf()
            if (currentChatList.isNotEmpty() && currentChatList.last().name == chatList.first().name) {
                currentChatList[currentChatList.lastIndex] = currentChatList.last().copy(
                    text = currentChatList.last().text + " " + chatList.first().text
                )
                currentChatList.addAll(chatList.subList(1, chatList.size))
            } else {
                currentChatList.addAll(chatList)
            }
            Log.d("current", currentChatList.toString())

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
        val serverAddress = "172.16.168.1"
        val serverPort = 8082
        SocketClient(serverAddress, serverPort, filePath).execute()
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
    override fun onDestroy() {
        super.onDestroy()
        // Ensure you cancel the timer when the activity is destroyed to avoid memory leaks
        timer?.cancel()
    }
}