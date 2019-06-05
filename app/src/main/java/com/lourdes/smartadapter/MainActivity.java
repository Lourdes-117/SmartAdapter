package com.lourdes.smartadapter;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView textView_statusDisplay;
    private ProgressBar progressBar;
    private Button button;

    public static String serverIpAddress = "Empty";

    DhcpInfo dhcpInfo;
    WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);     //Elements Mapping
        textView_statusDisplay = findViewById(R.id.statusDisplay);
        button = findViewById(R.id.startNetworkActivity);
        progressBar = findViewById(R.id.progressBar);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        button.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        wifiManager= (WifiManager) getSystemService(Context.WIFI_SERVICE);
        dhcpInfo=wifiManager.getDhcpInfo();

        Thread threadFindServer = new Thread(new Runnable() {     //Calling Ping Method
            @Override
            public void run() {
                new MainActivity_FindAdapterAsync().execute(Formatter.formatIpAddress(dhcpInfo.gateway));
                if(serverIpAddress.equals("NotFound")){
                    serverIpAddress = "not Connected";
                }
            }
        });
        threadFindServer.start();
        textView_statusDisplay.setText("Searching for the Server");
    }


    public class MainActivity_FindAdapterAsync extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(MainActivity.this, "Server Found at "+s, Toast.LENGTH_LONG).show();
            textView_statusDisplay.setText("The Server is at "+s);
            progressBar.setProgress(100);
            progressBar.setVisibility(View.INVISIBLE);
            button.setEnabled(true);
        }

        @Override
        protected String doInBackground(String... params) {
            if(!(MainActivity.serverIpAddress.equals("Empty") || MainActivity.serverIpAddress.equals("NotFound")))
            {
                return MainActivity.serverIpAddress;
            }
            String ipAddress = params[0];
            int i = 0;
            boolean isReachable = false;
            String newIp;
            String[] ipAdressArray = ipAddress.split("\\.");
            do {
                progressBar.setProgress(i);
                Log.d("Progress",String.valueOf(i));
                newIp = "" + ipAdressArray[0] + "." + ipAdressArray[1] + "." + ipAdressArray[2] + ".";
                newIp = newIp + i++;
                try {
                    isReachable = java.net.InetAddress.getByName(newIp).isReachable(200);
                }catch (IOException ioException){
                    ioException.printStackTrace();
                }
                if (isReachable) {
                    try {
                        String receivedMessage;
                        Socket socket;
                        try {
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
                                MainActivity.serverIpAddress = newIp;
                                return newIp;
                            }
                        }
                        socket.close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }

            } while (i < 255);
            MainActivity.serverIpAddress = "NotFound";
            return "NotFound";
        }
    }

}