package com.example.chatroom_wav.setting

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.widget.Toolbar
import com.example.chatroom_wav.MainActivity
import com.example.chatroom_wav.R

class SettingActivity : AppCompatActivity() {
    private var serverAddress: String = "192.168.43.218"
    private var serverPort: Int = 8082
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting)

        val settingToolbar = findViewById<Toolbar>(R.id.setting_toolbar)
        val serverAddressEditText = findViewById<EditText>(R.id.InputServerAddress)
        val serverPortEditText = findViewById<EditText>(R.id.InputServerPort)
        val languageSpinner = findViewById<Spinner>(R.id.InputLanguage)

        val languageOptions = arrayOf("en", "zh-TW")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languageOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val selectedLanguage = sharedPreferences.getString("language", "en")
        val position = languageOptions.indexOf(selectedLanguage)
        if (position != -1) {
            languageSpinner.setSelection(position)
        }

        serverAddressEditText.setText(serverAddress)
        serverPortEditText.setText(serverPort.toString())

        settingToolbar.setNavigationOnClickListener {
            // Retrieve the values from the EditText fields
            val newServerAddress = serverAddressEditText.text.toString()
            val newServerPort = serverPortEditText.text.toString()
            val newLanguage = languageSpinner.selectedItem.toString()

            // Use SharedPreferences to store the values
            val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("serverAddress", newServerAddress)
            editor.putString("serverPort", newServerPort)
            editor.putString("language", newLanguage)
            editor.apply()

            // Send the values back to the MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("serverAddress", newServerAddress)
            intent.putExtra("serverPort", newServerPort)
            intent.putExtra("language", newLanguage)
            startActivity(intent)
        }

    }
}