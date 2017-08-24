package com.chaoslabgames.mars.bridge.remote;

import com.chaoslabgames.mars.bridge.remote.socketio.AbstractArduinoBridge;

import org.junit.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ArduinoBridgeTest {
    @Test
    public void connectionTest() throws Exception {
        //given
        //channel

        //when
        //connect

        //then
        //is connected
    }

    @Test
    public void disconnectionTest() {

    }

    @Test
    public void messageSendTest() {
        //given
        //message

        //when
        //send message

        //then
        //message is the channel
    }

    @Test
    public void messageWithAskTest() {
        //given
        TestOuputMessageChannel channel = new TestOuputMessageChannel();
        AbstractArduinoBridge bridge = new AbstractArduinoBridge(channel);
        String message = "";

        //when
        //send message
        bridge.sendMessage(message);

        //then
        //confirmation message is received
        assertEquals(channel.messages.get(0), message);
    }

    @Test
    public void reciveMessage() {
        //given
        //message
        AbstractArduinoBridge bridge = new AbstractArduinoBridge(null, );

        //when
        //send message
        //bridge.sendMessage();

        //then
        //is not delivered
    }
}

class TestOuputMessageChannel implements AbstractArduinoBridge.OuputMessageChannel {

    public List<Serializable> messages = new ArrayList<>();

    @Override
    public void pushMessage(Serializable message) {
        messages.add(message);
    }
}