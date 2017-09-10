package com.chaoslabgames.mars.bridge.remote.communicator;

import com.chaoslabgames.mars.bridge.remote.communicator.exceptions.BadReplyIdException;
import com.chaoslabgames.mars.bridge.remote.communicator.exceptions.CommunicationException;

import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by drykovanov on 24.08.2017.
 */

public class Communicator {
    private final MessageIdGenerator messageIdGenerator;
    private final RoboMessageSender sender;
    private final RoboReplyReader reader;

    public Communicator(MessageIdGenerator messageIdGenerator, RoboMessageSender sender, RoboReplyReader reader) {
        this.sender = sender;
        this.reader = reader;
        this.messageIdGenerator = messageIdGenerator;
    }

    public Object send(JSONObject msg) throws CommunicationException {
        final RoboMessage roboMessage = new RoboMessage();
        final String requestId = this.messageIdGenerator.next();
        roboMessage.requestId = requestId;
        roboMessage.params = msg;

        try {
            this.sender.send(roboMessage);
        } catch (IOException e) {
            throw new CommunicationException(e);
        }
        final RoboReply reply = reader.readReply();
        if (!requestId.equals(reply.replyOn)) {
            throw new BadReplyIdException();
        }

        return reply.params;
    }
}
