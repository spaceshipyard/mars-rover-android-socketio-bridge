package com.chaoslabgames.mars.bridge.remote.communicator.impl;

import com.chaoslabgames.mars.bridge.remote.communicator.RoboReply;
import com.chaoslabgames.mars.bridge.remote.communicator.RoboReplyReader;
import com.chaoslabgames.mars.bridge.remote.communicator.exceptions.CommunicationException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by drykovanov on 10.09.2017.
 */

public class RoboMsgJsonRpcWriter implements RoboReplyReader {
    private final BufferedReader reader;
    private final char packageTerminator;

    public RoboMsgJsonRpcWriter(final char packageTerminator, final ByteArrayInputStream inputStream) {
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
        this.packageTerminator = packageTerminator;
    }

    @Override
    public RoboReply readReply() throws CommunicationException {
        try {
            final String json = readNextPackage();
            final JSONObject replyJson = new JSONObject(json);
            final String cmd = replyJson.getString("cmd");
            final String replyOn = replyJson.getString("replyOn");
            final JSONObject params = replyJson.getJSONObject("params");

            return new RoboReply(params, replyOn, cmd);
        } catch (JSONException e) {
            throw new CommunicationException(e);
        }
    }

    public String readNextPackage() throws CommunicationException {
        StringBuilder sb = new StringBuilder();
        try {
            int r;
            while ((r = reader.read()) != -1) {
                char c = (char) r;
                if (c == this.packageTerminator)
                    break;
                sb.append(c);
            }
        } catch (IOException e) {
            throw new CommunicationException(e);
        }
        return sb.toString();
    }

}
