package com.example.chatroom_wav.setting

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import com.example.chatroom_wav.MainActivity
import com.example.chatroom_wav.R

class SettingActivity : AppCompatActivity() {
    private var serverAddress: String = "172.17.41.191"
    private var serverPort: Int = 8082
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting)

        val settingToolbar = findViewById<Toolbar>(R.id.setting_toolbar)
        val serverAddressEditText = findViewById<EditText>(R.id.InputServerAddress)
        val serverPortEditText = findViewById<EditText>(R.id.InputServerPort)

        // Set the text of the EditText widgets to the original values
        serverAddressEditText.setText(serverAddress)
        serverPortEditText.setText(serverPort.toString())

        settingToolbar.setNavigationOnClickListener {
            // Retrieve the values from the EditText fields
            val newServerAddress = serverAddressEditText.text.toString()
            val newServerPort = serverPortEditText.text.toString()

            // Use SharedPreferences to store the values
            val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("serverAddress", newServerAddress)
            editor.putString("serverPort", newServerPort)
            editor.apply()

            // Send the values back to the MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("serverAddress", newServerAddress)
            intent.putExtra("serverPort", newServerPort)
            startActivity(intent)
        }

    }
}