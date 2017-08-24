package com.chaoslabgames.mars.bridge.remote.socketio;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by drykovanov on 21.08.2017.
 */

public class BluetoothCommunicator {

    BluetoothSocket clientSocket;
//    Output output;
//    Input input;

    Thread inThread;
    Thread outThread;
    Output<String> output;
    Timer pingTimer;

    volatile boolean isBtAlive = false;
    // SPP UUID сервиса
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public void connect(String btMac) {
        BluetoothAdapter.getDefaultAdapter().enable();
        BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();

        try {
            BluetoothDevice device = bluetooth.getRemoteDevice(btMac);

            clientSocket = device.createRfcommSocketToServiceRecord(MY_UUID);

            clientSocket.connect();

            output = new Output<>(clientSocket.getOutputStream());

            inThread = new Thread(output);
            outThread = new Thread(new Input(clientSocket.getInputStream()));


            inThread.start();
            outThread.start();

            pingTimer = new Timer();
            pingTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    output.putMessage("ping-platform:;");
                }
            }, 1000, 5000);

            Log.d("BLUETOOTH", "CONNECTED to BT " + btMac);
            isBtAlive = true;
        } catch (Exception e) {
            Log.d("BLUETOOTH", e.getMessage());
            Log.d("BLUETOOTH", "BLUETOOTH connection error " + e + " " + btMac);
            isBtAlive = false;
        }
    }

    ;

    public boolean isConnected() {
        return isBtAlive;
    }

    public void forwardToBT(String cmd) {
        try {
            output.putMessage(cmd);
        } catch (Exception e) {
            //Если есть ошибки, выводим их в лог
            isBtAlive = false;
            e.printStackTrace();
            Log.d("BLUETOOTH", e.getMessage());
        }
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                pingTimer.cancel();
                inThread.interrupt();
                outThread.interrupt();
                clientSocket.close();
            } catch (IOException e) {
                Log.e("BLUETOOTH", e.getMessage(), e);
            }
        }
    }

    static class Input implements Runnable {

        private DataInputStream inputReader;

        public Input(InputStream inputStream) throws IOException {
            this.inputReader = new DataInputStream(inputStream);
        }

        @Override
        public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()

            while (true) {

                if (Thread.currentThread().isInterrupted()) {
                    break;
                }

                try {
                    bytes = inputReader.read(buffer);
                    String inMessage = new String(buffer, 0, bytes);
                    Log.d("BLUETOOTH reader", inMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("BLUETOOTH reader", e.getMessage(), e);
                    break;
                }

            }
        }
    }

    static class Output<T> implements Runnable {
        private final BlockingQueue<T> messageQueue = new LinkedBlockingQueue(8);
        private final ObjectOutputStream outStream;

        public Output(OutputStream stream) throws IOException {
            outStream = new ObjectOutputStream(stream);
        }

        public void putMessage(T msg) {
            messageQueue.add(msg);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    T msg = messageQueue.take();
                    Log.d("out", "write out msg: " + msg);
                    outStream.write(msg.toString().getBytes());
                    outStream.flush();
                } catch (InterruptedException e) {
                    Log.d("Bluetooth", e.getMessage(), e);
                    break;
                } catch (IOException e) {
                    Log.e("Bluetooth", e.getMessage(), e);
                    break;
                }

                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
            }
        }
    }
}
