package com.example.chatroom_wav.data;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

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

    public String connect() {
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
            io.write(audioBytes);
            socket.close();
            io.close();
            out.close();
            in.close();

            //Thread.sleep(200000);

            System.out.println("Connect again");
            socket = new Socket(dstAddress, dstPort);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while (true) {
                line = input.readLine();
                System.out.println(line);
                response.append(line);
                if (line.equals("]")) {
                    input.close();
                    System.out.println("OK");
                    break;
                }
            }
            socket.close();
            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
            io.write(audioBytes);
            socket.close();

            //Thread.sleep(200000);

            System.out.println("Connect again");
            socket = new Socket(dstAddress, dstPort);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = input.readLine()) != null) {
                response.append(line);
            }
            //System.out.println(response);
            System.out.println("OK");
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

