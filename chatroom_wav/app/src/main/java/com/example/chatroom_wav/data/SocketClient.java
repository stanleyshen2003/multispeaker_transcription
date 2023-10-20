package com.example.chatroom_wav.data;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

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

            OutputStream io = socket.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(this.file_path));

            int read;
            byte[] buff = new byte[2048];
            while ((read = in.read(buff)) > 0)
            {
                out.write(buff, 0, read);
            }
            out.flush();
            byte[] audioBytes = out.toByteArray();
            System.out.println(audioBytes);
            io.write(audioBytes);

            Thread.sleep(20000);

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = input.readLine()) != null) {
                response.append(line);
            }

            // Close the connection
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        // Print the response from the server
        Log.d("SocketClient", "Response: " + result);
    }

}

