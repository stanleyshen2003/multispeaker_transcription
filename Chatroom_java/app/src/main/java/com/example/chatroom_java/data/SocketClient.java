package com.example.chatroom_java.data;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.*;

public class SocketClient extends AsyncTask<String, Integer, String> {
    private String dstAddress;
    private int dstPort;
    private String file_path;
    private String response = "";
    private static BufferedInputStream inputStream;

    public SocketClient(String serverAddress, int serverPort, String message) {
        dstAddress = serverAddress;
        dstPort = serverPort;
        this.file_path = message;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            System.out.println("Connecting to " + this.dstAddress + " on port " + this.dstPort);
            Socket socket = new Socket(dstAddress, dstPort);
            System.out.println("Just connected to " + socket.getRemoteSocketAddress());

            byte[] bytes = new byte[1024];
            System.out.println(this.file_path);
            BufferedOutputStream in = new BufferedOutputStream(new FileOutputStream(this.file_path));
            inputStream = new BufferedInputStream(socket.getInputStream());
            while (true) {
                int bytesRead = inputStream.read(bytes);
                System.out.println("Send wav file");
                if (bytesRead < 0) break;
                in.write(bytes, 0, bytesRead);
                // Now it loops around to read some more.
            }
            in.close();

            // Read the response from the server
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            response = bufferedReader.readLine();

            // Close the connection
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        // Print the response from the server
        Log.d("SocketClient", "Response: " + result);
    }

}

