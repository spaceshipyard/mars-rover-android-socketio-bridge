package com.chaoslabgames.mars.bridge.remote;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chaoslabgames.mars.bridge.remote.communicator.Communicator;
import com.chaoslabgames.mars.bridge.remote.communicator.MessageIdGenerator;
import com.chaoslabgames.mars.bridge.remote.communicator.impl.RoboMsgJsonRpcSender;
import com.chaoslabgames.mars.bridge.remote.communicator.impl.RoboMsgJsonRpcReader;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class CommunicatorInstrumentedTest {

    UUID id = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Test(timeout = 13000)
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        BluetoothAdapter.getDefaultAdapter().enable();
        BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> paired = bluetooth.getBondedDevices();
        BluetoothDevice bluetoothDevice = null;
        for (BluetoothDevice d : paired) {
            Log.d("test", d.getName());
            if (d.getName().contains("HC-06")) {
                bluetoothDevice = d;
            }
        }

        assertNotNull(bluetoothDevice);

        BluetoothSocket commSocket = bluetoothDevice.createRfcommSocketToServiceRecord(id);
        commSocket.connect();
        try {

        final char delimer = '\n';
        final OutputStream outputStream = commSocket.getOutputStream();
        final InputStream inputStream = commSocket.getInputStream();
//        commSocket.getOutputStream().write("2,1,1\n".getBytes());
            Communicator communicator = new Communicator(new MessageIdGenerator() {
                @Override
                public String next() {
                    return "1";
                }
            },
            new RoboMsgJsonRpcSender(outputStream, delimer),
            new RoboMsgJsonRpcReader(inputStream, delimer));

        final JSONObject params = new JSONObject();
        params.put("a", 1);
        params.put("b", "text");
        final JSONObject result = communicator.send("echo", params);

        assertEquals(1, result.getInt("a"));
        assertEquals("text", result.getString("b"));
        } finally {
            commSocket.close();
        }

    }
}
