package com.example.chatroom_wav

import ChatAdapter
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
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
import com.example.chatroom_wav.setting.SettingActivity
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

    private var serverAddress :String = "172.16.168.1"
    private var serverPort :Int = 8082


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //直接讀檔顯示頭兩個chat(之後刪掉)======================================================
        recyclerView = findViewById(R.id.recycler_view)
        val dataSource = DataSource.getDataSource(resources)
        val chatList = dataSource.getChatList().value?.toMutableList() ?: mutableListOf()

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        adapter = ChatAdapter(this, chatList ?: emptyList() ,recyclerView)
        recyclerView.adapter = adapter
        //setting========================================================================
        val settingIcon = findViewById<ImageView>(R.id.setting_icon)

        settingIcon.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)

            startActivity(intent)
        }
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        serverAddress = sharedPreferences.getString("serverAddress", "172.16.168.1") ?: "172.16.168.1"
        serverPort = sharedPreferences.getString("serverPort", "8082")?.toIntOrNull() ?: 8082
        Log.d("MainActivity", "Server Address: $serverAddress, Server Port: $serverPort")
        //開權限===========================================================================
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
        }

        //設定音訊存檔路徑===================================================================
        val downloadDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        if (!downloadDir.exists()) {
            downloadDir.mkdirs()
        }
        val recordFilePath = downloadDir.absolutePath

        filePath = recordFilePath + "/audioFile.wav"

        //設定錄音工具=====================================================================
        waveRecorder = WaveRecorder(filePath)

        waveRecorder.onStateChangeListener = {
            when (it) {
                RecorderState.RECORDING -> startRecording()
                RecorderState.STOP -> stopRecording()
                else -> {}
            }
        }
        waveRecorder.noiseSuppressorActive=true

        //每三秒做一次顯示========================================================================
        var count = 0

        fun createCancelTask(): TimerTask {
            return object : TimerTask() {
                override fun run() {
                    count++
                    Log.d("count", count.toString())

                    //現在沒錄音 => 啟動錄音
                    if (!isRecording) {
                        waveRecorder.startRecording()
                    }
                    //正在錄音 => 關閉錄音
                    else {
                        waveRecorder.stopRecording()
                        waveRecorder.startRecording()
                    }

                    //讀檔生成=====================================================================
                    val json = loadJSONFromAsset(baseContext, "chats1.json")
                    val chatList = parseChatJSON(json)
                    val currentChatList = dataSource.getChatList().value?.toMutableList() ?: mutableListOf()
                    //驗證 append/ concat
                    if (currentChatList.isNotEmpty() && currentChatList.last().name == chatList.first().name) {
                        currentChatList[currentChatList.lastIndex] = currentChatList.last().copy(
                            text = currentChatList.last().text + " " + chatList.first().text
                        )
                        currentChatList.addAll(chatList.subList(1, chatList.size))
                    } else {
                        currentChatList.addAll(chatList)
                    }
                    //更新資料與畫面
                    dataSource.getChatList().postValue(currentChatList)
                    runOnUiThread {
                        adapter.updateData(currentChatList)
                    }
                    recyclerView.postDelayed({
                        recyclerView.smoothScrollToPosition(currentChatList.size - 1)
                    }, 100)

                }
            }
        }


        //設定按鈕=======================================================================
        val recButton = findViewById<Button>(R.id.rec_button)
        recButton.setOnClickListener {

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
                waveRecorder.stopRecording()
            }
        }
    }

    //錄音系列 function==========================================================================
    private fun startRecording() {
        Log.d(TAG, waveRecorder.audioSessionId.toString())
        isRecording = true
    }

    private fun stopRecording() {
        isRecording = false
        runOnUiThread {
            Toast.makeText(this, "File saved at : $filePath", Toast.LENGTH_LONG).show()
        }
//        serverAddress = "172.16.168.1"
//        serverPort = 8082
        SocketClient(serverAddress, serverPort, filePath).execute()
    }


    //權限系列 function==========================================================================
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