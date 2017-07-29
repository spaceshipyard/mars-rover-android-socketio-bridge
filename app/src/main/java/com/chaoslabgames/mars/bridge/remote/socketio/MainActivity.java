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

    BluetoothSocket clientSocket;

    //final String BT_MAC = "98:D3:31:FC:43:A1";
//    final String DISPATCHER_URL = "http://micro-conf.xyz";

    EditText logText;
    EditText editTextMacAddr;
    EditText editTextDispatcherUrl;
    Button connectBtn;
    Button disconnectBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logText = (EditText) findViewById(R.id.logText);
        editTextMacAddr = (EditText) findViewById(R.id.editTextMacAddr);
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

    private void forwardCmd(String cmd) {
        try {
            OutputStream outStream = clientSocket.getOutputStream();
            outStream.write(cmd.getBytes());

        } catch (IOException e) {
            //Если есть ошибки, выводим их в лог
            Log.d("BLUETOOTH", e.getMessage());
            printMessageOnScreen("Error to send over BT cmd: " + cmd);
        }

    }

    private void initiateConnections() {
        //BT
        String enableBT = BluetoothAdapter.ACTION_REQUEST_ENABLE;
        startActivityForResult(new Intent(enableBT), 0);

        BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();

        final String btMac = editTextMacAddr.getText().toString();
        final String dispatcherUrl = editTextDispatcherUrl.getText().toString();
        try {
            BluetoothDevice device = bluetooth.getRemoteDevice(btMac);

            Method m = device.getClass().getMethod(
                    "createRfcommSocket", new Class[]{int.class});

            clientSocket = (BluetoothSocket) m.invoke(device, 1);
            clientSocket.connect();
            printMessageOnScreen("CONNECTED to BT " + btMac);
        } catch (Exception e) {
            Log.d("BLUETOOTH", e.getMessage());
            printMessageOnScreen("BLUETOOTH connection error " + e + " " + btMac);
        }

        //end BT

        try {
            final Socket socket = IO.socket(dispatcherUrl);

            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    printMessageOnScreen("socket io connected");
                }

            }).on("event", new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    JSONObject obj = (JSONObject) args[0];
                    Log.d("event", obj.toString());
                    printMessageOnScreen("event " + obj.toString());
                }

            }).on("message", new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    final JSONObject obj = (JSONObject) args[0];
                    Log.d("message", obj.toString());
                    try {
                        final String cmd = obj.get("cmd").toString();
                        forwardCmd(cmd);
                        printMessageOnScreen("in-cmd: " + cmd);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("socket", "fail to get cmd", e);
                    }

                }

            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    Log.d("disconnected", args.toString());
                    printMessageOnScreen("socket is disconnected");
                }

            }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {

                @Override
                public void call(final Object... args) {
                    Log.d("socket", "error " + args.toString());
                    printMessageOnScreen("socket error " + args.toString());
                }
            });
            socket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        Log.d("onClick", v.toString());

        if (v == connectBtn) {
            //initiateConnections();
            Intent intent = new Intent(this, ArduinoBridgeService.class);
            final String btMac = editTextMacAddr.getText().toString();
            final String dispatcherUrl = editTextDispatcherUrl.getText().toString();
            intent.putExtra("btMacAddress", btMac);
            intent.putExtra("dispatcherUrl", dispatcherUrl);
            startService(intent);
        } else if (v == disconnectBtn) {
            Intent intent = new Intent(this, ArduinoBridgeService.class);
            stopService(intent);
        }
    }
}
