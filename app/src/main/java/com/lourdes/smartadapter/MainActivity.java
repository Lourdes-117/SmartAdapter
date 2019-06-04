package com.lourdes.smartadapter;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView textView_statusDisplay;
    private Button button;
    private String ip;

    DhcpInfo dhcpInfo;
    WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);     //Elements Mapping
        textView_statusDisplay = findViewById(R.id.statusDisplay);
        button = findViewById(R.id.startNetworkActivity);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        wifiManager= (WifiManager) getSystemService(Context.WIFI_SERVICE);
        dhcpInfo=wifiManager.getDhcpInfo();

        Thread threadFindServer = new Thread(new Runnable() {     //Calling Ping Method
            @Override
            public void run() {
                ip = MainActivity_FindAdapter.findIpOfServer(Formatter.formatIpAddress(dhcpInfo.gateway));
                if(ip.equals("NOTFOUND")){
                    ip = "not Connected";
                }
            }
        });
        threadFindServer.start();
        textView_statusDisplay.setText("Your Server is "+ip);
    }
}