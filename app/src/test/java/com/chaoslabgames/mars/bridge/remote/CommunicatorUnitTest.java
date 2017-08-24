package com.chaoslabgames.mars.bridge.remote;

import com.chaoslabgames.mars.bridge.remote.communicator.CommunicationException;
import com.chaoslabgames.mars.bridge.remote.communicator.Communicator;
import com.chaoslabgames.mars.bridge.remote.communicator.RoboMessage;
import com.chaoslabgames.mars.bridge.remote.communicator.RoboMessageSender;
import com.chaoslabgames.mars.bridge.remote.communicator.RoboReply;
import com.chaoslabgames.mars.bridge.remote.communicator.RoboReplyReader;
import com.chaoslabgames.mars.bridge.remote.communicator.RoboReplyStatus;
import com.chaoslabgames.mars.bridge.remote.communicator.UnexpectedReplyException;

import junit.framework.Assert;

import org.junit.Test;

import java.io.IOException;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CommunicatorUnitTest {

    final Object anyObject = new Object();
    final RoboMessageSender emptySender = new RoboMessageSender() {
        @Override
        public void send(RoboMessage msg) {
        }
    };

    @Test
    public void messageSendTest() throws Exception {
        //given

        Object message = "message ";
        final Object expectedReply = "expected reply";

        Communicator comm = new Communicator(emptySender, new RoboReplyReader() {
            @Override
            public RoboReply readReply() {
                return new RoboReply(expectedReply);
            }
        });

        //when
        Object actualReply = comm.send(message);

        //then
        Assert.assertEquals(expectedReply, actualReply);
    }

    @Test(expected = UnexpectedReplyException.class)
    public void sendAndGetUnexpectedResponse() throws CommunicationException {
        //given
        Communicator comm = new Communicator(emptySender, new RoboReplyReader() {
            @Override
            public RoboReply readReply() throws CommunicationException {
                throw new UnexpectedReplyException();
            }
        });

        //then throw
        comm.send(anyObject);
    }
}