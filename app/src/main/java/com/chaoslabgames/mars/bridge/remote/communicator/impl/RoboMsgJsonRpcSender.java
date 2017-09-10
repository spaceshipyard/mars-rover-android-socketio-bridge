package com.chaoslabgames.mars.bridge.remote.communicator.impl;

import com.chaoslabgames.mars.bridge.remote.communicator.RoboMessage;
import com.chaoslabgames.mars.bridge.remote.communicator.RoboMessageSender;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by drykovanov on 08.09.2017.
 */

public class RoboMsgJsonRpcSender implements RoboMessageSender {
    final OutputStream outputStream;
    final char packageTerminator;

    public RoboMsgJsonRpcSender(final OutputStream outputStream, final char delimer) {
        this.outputStream = outputStream;
        this.packageTerminator = delimer;
    }

    @Override
    public void send(final RoboMessage msg) throws IOException {
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", msg.cmd);
            object.put("id", msg.requestId);
            object.put("params", msg.params);
        } catch (JSONException e) {
            throw new IOException(e);
        }
        final byte[] packageBytes = (object.toString() + this.packageTerminator).getBytes();
        outputStream.write(packageBytes);
    }
}
