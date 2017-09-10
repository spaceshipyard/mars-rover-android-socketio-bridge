package com.chaoslabgames.mars.bridge.remote;

import com.chaoslabgames.mars.bridge.remote.communicator.RoboMessage;
import com.chaoslabgames.mars.bridge.remote.communicator.impl.RoboMsgJsonRpcSender;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by drykovanov on 08.09.2017.
 */
class TestObject {
    String a;
}

public class RoboMsgJsonRpcSenderTest {
    @Test
    public void testGenericMessage() throws IOException, JSONException {
        //given
        final char separator = '\n';
        final String expectedPackage = "{\"cmd\":\"test\",\"id\":\"req1\",\"params\":{\"a\":\"1\"}}\n";
        final JSONObject testObj = new JSONObject();
        testObj.put("a", "1");
        final RoboMessage msg = new RoboMessage();
        msg.cmd = "test";
        msg.params = testObj;
        msg.requestId = "req1";

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        RoboMsgJsonRpcSender sender = new RoboMsgJsonRpcSender(outputStream, separator);

        //when
        sender.send(msg);

        //then
        final String actualPackage = outputStream.toString();
        Assert.assertEquals(expectedPackage, actualPackage);
    }
}
