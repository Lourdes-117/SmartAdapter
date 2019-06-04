package com.lourdes.smartadapter;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity_FindAdapter extends  MainActivity{
    public static String findIpOfServer(String ipAddress){
        int i = 0;
        boolean isReachable = false;
        String newIp = "";
        String[] ipAdressArray = ipAddress.split("\\.");
        do {
            newIp = "" + ipAdressArray[0] + "." + ipAdressArray[1] + "." + ipAdressArray[2] + ".";
            newIp = newIp + i++;
            try {
                isReachable = java.net.InetAddress.getByName(newIp).isReachable(200);
            }catch (IOException ioExcpetion){
                ioExcpetion.printStackTrace();
            }
            if (isReachable) {
                try {
                    String receivedMessage;
                    Socket socket = null;
                    try {
                        Log.d("Checking this Ip",ipAddress);
                        socket = new Socket(newIp, 8080);
                    } catch (Exception e) {
                        continue;
                    }
                    InputStream inputStream = socket.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                    printWriter.println("AreYouAdapter?");
                    printWriter.flush();
                    if ((receivedMessage = bufferedReader.readLine()) != null) {
                        if (receivedMessage.equals("Yes,IAm")) {
                            socket.close();
                            return newIp;
                        }
                    }
                    socket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }

        } while (i < 255);
        return "NOTFOUND";
    }
}