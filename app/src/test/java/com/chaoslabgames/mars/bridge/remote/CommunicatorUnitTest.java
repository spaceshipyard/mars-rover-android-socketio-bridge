package com.chaoslabgames.mars.bridge.remote;

import com.chaoslabgames.mars.bridge.remote.communicator.MessageIdGenerator;
import com.chaoslabgames.mars.bridge.remote.communicator.RoboReplyStatus;
import com.chaoslabgames.mars.bridge.remote.communicator.exceptions.BadReplyIdException;
import com.chaoslabgames.mars.bridge.remote.communicator.exceptions.BadReplyStatusException;
import com.chaoslabgames.mars.bridge.remote.communicator.exceptions.CommunicationException;
import com.chaoslabgames.mars.bridge.remote.communicator.Communicator;
import com.chaoslabgames.mars.bridge.remote.communicator.RoboMessage;
import com.chaoslabgames.mars.bridge.remote.communicator.RoboMessageSender;
import com.chaoslabgames.mars.bridge.remote.communicator.RoboReply;
import com.chaoslabgames.mars.bridge.remote.communicator.RoboReplyReader;
import com.chaoslabgames.mars.bridge.remote.communicator.exceptions.UnexpectedReplyException;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CommunicatorUnitTest {

    final String anyCmd = "anyCmd";
    final RoboReplyStatus okStatus = RoboReplyStatus.OK;
    final JSONObject anyObject = new JSONObject();
    final RoboMessageSender emptySender = new RoboMessageSender() {
        @Override
        public void send(RoboMessage msg) {
        }
    };
    private MessageIdGenerator newConstGenerator(final String constId) {
        return new MessageIdGenerator() {
            @Override
            public String next() {
                return constId;
            }
        };
    }

    @Test
    public void messageSendTest() throws Exception {
        //given

        JSONObject message = new JSONObject("{}");
        final JSONObject expectedReply = new JSONObject("{}");
        final String messageId = "msgId";
        final String messageCmd = "cmdId";

        Communicator comm = new Communicator(newConstGenerator(messageId), emptySender, new RoboReplyReader() {
            @Override
            public RoboReply readReply() {
                return new RoboReply(expectedReply, messageId, okStatus, anyCmd);
            }
        });

        //when
        Object actualReply = comm.send(messageCmd, message);

        //then
        Assert.assertEquals(expectedReply, actualReply);
    }

    @Test(expected = UnexpectedReplyException.class)
    public void sendAndGetUnexpectedResponse() throws CommunicationException {
        //given
        Communicator comm = new Communicator(newConstGenerator(anyObject.toString()), emptySender, new RoboReplyReader() {
            @Override
            public RoboReply readReply() throws CommunicationException {
                throw new UnexpectedReplyException();
            }
        });

        //then throw
        comm.send(anyCmd, anyObject);
    }

    @Test(expected = BadReplyIdException.class)
    public void handleReplyOnlyOnTargetMessage() throws CommunicationException {
        //given
        final String wrongMsgId = "non-msgId";
        final String correctMsgId = "msgId";
        final RoboReplyReader reader = new RoboReplyReader() {
            @Override
            public RoboReply readReply() throws CommunicationException {
                return new RoboReply(new JSONObject(), wrongMsgId, okStatus, "any");
            }
        };
        final Communicator communicator = new Communicator(newConstGenerator(correctMsgId), emptySender, reader);

        //when throw
        communicator.send(anyCmd, anyObject);
    }

    @Test(expected = BadReplyStatusException.class)
    public void handleBadReplyOnlyOnTargetMessage() throws CommunicationException {
        //given
        final String correctMsgId = "msgId";
        final RoboReplyReader reader = new RoboReplyReader() {
            @Override
            public RoboReply readReply() throws CommunicationException {
                return new RoboReply(new JSONObject(), correctMsgId, RoboReplyStatus.UNKNOWN_STATUS, "any");
            }
        };
        final Communicator communicator = new Communicator(newConstGenerator(correctMsgId), emptySender, reader);

        //when throw
        communicator.send(anyCmd, anyObject);
    }

}