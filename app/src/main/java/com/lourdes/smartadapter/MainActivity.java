package com.lourdes.smartadapter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText message_EditText;
    private TextView statusDisplay_TextView;
    private Button button;
    private EditText ipAddress_EditText;
    public static InetAddress inetAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);     //Elements Mapping
        message_EditText = findViewById(R.id.message);
        statusDisplay_TextView = findViewById(R.id.statusDisplay);
        button = findViewById(R.id.startNetworkActivity);
        ipAddress_EditText = findViewById(R.id.ipAddress);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        try {       //Create InetAddress
            inetAddress = InetAddress.getByName(ipAddress_EditText.getText().toString());
        } catch (UnknownHostException unknownHostException) {
            unknownHostException.printStackTrace();
        }
        statusDisplay_TextView.setText(message_EditText.getText());
        statusDisplay_TextView.append("\n");

        Thread threadPing = new Thread(new Runnable() {     //Calling Ping Method
            @Override
            public void run() {
                ping();
            }
        });
        threadPing.start();

        Thread threadSendMessage = new Thread(new Runnable() {      //Sending Message
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(ipAddress_EditText.getText().toString(), 8080);
                    PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                    printWriter.write(message_EditText.getText().toString());
                    printWriter.flush();
                    printWriter.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

            }
        });
        threadSendMessage.start();
    }

    private void ping() {       //Ping Method
        try {
            if (MainActivity.inetAddress.isReachable(5000)) {
                statusDisplay_TextView.append("Response OK");
            } else {
                statusDisplay_TextView.append("No response: Time out");
            }
        } catch (Exception e) {
            statusDisplay_TextView.append(e.toString());
        }
    }
}
