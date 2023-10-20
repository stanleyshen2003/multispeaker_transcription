package com.example.chatroom_java;

import static android.Manifest.permission.RECORD_AUDIO;
import static com.example.chatroom_java.data.LoadData.loadJSONFromAsset;
import static com.example.chatroom_java.data.LoadData.parseChatJSON;
import static com.example.chatroom_java.data.SocketClient.*;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.os.AsyncTask;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatroom_java.chatList.ChatAdapter;
import com.example.chatroom_java.data.Chat;
import com.example.chatroom_java.data.DataSource;
import com.example.chatroom_java.data.SocketClient;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private boolean isRecording= false;
    private Button recButton;
    private MediaRecorder mediaRecorder;
    private static String recordFile = null;
    private static String recordFilePath = null;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    private DataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //別動-------------------------------------------------------------------------------
        Log.d("Wellcome","Wellcome");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);

        dataSource = DataSource.getDataSource(getResources());
        final java.util.List<Chat> chatList = dataSource.getChatList().getValue();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ChatAdapter(this, chatList != null ? chatList : new ArrayList<Chat>(), recyclerView);
        recyclerView.setAdapter(adapter);


        recButton = findViewById(R.id.rec_button);
    }
    //別動--------------------------------------------------------------------------------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length >= 2) { // Check if there are at least two results
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                        onRestart();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
    public boolean CheckPermissions() {
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result1 == PackageManager.PERMISSION_GRANTED;
    }
    private void RequestPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{RECORD_AUDIO}, REQUEST_AUDIO_PERMISSION_CODE);
    }

    public void recording(View view) throws IOException {
        if (CheckPermissions()) {
            if(isRecording){
                stopRecording();
                isRecording = false;

            }
            else{
                startRecording();
                isRecording = true;
            }
        } else {
            RequestPermissions();
        }

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

    private void startRecording() throws IOException {

        recButton.setBackgroundResource(R.drawable.square_shape);
        recordFilePath = this.getExternalFilesDir("/").getAbsolutePath();
        Log.d("recordFilePath",recordFilePath);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.ENGLISH);
        Date current = new Date();
        recordFile = "Recording_"+ format1.format(current) +".wav";
        Log.d("recordFilePath",recordFilePath);
        Log.d("recordFile",recordFile);
        Log.d("recordFileSize", String.valueOf(recordFile.length()));
        Toast toastStart = Toast.makeText(this, "Recording started",Toast.LENGTH_SHORT);
        toastStart.show();
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordFilePath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.start();

    }


    private void stopRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                Log.d("ZFinish","finish");
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;

            String serverAddress = "172.16.168.1";
            int serverPort = 8082;
            new SocketClient(serverAddress, serverPort, recordFilePath + "/" + recordFile).execute();

            recButton.setBackgroundResource(R.drawable.record_button_background);
            Toast toastStop = Toast.makeText(getBaseContext(), "Recording stopped", Toast.LENGTH_SHORT);
            toastStop.show();
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        if (isRecording) {
            stopRecording();
        }
    }
}

