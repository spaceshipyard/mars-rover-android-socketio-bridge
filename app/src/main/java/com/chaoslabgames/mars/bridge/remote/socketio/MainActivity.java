package com.chaoslabgames.mars.bridge.remote.socketio;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    //final String BT_MAC = "98:D3:31:FC:43:A1";
//    final String DISPATCHER_URL = "http://micro-conf.xyz";

    EditText logText;
    EditText editTextDispatcherUrl;
    EditText editTextRoomName;
    Button connectBtn;
    Button disconnectBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logText = (EditText) findViewById(R.id.logText);
        editTextRoomName = (EditText) findViewById(R.id.editTextRoomName);
        editTextDispatcherUrl = (EditText) findViewById(R.id.editTextDispatcherUrl);

        connectBtn = (Button) findViewById(R.id.btnConnect);
        disconnectBtn = (Button) findViewById(R.id.btnDisconnect);

    }

    private void printMessageOnScreen(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logText.append(msg + "\n");
            }
        });
    }

    @Override
    public void onClick(View v) {
        Log.d("onClick", v.toString());

        if (v == connectBtn) {
            Intent intent = new Intent(this, ArduinoBridgeService.class);
            final String dispatcherUrl = editTextDispatcherUrl.getText().toString();
            final String roomName = editTextRoomName.getText().toString();
            intent.putExtra("dispatcherUrl", dispatcherUrl);
            intent.putExtra("roomName", roomName);
            startService(intent);
        } else if (v == disconnectBtn) {
            Intent intent = new Intent(this, ArduinoBridgeService.class);
            stopService(intent);
        }
    }
}
