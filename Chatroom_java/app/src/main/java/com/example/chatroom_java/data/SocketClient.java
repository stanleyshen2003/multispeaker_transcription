package com.example.chatroom_java.data;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.*;


public class SocketClient extends AsyncTask<String, Integer, String> {
    private String dstAddress;
    private int dstPort;
    private String file_path;
    private String response = "";

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

            // Send the message to the server
            File file = new File(this.file_path);
            byte[] fileData = new byte[(int) file.length()];
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            bufferedInputStream.read(fileData, 0, fileData.length);
            OutputStream outputStream = socket.getOutputStream();
            //PrintWriter printWriter = new PrintWriter(outputStream, true);

            // Sending file name and file data
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeUTF(file.getName());
            dataOutputStream.writeInt(fileData.length);
            dataOutputStream.write(fileData, 0, fileData.length);
            dataOutputStream.flush();

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

