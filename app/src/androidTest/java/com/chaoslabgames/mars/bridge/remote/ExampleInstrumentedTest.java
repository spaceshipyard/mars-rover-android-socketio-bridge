package com.chaoslabgames.mars.bridge.remote;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStream;
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
public class ExampleInstrumentedTest {

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


        commSocket.getOutputStream().write("2,1,1;".getBytes());
        InputStream reader = commSocket.getInputStream();
        String result = "";
        char c;
        while ((c = (char) reader.read()) != ';') {
            result += c;
            Log.d("test", "rest: " + c);
        }

        assertEquals(result, "3,2.00,0.00");
        } finally {
            commSocket.close();
        }

    }
}
