package com.example.chatroom_hackthon

//import static com.example.chatroom_java.Audio.AudioRecorder.audioRecorder;
import static com.example.chatroom_java.data.LoadData.loadJSONFromAsset;
import static com.example.chatroom_java.data.LoadData.parseChatJSON;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatroom_java.chatList.ChatAdapter;
import com.example.chatroom_java.data.Chat;
import com.example.chatroom_java.data.DataSource;
import com.example.chatroom_java.data.LoadData;
import com.example.chatroom_java.Audio.*;
import com.example.chatroom_java.Audio.IAudioCallback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements IAudioCallback{
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private AudioRecorder audioRecorder;
    private boolean isKeepTime;
    /**
     * 支持定时和周期性执行的线程池
     */
    private ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);
    private int time;
    private static final int INITIAL_DELAY = 0;
    private static final int PERIOD = 1000;
    // 声明一个集合，在后面的代码中用来存储用户拒绝授权的权
    private List<String> mPermissionList = new ArrayList<>();
    private final static int ACCESS_FINE_ERROR_CODE = 0x0245;

    private final static int HANDLER_CODE = 0x0249;

    private RecyclerView recyclerView;
    private ChatAdapter adapter;

    private void initData() {
        audioRecorder = AudioRecorder.getInstance(this);
        scheduledThreadPool.scheduleAtFixedRate(() -> {
            if (isKeepTime) {
                ++time;
            }
        }, INITIAL_DELAY, PERIOD, TimeUnit.MILLISECONDS);

        setPermissions(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_SETTINGS,
                        Manifest.permission.RECORD_AUDIO},
                ACCESS_FINE_ERROR_CODE);
    }

    private void setPermissions(String[] permissions, int permissionsCode) {
        mPermissionList.clear();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permission);
                }
            }

            //未授予的权限为空，表示都授予了
            if (mPermissionList.isEmpty()) {
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast.makeText(context, "已经授权", duration);
            } else {
                //将List转为数组
                permissions = mPermissionList.toArray(new String[mPermissionList.size()]);
                ActivityCompat.requestPermissions(this, permissions, permissionsCode);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < grantResults.length; i++) {
            boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]);
            if (showRequestPermission) {
                showToast("权限未申请");
            }
        }
    }

    protected void showToast(String toastInfo) {
        Toast.makeText(this, toastInfo, Toast.LENGTH_LONG).show();
    }
    @Override
    public void showPlay(String filePath) {
        File file = new File(filePath);
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(this , filePath, duration);
        Log.d("FilePath",filePath);
        if (file.exists()) {
//            //合成完后的操作，根据需要去做处理，此处用于测试播放
//            audioRecorder.play(filePath);
            Intent intent = new Intent(this, UploadingService.class);
            intent.putExtra("test", "test");
            startService(intent);
        }
    }
    private boolean notRunning = true;

    private boolean checkPermissions() {
        String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        List<String> permissionsToRequest = new ArrayList<>();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            String[] permissionsArray = permissionsToRequest.toArray(new String[0]);
            ActivityCompat.requestPermissions(this, permissionsArray, REQUEST_RECORD_AUDIO_PERMISSION);
            return false; // Permissions not granted yet
        }

        return true; // Permissions already granted
    }

    @Override
    protected void onDestroy() {
        audioRecorder.release();
        audioRecorder.releaseAudioTrack();
        scheduledThreadPool.shutdown();
        super.onDestroy();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkPermissions()) {
            // Permissions have been granted, you can now proceed with audio recording
            initData();
        } else {
            // Permissions have not been granted, and you should handle this accordingly
        }

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

                if (notRunning) {
                    notRunning = false;
                    try {
                        audioRecorder = AudioRecorder.getInstance(MainActivity.this);
                        if (audioRecorder.getStatus() == AudioStatus.STATUS_NO_READY) {
                            String fileName = new SimpleDateFormat("yyyyMMddhhmmss", Locale.TAIWAN).format(new Date());
                            audioRecorder.createDefaultAudio(fileName);
                            audioRecorder.startRecord();
                            recButton.setBackgroundResource(R.drawable.square_shape);
                            isKeepTime = true;
                        } else {
                            audioRecorder.startRecord();
                            recButton.setBackgroundResource(R.drawable.square_shape);
                            isKeepTime = true;
                        }
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Log.d("Stop","Stop");
                    audioRecorder.setReset();
                    isKeepTime = false;

                    audioRecorder.stopRecord();
                    Log.d("stopRecord","stopRecord");
                    recButton.setBackgroundResource(R.mipmap.ic_record_white);
                    time = 0;
                    notRunning = true;
                }



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
