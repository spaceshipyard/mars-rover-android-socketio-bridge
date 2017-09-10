package com.chaoslabgames.mars.bridge.remote;

import com.chaoslabgames.mars.bridge.remote.communicator.RoboReply;
import com.chaoslabgames.mars.bridge.remote.communicator.RoboReplyReader;
import com.chaoslabgames.mars.bridge.remote.communicator.RoboReplyStatus;
import com.chaoslabgames.mars.bridge.remote.communicator.exceptions.CommunicationException;
import com.chaoslabgames.mars.bridge.remote.communicator.impl.RoboMsgJsonRpcReader;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.ByteArrayInputStream;

/**
 * Created by drykovanov on 10.09.2017.
 */


public class RoboMsgJsonRpcReplyReaderTest {

    @Test
    public void testReadCmdReply() throws CommunicationException, JSONException {
        //given
        final RoboReply expectedReply =
                new RoboReply(new JSONObject("{ \"sum\":3 }"), "reqId1", RoboReplyStatus.OK, "acknowledge");

        final char packageTerminator = '\n';
        final String inPackage =
                "{ \"cmd\": \"acknowledge\", \"params\": { \"sum\": 3 } ," +
                        " \"replyOn\": \"reqId1\" , \"replyStatus\": 0, \"replyDelay\": 1 }" +
                        packageTerminator;
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(inPackage.getBytes());

        final RoboReplyReader reader = new RoboMsgJsonRpcReader(inputStream, packageTerminator);
        //when
        final RoboReply actualReply = reader.readReply();

        //then
        Assert.assertEquals(actualReply, expectedReply);
    }
}
